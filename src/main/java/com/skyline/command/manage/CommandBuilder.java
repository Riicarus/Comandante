package com.skyline.command.manage;

import com.skyline.command.argument.CommandArgumentType;
import com.skyline.command.exception.CommandBuildException;
import com.skyline.command.executor.CommandExecutor;
import com.skyline.command.executor.CommandHelper;
import com.skyline.command.tree.*;

/**
 * [FEATURE INFO]<br/>
 * 指令构建器, 用于定义一条指令<br/>
 *
 * 主要功能:<br/>
 *  1. 使用 execution() 定义一个 ExecutionNode<br/>
 *  2. 使用 action() 定义一个 ActionNode<br/>
 *  3. 使用 subAction() 定义一个 SubActionNode<br/>
 *  4. 使用 option() 定义一个 OptionNode<br/>
 *  5. 使用 argument() 定义一个 ArgumentNode<br/>
 *  6. 使用 executor() 定义一个 指令执行器, 会被注册到上一个调用方法生成的节点上<br/>
 *
 * @author Skyline
 * @create 2022-10-15 0:23
 * @since 1.0
 */
public class CommandBuilder {

    /**
     * 保持一个指令树根节点的引用, 将一个指令分支注册到根节点上
     */
    private final RootCommandNode rootCommandNode;

    /**
     * 当前被构建出的节点, 会随着一条指令的构建逐渐更新, 但是保持为当前指令链的尾节点
     */
    private CommandNode currentNode;

    public CommandBuilder(final RootCommandNode rootCommandNode) {
        this.rootCommandNode = rootCommandNode;
        this.currentNode = rootCommandNode;
    }

    /**
     * 构建 ExecutionNode 节点, 会被注册到 RootCommandNode 的子节点集合中<br/>
     * 会被自动注册入一个 CommandHelper 节点, 用于执行 'xxx -h/--help' 指令<br/>
     * 其父节点只能是根节点<br/>
     *
     * @param name 指令部分字符串
     * @return 指令构建器
     * @throws CommandBuildException 指令构建异常, 属于运行时异常
     */
    public CommandBuilder execution(String name) {
        if (currentNode instanceof RootCommandNode) {
            ExecutionCommandNode executionCommandNode = new ExecutionCommandNode(name);
            currentNode.addChildNode(executionCommandNode);
            currentNode.addExecutionNode(executionCommandNode);

            if (currentNode.getExecutions() != null) {
                currentNode = currentNode.getExecutions().get(name);

                // 添加 --help/-h 指令支持
                OptionCommandNode helpOptionNode = new OptionCommandNode(null, "help", "h");
                helpOptionNode.setUsage("帮助指令");
                helpOptionNode.setCommandExecutor(new CommandHelper(currentNode), true);
                currentNode.addChildNode(helpOptionNode);
                currentNode.addOptionNode(helpOptionNode);

                return this;
            }

            throw new CommandBuildException("Current node may be wrong when adding exe-node: " + name, null);
        }

        throw new CommandBuildException("ExecutionNode can only follow behind RootNode", null);
    }

    /**
     * 构建 ActionNode 节点, 将其注册到父 ExecutionNode 的子节点集合中<br/>
     * 父节点只能为 ExecutionNode<br/>
     *
     * @param name 指令部分字符串
     * @return 指令构建器
     * @throws CommandBuildException 指令构建异常, 属于运行时异常
     */
    public CommandBuilder action(String name) {
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

    /**
     * 构建 SubActionNode 节点, 将其注册到父 ActionNode 的子节点集合中<br/>
     * 父节点只能为 ActionNode<br/>
     *
     * @param name 指令部分字符串
     * @return 指令构建器
     * @throws CommandBuildException 指令构建异常, 属于运行时异常
     */
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

    /**
     * 构建 OptionNode 节点, 将其注册到父节点的子节点集合中<br/>
     * 父节点可以为 ExecutionNode/ActionNode/SubActionNode/ArgumentNode<br/>
     *
     * @param name 指令部分字符串, 用于长指令, 使用 '--'
     * @param alias 指令部分简写, 用户短指令, 使用 '-'
     * @return 指令构建器
     * @throws CommandBuildException 指令构建异常, 属于运行时异常
     */
    public CommandBuilder option(String name, String alias) {
        if (currentNode instanceof ExecutionCommandNode ||
                currentNode instanceof ActionCommandNode ||
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

    /**
     * 构建 ArgumentNode 节点, 将其注册到父 OptionNode 的子节点集合中<br/>
     * 父节点只能为 OptionNode<br/>
     *
     * @param name 指令部分字符串
     * @param type 参数类型定义
     * @return 指令构建器
     * @throws CommandBuildException 指令构建异常, 属于运行时异常
     */
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

    /**
     * 构建指令执行器, 将其注册到当前构建出的节点中, 作为一个可执行节点<br/>
     * 父节点不能为 RootNode 或 ExecutionNode<br/>
     *
     * @param commandExecutor 指令执行器
     * @throws CommandBuildException 指令构建异常, 属于运行时异常
     */
    public void executor(CommandExecutor commandExecutor) {
        if (currentNode instanceof RootCommandNode || currentNode instanceof ExecutionCommandNode) {
            throw new CommandBuildException("RootNode or ExecutionNode can not have a command executor", null);
        }

        currentNode.setCommandExecutor(commandExecutor);
    }
    /**
     * 构建指令执行器, 将其注册到当前构建出的节点中, 作为一个可执行节点<br/>
     * 父节点不能为 RootNode 或 ExecutionNode<br/>
     *
     * @param commandExecutor 指令执行器
     * @param usage 指令用途说明
     * @throws CommandBuildException 指令构建异常, 属于运行时异常
     */
    public void executor(CommandExecutor commandExecutor, final String usage) {
        if (currentNode instanceof RootCommandNode || currentNode instanceof ExecutionCommandNode) {
            throw new CommandBuildException("RootNode or ExecutionNode can not have a command executor", null);
        }

        currentNode.setCommandExecutor(commandExecutor);
        currentNode.setUsage(usage);
    }

    public RootCommandNode getRootCommandNode() {
        return rootCommandNode;
    }
}
