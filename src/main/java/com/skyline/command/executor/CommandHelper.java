package com.skyline.command.executor;

import com.skyline.command.exception.CommandExecutionException;
import com.skyline.command.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [FEATURE INFO]<br/>
 * 帮助指令执行器
 *
 * @author Skyline
 * @create 2022-10-19 17:13
 * @since 1.0.0
 */
public class CommandHelper implements CommandExecutor {

    public static final String OPT_QUOTE_LEFT = "[";
    public static final String OPT_QUOTE_RIGHT = "]";
    public static final String ARG_QUOTE_LEFT = "<";
    public static final String ARG_QUOTE_RIGHT = ">";
    private final CommandNode commandNode;

    public CommandHelper(CommandNode commandNode) {
        this.commandNode = commandNode;
    }

    @Override
    public void execute(Object... args) throws Exception {
        if (commandNode == null) {
            throw new CommandExecutionException("Command node could not be null.");
        }
        System.out.println("Format: exe act sub-act [opt] <arg>");

        List<String> helpCommands = new ArrayList<>();
        listAllCommand(commandNode, new StringBuilder(), helpCommands);
        helpCommands.forEach(System.out::println);
    }

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
