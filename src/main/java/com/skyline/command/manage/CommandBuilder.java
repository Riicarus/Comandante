package com.skyline.command.manage;

import com.skyline.command.argument.CommandArgumentType;
import com.skyline.command.exception.CommandBuildException;
import com.skyline.command.executor.CommandExecutor;
import com.skyline.command.executor.CommandHelper;
import com.skyline.command.tree.*;

/**
 * [FEATURE INFO]<br/>
 * 指令构建器
 *
 * @author Skyline
 * @create 2022-10-15 0:23
 * @since 1.0.0
 */
public class CommandBuilder {

    private final RootCommandNode rootCommandNode;

    private CommandNode currentNode;

    private CommandExecutor commandExecutor;

    public CommandBuilder(final RootCommandNode rootCommandNode) {
        this.rootCommandNode = rootCommandNode;
        this.currentNode = rootCommandNode;
    }

    public CommandBuilder(final RootCommandNode rootCommandNode,
                          final CommandExecutor commandExecutor) {
        this.rootCommandNode = rootCommandNode;
        this.currentNode = rootCommandNode;
        this.commandExecutor = commandExecutor;
    }

    public CommandBuilder execution(String name) {
        if (currentNode instanceof RootCommandNode) {
            ExecutionCommandNode executionCommandNode = new ExecutionCommandNode(name);
            currentNode.addChildNode(executionCommandNode);
            currentNode.addExecutionNode(executionCommandNode);

            if (currentNode.getExecutions() != null) {
                currentNode = currentNode.getExecutions().get(name);

                // 添加 --help/-h 指令支持
                OptionCommandNode helpOptionNode = new OptionCommandNode(null, "help", "h");
                helpOptionNode.setCommandExecutor(new CommandHelper(currentNode));
                currentNode.addChildNode(helpOptionNode);
                currentNode.addOptionNode(helpOptionNode);

                return this;
            }

            throw new CommandBuildException("Current node may be wrong when adding exe-node: " + name, null);
        }

        throw new CommandBuildException("ExecutionNode can only follow behind RootNode", null);
    }

    public CommandBuilder action(String name) {
        //考虑: 需不需要有多个 action, 按理说是不需要的
        if (currentNode instanceof ExecutionCommandNode) {
            ActionCommandNode actionCommandNode =
                    new ActionCommandNode(null, name, false);

            currentNode.addChildNode(actionCommandNode);
            currentNode.addActionNode(actionCommandNode);

            if (currentNode.getActions() != null) {
                currentNode = currentNode.getActions().get(name);
                return this;
            }
            throw new CommandBuildException("Current node may be wrong when adding act-node: " + name, null);
        }

        throw new CommandBuildException("ActionNode can only follow behind ExecutionNode", null);
    }

    public CommandBuilder subAction(String name) {
        if (currentNode instanceof ActionCommandNode) {
            ActionCommandNode actionCommandNode =
                    new ActionCommandNode(null, name, true);

            currentNode.addChildNode(actionCommandNode);
            currentNode.addSubActionNode(actionCommandNode);

            if (currentNode.getSubActions() != null) {
                currentNode = currentNode.getSubActions().get(name);
                return this;
            }
            throw new CommandBuildException("Current node may be wrong when adding sub-act-node: " + name, null);
        }

        throw new CommandBuildException("SubActionNode can only follow behind ActionNode", null);
    }

    public CommandBuilder option(String name, String alias) {
        if (currentNode instanceof ActionCommandNode ||
                currentNode instanceof ExecutionCommandNode ||
                currentNode instanceof OptionCommandNode ||
                currentNode instanceof ArgumentCommandNode) {
            OptionCommandNode optionCommandNode =
                    new OptionCommandNode(null, name, alias);

            currentNode.addChildNode(optionCommandNode);
            currentNode.addOptionNode(optionCommandNode);

            if (currentNode.getOptions() != null) {
                currentNode = currentNode.getOptions().get(name);
                return this;
            }
            throw new CommandBuildException("Current node may be wrong when adding opt-node: " + name, null);
        }

        throw new CommandBuildException("OptionNode can only follow behind (Sub)ActionNode or ExecutionNode or ArgumentNode", null);
    }

    public <T> CommandBuilder argument(String name, CommandArgumentType<T> type) {
        if (currentNode instanceof OptionCommandNode) {
            ArgumentCommandNode<T> argumentCommandNode =
                    new ArgumentCommandNode<>(name, null, type);

            currentNode.addChildNode(argumentCommandNode);
            currentNode.addArgumentNode(argumentCommandNode);

            if (currentNode.getArguments() != null) {
                currentNode = currentNode.getArguments().get(name);
                return this;
            }
            throw new CommandBuildException("Current node may be wrong when adding arg-node: " + name, null);
        }
        throw new CommandBuildException("ArgumentNode can only follow behind (Sub)ActionNode or OptionNode or ArgumentNode", null);
    }

    public void executor(CommandExecutor commandExecutor) {
        if (currentNode instanceof RootCommandNode || currentNode instanceof ExecutionCommandNode) {
            throw new CommandBuildException("RootNode or ExecutionNode can not have a command executor", null);
        }

        currentNode.setCommandExecutor(commandExecutor);
    }

    public RootCommandNode getRootCommandNode() {
        return rootCommandNode;
    }

    public CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    public void setCommandExecutor(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }
}
