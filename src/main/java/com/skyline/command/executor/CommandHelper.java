package com.skyline.command.executor;

import com.skyline.command.exception.CommandExecutionException;
import com.skyline.command.main.Logger;
import com.skyline.command.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [FEATURE INFO]<br/>
 * 帮助指令执行器, 会由 CommandBuilder 自动注册到每一个 ExecutionCommandNode 节点上<br/>
 * 会遍历 ExecutionCommandNode 下的每一个节点,<br/>
 * 如果为注册了 Executor 的节点或者为 尾节点(尾节点一定是有 Executor 的), 就为其输出一个指令使用格式<br/>
 *
 * @author Skyline
 * @create 2022-10-19 17:13
 * @since 1.0
 */
public class CommandHelper implements CommandExecutor {

    /**
     * option 类型节点帮助指令使用的左括号
     */
    public static final String OPT_QUOTE_LEFT = "[";
    /**
     * option 类型节点帮助指令使用的右括号
     */
    public static final String OPT_QUOTE_RIGHT = "]";
    /**
     * argument 类型节点帮助指令使用的左括号
     */
    public static final String ARG_QUOTE_LEFT = "<";
    /**
     * argument 类型节点帮助指令使用的右括号
     */
    public static final String ARG_QUOTE_RIGHT = ">";
    /**
     * 被注册的到的节点引用, 节点为 ExecutionCommandNode
     */
    private final CommandNode commandNode;

    public CommandHelper(CommandNode commandNode) {
        this.commandNode = commandNode;
    }

    /**
     * 指令执行方法, 在指令注册时被定义, 由 CommandDispatcher 进行调用<br/>
     *
     * @param args 方法需要传入的参数
     * @throws Exception 执行时抛出的异常
     */
    @Override
    public void execute(Object... args) throws Exception {
        if (commandNode == null) {
            throw new CommandExecutionException("Command node could not be null.");
        }
        Logger.log("Format: exe act sub-act [opt] <arg>");

        List<String> helpCommands = new ArrayList<>();
        listAllCommand(commandNode, new StringBuilder(), helpCommands);
        helpCommands.forEach(Logger::log);
    }

    /**
     * 递归获取当前节点的指令部分字符串, 将其加入 helpStr 中, 用于构建一个完整的可执行指令字符串<br/>
     * 同时判断当前节点是否为注册了 Executor 的节点, 如果是, 就表示当前节点是一个可执行的指令节点,<br/>
     * 将对应的指令使用格式(helpStr) 加入到 helpCommands 集合中<br/>
     * 当当前节点为尾节点或当前节点不存在时停止递归查找<br/>
     *
     * @param commandNode 当前递归遍历到的指令节点
     * @param helpStr 由 ExecutionCommandNode 到当前节点的父节点构建的指令部分字符串
     * @param helpCommands 所有可执行指令的 helpStr 集合
     */
    private void listAllCommand(CommandNode commandNode, StringBuilder helpStr, List<String> helpCommands) {
        // 当前节点为空, 将 helpStr 加入 list, 返回(一般不会出现此类情况)
        if (commandNode == null) {
            helpCommands.add(helpStr.toString());
            return;
        }

        // 根据节点类型, 将各自部分的 名称和前后缀加入 helpStr
        if (commandNode instanceof ExecutionCommandNode) {
            helpStr.append(commandNode.getName());
        } else if (commandNode instanceof ActionCommandNode) {
            helpStr.append(" ").append(commandNode.getName());
        } else if (commandNode instanceof OptionCommandNode) {
            helpStr.append(" ").append(OPT_QUOTE_LEFT).append(commandNode.getName()).append(OPT_QUOTE_RIGHT);
        } else if (commandNode instanceof ArgumentCommandNode) {
            helpStr.append(" ").append(ARG_QUOTE_LEFT).append(commandNode.getName()).append(ARG_QUOTE_RIGHT);
        }

        // 如果当前节点没有后继节点了, 就将 helpStr 加入 list, 返回(一般来说尾节点都是可执行的指令节点)
        if (commandNode.getChildren().isEmpty()) {
            helpCommands.add(helpStr.toString() + "  " + commandNode.getUsage());
            return;
        }

        // 当前节点不是尾节点, 但是注册有指令执行器, 同样加入 helpStr
        if (commandNode.getCommandExecutor() != null) {
            helpCommands.add(helpStr.toString() + "  " + commandNode.getUsage());
        }

        String str = helpStr.toString();
        ConcurrentHashMap<String, CommandNode> children = commandNode.getChildren();
        // 遍历每一个子节点, 继续向下搜索, 更新 helpStr
        for (CommandNode node : children.values()) {
            listAllCommand(node, new StringBuilder(str), helpCommands);
        }
    }

    public CommandNode getCommandNode() {
        return commandNode;
    }
}
