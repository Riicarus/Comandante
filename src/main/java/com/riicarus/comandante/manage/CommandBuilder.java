package com.riicarus.comandante.manage;

import com.riicarus.comandante.argument.CommandArgumentType;
import com.riicarus.comandante.exception.CommandBuildException;
import com.riicarus.comandante.executor.CommandExecutor;
import com.riicarus.comandante.tree.*;

/**
 * [FEATURE INFO]<br/>
 * 指令构建器, 用于定义一条指令<br/>
 *
 * 主要功能:<br/>
 *  1. 使用 execution() 定义一个 ExecutionCommandNode<br/>
 *  2. 使用 action() 定义一个 ActionCommandNode<br/>
 *  3. 使用 subAction() 定义一个 SubActionCommandNode<br/>
 *  4. 使用 option() 定义一个 OptionCommandNode<br/>
 *  5. 使用 argument() 定义一个 ArgumentCommandNode<br/>
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
    private final RootNode rootNode;
    /**
     * 当前被构建出的节点, 会随着一条指令的构建逐渐更新, 但是保持为当前指令链的尾节点
     */
    private AbstractNode currentNode;
    /**
     * 当前被构建出的 ExecutionNode 节点, 会随着一条指令的构建逐渐更新, 但是保持为当前指令链的尾 ExecutionNode 节点
     */
    private ExecutionNode currentExecutionNode;

    public CommandBuilder(final RootNode rootNode) {
        this.rootNode = rootNode;
        this.currentNode = rootNode;
        this.currentExecutionNode = null;
    }

    /**
     * 构建 ExecutionNode 节点, 会被注册到根节点或其上一个 ExecutionNode 节点的子节点集合中<br/>
     * 会被自动注册入一个 CommandHelper 节点, 用于执行 'xxx -h/--help' 指令<br/>
     * CommandHelper 节点无法重定向到其他节点<br/>
     *
     * @param name 指令部分字符串
     * @return 指令构建器
     * @throws CommandBuildException 指令构建异常, 属于运行时异常
     */
    public CommandBuilder exe(String name) {
        boolean isRootOrExecutionNode = currentNode instanceof RootNode || currentNode instanceof ExecutionNode;

        ExecutionNode executionNode = new ExecutionNode(name);

        if (isRootOrExecutionNode) {
            // 如果当前节点位于指令树主干, 就让当前节点添加
            currentNode.addChild(executionNode);
            executionNode = currentNode.addExecution(executionNode);
        } else if (currentExecutionNode != null){
            // 当前节点不在指令树主干上, 就让上一个主干上的 ExecutionNode 添加
            currentExecutionNode.addChild(executionNode);
            executionNode = currentExecutionNode.addExecution(executionNode);
        } else {
            throw new CommandBuildException("ExecutionNode[" + name + "] build failed.");
        }

        currentNode = executionNode;
        currentExecutionNode = executionNode;

        return this;
    }

    /**
     * 构建 OptionNode 节点<br/>
     * 需要将所有 OptionNode 节点都添加到对应的 ExecutionCommandNode 子节点集合中<br/>
     * OptionNode 节点不会构成链式结构, 任何其父节点只能注册一层 OptionNode 节点<br/>
     *
     * @param name 指令部分字符串, 用于长指令, 使用 '--'
     * @param alias 指令部分简写, 用户短指令, 使用 '-'
     * @return 指令构建器
     * @throws CommandBuildException 指令构建异常, 属于运行时异常
     */
    public CommandBuilder opt(String name, String alias) {
        OptionNode optionNode = new OptionNode(name, alias);

        if (currentNode instanceof ExecutionNode) {
            // 当前为 ExecutionNode, 直接添加 OptionNode
            currentNode.addChild(optionNode);
            optionNode = currentNode.addOption(optionNode);
        } else if (currentNode instanceof OptionNode) {
            // 当前为 OptionNode, 需要添加到对应的 ExecutionNode 的子节点
            currentExecutionNode.addChild(optionNode);
            optionNode = currentExecutionNode.addOption(optionNode);
        } else if (currentNode instanceof ArgumentNode<?>) {
            // 当前为 ArgumentNode, 需要添加到对应的 ExecutionNode 的子节点
            currentExecutionNode.addChild(optionNode);
            optionNode = currentExecutionNode.addOption(optionNode);
        } else {
            throw new CommandBuildException("OptionNode[" + name + "] build failed.");
        }

        currentNode = optionNode;

        return this;
    }

    /**
     * Option 节点可以不设置短指令, 只设置长指令
     *
     * @param name 长指令部分字符串
     * @return 指令构建器
     * @throws CommandBuildException 指令构建异常, 属于运行时异常
     */
    public CommandBuilder opt(String name) {
        return opt(name, null);
    }

    /**
     * 构建 ArgumentNode 节点<br/>
     * 父节点可以为 ExecutionNode 或 OptionNode 或 ArgumentNode<br/>
     * 将其加入父节点的 arguments 有序链表中
     *
     * @param name 指令部分字符串
     * @param type 参数类型定义
     * @return 指令构建器
     * @throws CommandBuildException 指令构建异常, 属于运行时异常
     */
    public <T> CommandBuilder arg(String name, CommandArgumentType<T> type) {
        ArgumentNode<?> argumentNode = new ArgumentNode<>(name, type);

        if (currentNode instanceof ExecutionNode ||
                currentNode instanceof OptionNode ||
                currentNode instanceof ArgumentNode<?>) {
            currentNode.addChild(argumentNode);
            argumentNode = currentNode.setNextArgument(argumentNode);
        } else {
            throw new CommandBuildException("ArgumentNode[" + name + "] build failed.");
        }

        currentNode = argumentNode;

        return this;
    }

    /**
     * 构建指令执行器, 将其注册到当前构建出的节点中, 作为一个可执行节点<br/>
     * 父节点不能为 RootCommandNode 或 ExecutionCommandNode<br/>
     *
     * @param commandExecutor 指令执行器
     * @throws CommandBuildException 指令构建异常, 属于运行时异常
     */
    public void executor(CommandExecutor commandExecutor) {
        if (currentNode instanceof RootNode) {
            throw new CommandBuildException("RootNode can not have a command executor");
        }

        currentNode.setCommandExecutor(commandExecutor);
    }
    /**
     * 构建指令执行器, 将其注册到当前构建出的节点中, 作为一个可执行节点<br/>
     * 父节点不能为 RootCommandNode 或 ExecutionCommandNode<br/>
     *
     * @param commandExecutor 指令执行器
     * @param usage 指令用途说明
     * @throws CommandBuildException 指令构建异常, 属于运行时异常
     */
    public void executor(CommandExecutor commandExecutor, final String usage) {
        if (currentNode instanceof RootNode) {
            throw new CommandBuildException("RootNode can not have a command executor");
        }

        currentNode.setCommandExecutor(commandExecutor);
        currentNode.setUsage(usage);
    }

    public RootNode getRootNode() {
        return rootNode;
    }
}
