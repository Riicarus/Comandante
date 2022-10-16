package com.skyline.command.tree;

import com.skyline.command.exception.CommandBuildException;
import com.skyline.command.executor.CommandExecutor;

import java.util.HashMap;

/**
 * [FEATURE INFO]<br/>
 * 指令节点抽象类
 *
 * @author Skyline
 * @create 2022-10-14 23:42
 * @since 1.0.0
 */
public abstract class CommandNode {

    private final String name;

    private final HashMap<String, CommandNode> children = new HashMap<>();

    private final HashMap<String, ExecutionCommandNode> executions;

    private final HashMap<String, ActionCommandNode> actions;

    private final HashMap<String, ActionCommandNode> subActions;

    private final HashMap<String, OptionCommandNode> options;

    private final HashMap<String, ArgumentCommandNode<?>> arguments;

    private CommandExecutor commandExecutor;

    public CommandNode(final String name,
                       HashMap<String, ExecutionCommandNode> executions,
                       HashMap<String, ActionCommandNode> actions,
                       HashMap<String, ActionCommandNode> subActions,
                       HashMap<String, OptionCommandNode> options,
                       HashMap<String, ArgumentCommandNode<?>> arguments,
                       CommandExecutor commandExecutor) {
        this.name = name;
        this.executions = executions;
        this.actions = actions;
        this.subActions = subActions;
        this.options = options;
        this.arguments = arguments;
        this.commandExecutor = commandExecutor;
    }

    public final void addChildNode(CommandNode node) {
        if (node != null && !node.getName().trim().isEmpty()) {
            if (!getChildren().containsKey(node.getName())) {
                getChildren().put(node.getName(), node);
            }
        } else {
            throw new CommandBuildException("Node not fit. Name: " + (node == null ? "" : node.getName()), null);
        }

    }

    public final void addExecutionNode(ExecutionCommandNode node) {
        if (getExecutions() != null &&
                node != null &&
                !node.getName().trim().isEmpty()) {
            if (!getExecutions().containsKey(node.getName())) {
                getExecutions().put(node.getName(), node);
            }
        } else {
            throw new CommandBuildException("Node not fit. Name: " + (node == null ? "" : node.getName()), null);
        }
    }

    public final void addActionNode(ActionCommandNode node) {
        if (getActions() != null &&
                node != null &&
                !node.getName().trim().isEmpty() &&
                !node.isSubAction()) {
            if (!getActions().containsKey(node.getName())) {
                getActions().put(node.getName(), node);
            }
        } else {
            throw new CommandBuildException("Node not fit. Name: " + (node == null ? "" : node.getName()), null);
        }
    }

    public final void addSubActionNode(ActionCommandNode node) {
        if (getSubActions() != null &&
                node != null &&
                !node.getName().trim().isEmpty() &&
                node.isSubAction()) {
            if (!getSubActions().containsKey(node.getName())) {
                getSubActions().put(node.getName(), node);
            }
        } else {
            throw new CommandBuildException("Node not fit. Name: " + (node == null ? "" : node.getName()), null);
        }
    }

    public final void addOptionNode(OptionCommandNode node) {
        if (getOptions() != null &&
                node != null &&
                !node.getName().trim().isEmpty()) {
            if (!getOptions().containsKey(node.getName())) {
                getOptions().put(node.getName(), node);
            }
        } else {
            throw new CommandBuildException("Node not fit. Name: " + (node == null ? "" : node.getName()), null);
        }
    }

    public final void addArgumentNode(ArgumentCommandNode<?> node) {
        if (getArguments() != null &&
                node != null &&
                !node.getName().trim().isEmpty()) {
            if (!getArguments().containsKey(node.getName())) {
                getArguments().put(node.getName(), node);
            }
        } else {
            throw new CommandBuildException("Node not fit. Name: " + (node == null ? "" : node.getName()), null);
        }
    }

    public String getName() {
        return name;
    }

    public final HashMap<String, CommandNode> getChildren() {
        return children;
    }

    public final HashMap<String, ExecutionCommandNode> getExecutions() {
        if (this instanceof RootCommandNode) {
            return executions;
        }

        return null;
    }

    public final HashMap<String, ActionCommandNode> getActions() {
        if (this instanceof ExecutionCommandNode) {
            return actions;
        }

        return null;
    }

    public abstract HashMap<String, ActionCommandNode> getSubActions();

    protected final HashMap<String, ActionCommandNode> doGetSubActions() {
        if (this instanceof ActionCommandNode) {
            return subActions;
        }

        return null;
    }

    public final HashMap<String, OptionCommandNode> getOptions() {
        if (this instanceof ExecutionCommandNode ||
                this instanceof ActionCommandNode ||
                this instanceof OptionCommandNode ||
                this instanceof ArgumentCommandNode) {
            return options;
        }

        return null;
    }

    public final HashMap<String, ArgumentCommandNode<?>> getArguments() {
        if (this instanceof ActionCommandNode ||
                this instanceof OptionCommandNode ||
                this instanceof ArgumentCommandNode) {
            return arguments;
        }

        return null;
    }

    public CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    public void setCommandExecutor(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }
}
