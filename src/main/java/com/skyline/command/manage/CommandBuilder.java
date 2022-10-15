package com.skyline.command.manage;

import com.skyline.command.annotation.*;
import com.skyline.command.argument.CommandArgumentType;
import com.skyline.command.exception.CommandBuildException;
import com.skyline.command.executor.CommandExecutor;
import com.skyline.command.tree.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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

    private final Class<?> definitionClass;

    private CommandExecutor commandExecutor;

    public CommandBuilder(final RootCommandNode rootCommandNode, final Class<?> definitionClass) {
        this.rootCommandNode = rootCommandNode;
        this.definitionClass = definitionClass;
    }

    public CommandBuilder(final RootCommandNode rootCommandNode,
                          final Class<?> definitionClass,
                          final CommandExecutor commandExecutor) {
        this.rootCommandNode = rootCommandNode;
        this.definitionClass = definitionClass;
        this.commandExecutor = commandExecutor;
    }

    public final void build() {
        buildExecution();
    }

    protected void buildExecution() {
        Execution execution;

        if ((execution = definitionClass.getAnnotation(Execution.class)) != null) {
            String name = execution.name().trim();
            if (!name.isEmpty()) {
                ExecutionCommandNode executionCommandNode = new ExecutionCommandNode(name);
                rootCommandNode.addChildNode(executionCommandNode);
                rootCommandNode.addExecutionNode(executionCommandNode);

                Method[] methods = definitionClass.getMethods();
                buildAction(executionCommandNode, methods);
            }
        }
    }

    protected void buildAction(ExecutionCommandNode node, Method[] methods) {
        if (methods.length == 0) {
            throw new CommandBuildException("No method found in class: " + definitionClass.getName(), null);
        }

        for (Method method : methods) {
            if (method.isAnnotationPresent(Action.class)) {
                Action action = method.getAnnotation(Action.class);
                String name = action.name();
                ActionCommandNode actionCommandNode = new ActionCommandNode(null, name, false);
                node.addChildNode(actionCommandNode);
                node.addActionNode(actionCommandNode);

                boolean hasSubPattern = false;
                SubAction subAction = action.subAction();
                if (!subAction.name().trim().isEmpty()) {
                    hasSubPattern = true;
                    buildSubAction(actionCommandNode, subAction, method);
                }

                Option option = action.option();
                if (!hasSubPattern && !option.name().trim().isEmpty()) {
                    hasSubPattern = true;
                    buildOption(actionCommandNode, option, method);
                }

                Argument[] arguments = action.args();
                if (!hasSubPattern && arguments.length != 0) {
                    hasSubPattern = true;
                    buildArgument(actionCommandNode, arguments, method);
                }

                if (!hasSubPattern) {
                    if (node.getActions() != null) {
                        ActionCommandNode commandNode = node.getActions().get(name);
                        if (commandNode != null && commandNode.getCommandExecutor() == null) {
                            CommandExecutor executor = new CommandExecutor(commandNode, method);
                            commandNode.setCommandExecutor(executor);
                        }
                    }
                }
            }
        }
    }

    protected void buildSubAction(ActionCommandNode node, SubAction subAction, Method method) {
        String name = subAction.name();
        ActionCommandNode actionCommandNode = new ActionCommandNode(null, name, true);
        node.addChildNode(actionCommandNode);
        node.addSubActionNode(actionCommandNode);

        boolean hasSubPattern = false;
        Option option = subAction.option();
        if (!option.name().trim().isEmpty()) {
            hasSubPattern = true;
            buildOption(actionCommandNode, option, method);
        }

        Argument[] arguments = subAction.args();
        if (!hasSubPattern && arguments.length != 0) {
            hasSubPattern = true;
            buildArgument(actionCommandNode, arguments, method);
        }

        if (!hasSubPattern) {
            if (node.getSubActions() != null) {
                ActionCommandNode commandNode = node.getSubActions().get(name);
                if (commandNode != null && commandNode.getCommandExecutor() == null) {
                    CommandExecutor executor = new CommandExecutor(commandNode, method);
                    commandNode.setCommandExecutor(executor);
                }
            }
        }
    }

    protected void buildOption(ActionCommandNode node, Option option, Method method) {
        String name = option.name();
        String alias = option.alias();
        OptionCommandNode optionCommandNode = new OptionCommandNode(null, name, alias);
        node.addChildNode(optionCommandNode);
        node.addOptionNode(optionCommandNode);

        boolean hasSubPattern = false;
        Argument[] arguments = option.args();
        if (arguments.length != 0) {
            hasSubPattern = true;
            buildArgument(optionCommandNode, arguments, method);
        }

        if (!hasSubPattern) {
            if (node.getOptions() != null) {
                OptionCommandNode commandNode = node.getOptions().get(name);
                if (commandNode != null && commandNode.getCommandExecutor() == null) {
                    CommandExecutor executor = new CommandExecutor(commandNode, method);
                    commandNode.setCommandExecutor(executor);
                }
            }
        }
    }

    protected void buildArgument(CommandNode node, Argument[] arguments, Method method) {
        List<CommandArgument<?>> argumentList = new ArrayList<>();

        for (Argument argument : arguments) {
            String name = argument.name();
            CommandArgumentType<?> type;
            try {
                type = argument.type().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new CommandBuildException("Failed to create CommandArgumentType for argument: " + name, e.getCause());
            }

            CommandArgument<?> commandArgument = new CommandArgument<>(name, type);
            argumentList.add(commandArgument);
        }

        ArgumentCommandNode argumentCommandNode = new ArgumentCommandNode(null, argumentList);
        node.addChildNode(argumentCommandNode);
        node.addArgumentNode(argumentCommandNode);

        if (node.getArguments() != null) {
            ArgumentCommandNode commandNode = node.getArguments().get(ArgumentCommandNode.ARGUMENT_NAME);
            if (commandNode != null && commandNode.getCommandExecutor() == null) {
                CommandExecutor executor = new CommandExecutor(commandNode, method);
                commandNode.setCommandExecutor(executor);
            }
        }
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
