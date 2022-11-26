package com.riicarus.comandante.tree;

import com.riicarus.comandante.exception.CommandBuildException;
import com.riicarus.comandante.executor.CommandExecutor;
import com.riicarus.util.asserts.Asserts;

import java.util.HashMap;

/**
 * [FEATURE INFO]<br/>
 * 指令树抽象节点
 *
 * @author Riicarus
 * @create 2022-11-16 16:45
 * @since 1.0.0
 */
public abstract class AbstractNode {

    /**
     * 指令节点名称, 和对应指令部分相同
     */
    private final String name;
    /**
     * 节点在指令树中的子 ExecutionNode 节点
     */
    private final HashMap<String, ExecutionNode> executions;
    /**
     * 节点在指令树中的子 OptionNode 节点
     */
    private final HashMap<String, OptionNode> options;
    /**
     * 节点在指令树中的子 ArgumentNode 节点
     */
    private final HashMap<String, ArgumentNode<?>> arguments;
    /**
     * 当前节点的前一个节点
     */
    private final AbstractNode previousNode;
    /**
     * 注册在该节点的指令执行器的用途
     */
    private String usage = "";
    /**
     * 注册在该节点的指令执行器
     */
    private CommandExecutor commandExecutor;

    public AbstractNode(String name,
                        HashMap<String, ExecutionNode> executions,
                        HashMap<String, OptionNode> options,
                        HashMap<String, ArgumentNode<?>> arguments,
                        AbstractNode previousNode) {
        this.name = name;
        this.executions = executions;
        this.options = options;
        this.arguments = arguments;
        this.previousNode = previousNode;
    }

    public ExecutionNode addExecution(ExecutionNode node) throws CommandBuildException {
        Asserts.notNull(node,
                new CommandBuildException("ExecutionNode behind Node[" + this.getName() + "] add failed: input node is null."));

        // 允许添加 ExecutionNode 节点并且不存在对应名称的节点
        Asserts.notNull(getExecutions(),
                new CommandBuildException("ExecutionNode[" + node.getName() + "] behind Node[" + this.getName() + "] add failed: " +
                "Node[" + this.getName() + "] can not add ExceptionNode"));

        if (!getExecutions().containsKey(node.getName())) {
            getExecutions().put(node.getName(), node);
        }

        ExecutionNode execution = getExecution(node.getName());

        Asserts.notNull(execution, new CommandBuildException("ExecutionNode[" + name + "] add failed."));
        return execution;
    }

    public OptionNode addOption(OptionNode node) throws CommandBuildException {
        Asserts.notNull(node,
                new CommandBuildException("OptionNode behind Node[" + this.getName() + "] add failed: input node is null."));

        // 允许添加 OptionNode 节点并且不存在对应名称的节点
        Asserts.notNull(getOptions(),
                new CommandBuildException("OptionNode[" + node.getName() + "] behind Node[" + this.getName() + "] add failed: " +
                        "Node[" + this.getName() + "] can not add OptionNode"));

        if (!getOptions().containsKey(node.getName())) {
            getOptions().put(node.getName(), node);
        }

        // 返回当前在指令树中对应的节点(可能该节点没有被添加进去)
        OptionNode optionNode = getOption(node.getName());

        Asserts.notNull(optionNode, new CommandBuildException("OptionNode[" + name + "] add failed."));
        return optionNode;
    }

    public ArgumentNode<?> addArgument(ArgumentNode<?> node) throws CommandBuildException {
        Asserts.notNull(node,
                new CommandBuildException("ArgumentNode behind Node[" + this.getName() + "] add failed: input node is null."));

        // 如果是属于 OptionNode 的参数节点, 就将注册的名称改为 ArgumentNode.OPTION_ARGUMENT_NAME, 便于在 CommandDispatcher 中获取对应节点
        String nodeName = node.isOptionArg() ? ArgumentNode.OPTION_ARGUMENT_NAME : ArgumentNode.EXECUTION_ARGUMENT_NAME;

        // 允许添加 ArgumentNode 节点并且当前没有被注册
        Asserts.notNull(getArguments(),
                new CommandBuildException("ArgumentNode[" + node.getName() + "] behind Node[" + this.getName() + "] add failed: " +
                        "Node[" + this.getName() + "] can not add ArgumentNode"));

        if (!getArguments().containsKey(nodeName)) {
            getArguments().put(nodeName, node);
        }

        ArgumentNode<?> argumentNode = getArgument(nodeName);

        Asserts.notNull(argumentNode, new CommandBuildException("ArgumentNode[" + name + "] add failed."));
        return argumentNode;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    /**
     * 设置指令执行器
     *
     * @param commandExecutor 指令执行器
     * @return 是否设置成功
     */
    public boolean setCommandExecutor(CommandExecutor commandExecutor) {
        if (this.commandExecutor == null) {
            this.commandExecutor = commandExecutor;
            return true;
        }

        return false;
    }

    public String getName() {
        return name;
    }

    public String getUsage() {
        return usage;
    }

    public HashMap<String, ExecutionNode> getExecutions() {
        return executions;
    }

    public HashMap<String, OptionNode> getOptions() {
        return options;
    }

    public HashMap<String, ArgumentNode<?>> getArguments() {
        return arguments;
    }

    public AbstractNode getPreviousNode() {
        return previousNode;
    }

    public CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    public ExecutionNode getExecution(String name) {
        return getExecutions() == null ? null : getExecutions().get(name);
    }

    public OptionNode getOption(String name) {
        HashMap<String, OptionNode> options = getOptions();
        if (options == null || options.isEmpty()) {
            return null;
        }

        OptionNode optionNode;
        if ((optionNode = options.get(name)) == null) {
            for (OptionNode node : options.values()) {
                if (name.equals(node.getAlias())) {
                    optionNode = node;
                    break;
                }
            }
        }

        return optionNode;
    }

    public ArgumentNode<?> getArgument(String name) {
        return getArguments() == null ? null : getArguments().get(name);
    }
}
