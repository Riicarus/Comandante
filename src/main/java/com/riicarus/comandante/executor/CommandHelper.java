package com.riicarus.comandante.executor;

import com.riicarus.comandante.exception.CommandExecutionException;
import com.riicarus.comandante.main.Logger;
import com.riicarus.comandante.manage.CommandContext;
import com.riicarus.comandante.tree.*;

import java.util.List;

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
     * option 节点帮助指令使用的左括号
     */
    public static final String OPT_QUOTE_LEFT = "[";
    /**
     * option 节点帮助指令使用的右括号
     */
    public static final String OPT_QUOTE_RIGHT = "]";
    /**
     * argument 节点帮助指令使用的左括号
     */
    public static final String ARG_QUOTE_LEFT = "<";
    /**
     * argument 节点帮助指令使用的右括号
     */
    public static final String ARG_QUOTE_RIGHT = ">";
    /**
     * 被注册的到的节点引用, 节点为 ExecutionCommandNode
     */
    private final AbstractNode node;

    public CommandHelper(AbstractNode node) {
        this.node = node;
    }

    /**
     * 指令执行方法, 在指令注册时被定义, 由 CommandDispatcher 进行调用<br/>
     *
     * @param context 指令上下文
     * @throws Exception 执行时抛出的异常
     */
    @Override
    public void execute(CommandContext context) throws Exception {
        if (node == null) {
            throw new CommandExecutionException("Command node could not be null.");
        }
        Logger.log("Format: exe act sub-act [opt] <arg>");
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
    private void listAllCommand(AbstractNode commandNode, StringBuilder helpStr, List<String> helpCommands) {
    }

    public AbstractNode getNode() {
        return node;
    }
}
