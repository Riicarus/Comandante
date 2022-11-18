package com.riicarus.comandante.executor;

import com.riicarus.comandante.exception.CommandExecutionException;
import com.riicarus.comandante.main.CommandLogger;
import com.riicarus.comandante.manage.CommandContext;
import com.riicarus.comandante.manage.CommandDispatcher;
import com.riicarus.comandante.tree.*;

import java.util.*;

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
     * argument 节点参数类型帮助指令使用的左括号
     */
    public static final String ARG_TYPE_QUOTE_LEFT = "(";
    /**
     * argument 节点参数类型帮助指令使用的右括号
     */
    public static final String ARG_TYPE_QUOTE_RIGHT = ")";
    /**
     * 长短指令提示的分隔符
     */
    public static final String LONG_SHORT_OPTION_SEPARATOR = "/";
    /**
     * 被注册的到的节点引用, 一定为 MainExecutionNode, 注册在 RootNode 下
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
        String commandFormat =
                "Format: " +
                "exe" + CommandDispatcher.COMMAND_PART_SEPARATOR +
                CommandDispatcher.LONG_OPTION_PREFIX_STRING + "opt" + LONG_SHORT_OPTION_SEPARATOR +
                CommandDispatcher.SHORT_OPTION_PREFIX_STRING + "o" + CommandDispatcher.COMMAND_PART_SEPARATOR +
                "arg";
        CommandLogger.log(commandFormat);
        List<String> helpMessages = new ArrayList<>();
        findThroughMainTree(node, "", helpMessages);

        helpMessages.forEach(CommandLogger::log);
    }

    /**
     * 递归遍历指令树, 获取当前节点的指令部分字符串, 将其加入 helpMessage 中, 用于构建一个完整的可执行指令字符串<br/>
     * 同时判断当前节点是否为注册了 Executor 的节点, 如果是, 就表示当前节点是一个可执行的指令节点,<br/>
     * 将对应的指令使用格式(helpMessage)加入到 helpMessages 集合中<br/>
     * 当当前节点为尾节点时停止递归查找<br/>
     *
     * @param currentNode 当前递归遍历到的指令节点
     * @param helpMessage 由 ExecutionCommandNode 到当前节点的父节点构建的指令部分字符串
     * @param helpMessages 所有可执行指令的 helpMessages 集合
     */
    private void findThroughMainTree(AbstractNode currentNode, String helpMessage, List<String> helpMessages) {
        // 处理当前节点的 help message
        String name = currentNode.getName();
        StringBuilder currentHelpMessage = new StringBuilder(helpMessage);
        currentHelpMessage.append(name);
        // 处理参数节点的提示
        if (currentNode instanceof ArgumentNode) {
            String typeName = ((ArgumentNode<?>) currentNode).getType().getTypeName();
            currentHelpMessage.append(ARG_TYPE_QUOTE_LEFT).append(typeName).append(ARG_TYPE_QUOTE_RIGHT);
        }
        currentHelpMessage.append(CommandDispatcher.COMMAND_PART_SEPARATOR);

        // 如果当前节点注册了 CommandExecutor, append 当前执行器的用途
        if (currentNode.getCommandExecutor() != null) {
            currentHelpMessage.append(currentNode.getUsage());
            helpMessages.add(currentHelpMessage.toString());
        }

        // 处理当前的节点的 OptionNode 子节点, ArgumentNode 的 options 集合为 null
        if (currentNode.getOptions() != null && !currentNode.getOptions().isEmpty()) {
            Collection<OptionNode> options = currentNode.getOptions().values();
            options.forEach(node -> listHelpOfOption(node, currentHelpMessage.toString(), helpMessages));
        }

        // 指令树主干节点为 ExecutionNode 或 ArgumentNode
        HashSet<AbstractNode> children = new HashSet<>();
        // 只会有一个 ArgumentNode
        ArgumentNode<?> argumentNode = currentNode.getArgument(ArgumentNode.EXECUTION_ARGUMENT_NAME);
        if (argumentNode != null) {
            children.add(argumentNode);
        }
        if (!currentNode.getExecutions().isEmpty()) {
            children.addAll(currentNode.getExecutions().values());
        }

        // 如果没有后续节点了, 就停止遍历
        if (children.isEmpty()) {
            return;
        }

        // 遍历主干节点上的所有子节点
        for (AbstractNode child : children) {
            findThroughMainTree(child, currentHelpMessage.toString(), helpMessages);
        }
    }

    /**
     * 处理 OptionNode 对应的 help message<br/>
     *
     * @param currentNode 当前 OptionNode
     * @param helpMessage 帮助信息
     * @param helpMessages 帮助信息列表
     */
    private void listHelpOfOption(OptionNode currentNode, String helpMessage, List<String> helpMessages) {
        String name = currentNode.getName();
        StringBuilder currentHelpMessage = new StringBuilder(helpMessage);
        currentHelpMessage.append(CommandDispatcher.LONG_OPTION_PREFIX_STRING).append(name);

        String alias = currentNode.getAlias();
        if (alias != null && !alias.trim().isEmpty()) {
            currentHelpMessage.append(LONG_SHORT_OPTION_SEPARATOR).append(alias);
        }
        currentHelpMessage.append(CommandDispatcher.COMMAND_PART_SEPARATOR);

        // 如果没有参数
        if (!currentNode.requireArg()) {
            currentHelpMessage.append(currentNode.getUsage());
            helpMessages.add(currentHelpMessage.toString());
            return;
        }

        // 处理参数节点
        ArgumentNode<?> argumentNode = currentNode.getArgument(ArgumentNode.OPTION_ARGUMENT_NAME);
        listHelpOfOptionArgument(argumentNode, currentHelpMessage.toString(), helpMessages);
    }

    /**
     * 处理 ArgumentNode 对应的 help message<br/>
     * 从 ArgumentNode 开始循环直到没有节点为止
     *
     * @param currentNode 当前 ArgumentNode
     * @param helpMessage 帮助信息
     * @param helpMessages 帮助信息列表
     */
    private void listHelpOfOptionArgument(ArgumentNode<?> currentNode, String helpMessage, List<String> helpMessages) {
        String name = currentNode.getName();
        String typeName = currentNode.getType().getTypeName();
        StringBuilder currentHelpMessage = new StringBuilder(helpMessage);
        currentHelpMessage.append(name).append(ARG_TYPE_QUOTE_LEFT).append(typeName).append(ARG_TYPE_QUOTE_RIGHT).append(CommandDispatcher.COMMAND_PART_SEPARATOR);

        ArgumentNode<?> nextNode = currentNode.getArgument(ArgumentNode.OPTION_ARGUMENT_NAME);
        if (nextNode == null) {
            currentHelpMessage.append(currentNode.getUsage());
            helpMessages.add(currentHelpMessage.toString());
            return;
        }

        // 处理后续参数节点
        listHelpOfOptionArgument(nextNode, currentHelpMessage.toString(), helpMessages);
    }

    public AbstractNode getNode() {
        return node;
    }
}
