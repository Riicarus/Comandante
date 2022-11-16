package com.riicarus.comandante.tree;

import com.riicarus.comandante.exception.CommandBuildException;
import com.riicarus.comandante.executor.CommandExecutor;

import java.util.HashMap;

/**
 * [FEATURE INFO]<br/>
 * 指令树抽象节点
 *
 * @author Skyline
 * @create 2022-11-16 16:45
 * @since 1.0.0
 */
public abstract class AbstractNode {

    /**
     * 指令节点名称, 和对应指令部分相同
     */
    private final String name;
    /**
     * 节点在指令树中的子节点
     */
    private final HashMap<String, AbstractNode> children = new HashMap<>();
    /**
     * 节点在指令树中的子 ExecutionNode 节点
     */
    private final HashMap<String, ExecutionNode> executions;
    /**
     * 节点在指令树中的子 OptionNode 节点
     */
    private final HashMap<String, OptionNode> options;
    /**
     * 节点在指令树中的子 ArgumentNode 节点链表
     */
    private ArgumentNode<?> nextArgument;
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
                        HashMap<String, OptionNode> options) {
        this.name = name;
        this.executions = executions;
        this.options = options;
    }

    public void addChild(AbstractNode node) {
        if (!getChildren().containsKey(node.getName())) {
            getChildren().put(node.getName(), node);
        }
    }

    public ExecutionNode addExecution(ExecutionNode node) {
        // 允许添加 ExecutionNode 节点并且不存在对应名称的节点
        if (getExecutions() != null && !getExecutions().containsKey(node.getName())) {
            getExecutions().put(node.getName(), node);
        }

        if (getExecutions() != null) {
            return getExecutions().get(node.getName());
        }

        throw new CommandBuildException("ExecutionNode[" + name + "] add failed.");
    }

    public OptionNode addOption(OptionNode node) {
        // 允许添加 ExecutionNode 节点并且不存在对应名称的节点
        if (getOptions() != null && !getOptions().containsKey(node.getName())) {
            getOptions().put(node.getName(), node);
        }

        if (getOptions() != null) {
            return getOptions().get(node.getName());
        }

        throw new CommandBuildException("OptionNode[" + name + "] add failed.");
    }

    public ArgumentNode<?> setNextArgument(ArgumentNode<?> node) {
        // 允许添加 ExecutionNode 节点并且当前没有被注册
        if (!(this instanceof RootNode) && getNextArgument() == null) {
            nextArgument = node;
        }

        if (getNextArgument() != null) {
            return getNextArgument();
        }

        throw new CommandBuildException("ArgumentNode[" + name + "] add failed.");
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public void setCommandExecutor(CommandExecutor commandExecutor) {
        setCommandExecutor(commandExecutor, false);
    }

    public void setCommandExecutor(CommandExecutor commandExecutor, boolean cover) {
        if (this.commandExecutor != null && !cover) {
            return;
        }

        this.commandExecutor = commandExecutor;
    }

    public String getName() {
        return name;
    }

    public String getUsage() {
        return usage;
    }

    public HashMap<String, AbstractNode> getChildren() {
        return children;
    }

    public HashMap<String, ExecutionNode> getExecutions() {
        return executions;
    }

    public HashMap<String, OptionNode> getOptions() {
        return options;
    }

    public ArgumentNode<?> getNextArgument() {
        return nextArgument;
    }

    public CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }
}
