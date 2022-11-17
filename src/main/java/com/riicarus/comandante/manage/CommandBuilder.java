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
 *  1. 使用 exe() 定义一个 ExecutionNode<br/>
 *  4. 使用 opt() 定义一个 OptionNode<br/>
 *  5. 使用 arg() 定义一个 ArgumentNode<br/>
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
    /**
     * 当前被构建出的注册在 RootNode 下的 ExecutionNode, 需要保存所有的 OptionNode 信息
     */
    private ExecutionNode mainExecutionNode;

    public CommandBuilder(final RootNode rootNode) {
        this.rootNode = rootNode;
        this.currentNode = rootNode;
        this.currentExecutionNode = null;
        this.mainExecutionNode = null;
    }

    /**
     * 构建 ExecutionNode 节点, <br/>
     * 如果当前节点为 RootNode 或 ExecutionNode, 注册到当前节点下<br/>
     * 如果当前节点为 ArgumentNode, 根据 Argument#optionArg 属性决定,<br/>
     * true: 注册到 CurrentExecutionNode 下, false: 注册到当前节点下<br/>
     * 如果当前节点为 OptionNode, 注册到 CurrentExecutionNode 下<br/>
     * <br/>
     * 更新 CurrentNode 和 CurrentExecutionNode<br/>
     * <br/>
     * 会被自动注册入一个 CommandHelper 节点, 用于执行 'xxx -h/--help' 指令<br/>
     *
     * @param name 指令部分字符串
     * @return 指令构建器
     * @throws CommandBuildException 指令构建异常, 属于运行时异常
     */
    public CommandBuilder exe(String name) throws CommandBuildException {
        if (name == null || "".equals(name.trim())) {
            throw new CommandBuildException("Node name can not be null.");
        }

        ExecutionNode executionNode;
        boolean addMainExecution = currentNode instanceof RootNode;
        boolean addToCurrentNode =
                currentNode instanceof RootNode ||
                currentNode instanceof ExecutionNode ||
                (currentNode instanceof ArgumentNode && !((ArgumentNode<?>) currentNode).isOptionArg());

        if (addToCurrentNode) {
            executionNode = new ExecutionNode(name, currentNode);
            executionNode = currentNode.addExecution(executionNode);

            if (addMainExecution) {
                mainExecutionNode = executionNode;
            }
        } else {
            executionNode = new ExecutionNode(name, currentExecutionNode);
            executionNode = currentExecutionNode.addExecution(executionNode);
        }

        currentNode = executionNode;
        currentExecutionNode = executionNode;

        return this;
    }

    /**
     * Option 节点可以不设置短指令, 只设置长指令
     *
     * @param name 长指令部分字符串
     * @return 指令构建器
     * @throws CommandBuildException 指令构建异常, 属于运行时异常
     */
    public CommandBuilder opt(String name) throws CommandBuildException {
        return opt(name, null);
    }


    /**
     * 构建 OptionNode 节点<br/>
     * 如果 MainExecutionNode 中不包含 name 或 alias 相同的节点<br/>
     * 将其注册到 CurrentExecutionNode 下<br/>
     * 并在 MainExecutionNode 中保存<br/>
     * OptionNode 节点不会构成链式结构, 任何其父节点只能注册一层 OptionNode 节点<br/>
     * <br/>
     * 更新 CurrentNode<br/>
     * <br/>
     *
     * @param name 指令部分字符串, 用于长指令, 使用 '--'
     * @param alias 指令部分简写, 用户短指令, 使用 '-'
     * @return 指令构建器
     * @throws CommandBuildException 指令构建异常, 属于运行时异常
     */
    public CommandBuilder opt(String name, String alias) throws CommandBuildException {
        if (name == null || "".equals(name.trim())) {
            throw new CommandBuildException("Node name can not be null.");
        }

        // 如果该 OptionNode 节点没有被注册过
        if (!mainExecutionNode.containsOption(name, alias)) {
            OptionNode optionNode = new OptionNode(name, alias, currentExecutionNode);
            optionNode = currentExecutionNode.addOption(optionNode);
            mainExecutionNode.addAllOption(optionNode);

            currentNode = optionNode;
        }

        return this;
    }

    /**
     * 构建 ArgumentNode 节点<br/>
     * 将其注册到 CurrentNode 下 <br/>
     * 前驱节点不能是 RootNode <br/>
     * <br/>
     * 更新 CurrentNode<br/>
     * <br/>
     *
     * @param name 指令部分字符串
     * @param type 参数类型定义
     * @return 指令构建器
     * @throws CommandBuildException 指令构建异常, 属于运行时异常
     */
    public <T> CommandBuilder arg(String name, CommandArgumentType<T> type) throws CommandBuildException {
        if (name == null || "".equals(name.trim()) || type == null) {
            throw new CommandBuildException("Node name or type can not be null.");
        }

        ArgumentNode<?> argumentNode = new ArgumentNode<>(name, type, currentNode);

        boolean canAdd = !(currentNode instanceof RootNode);

        if (canAdd) {
            argumentNode = currentNode.addArgument(argumentNode);
        } else {
            throw new CommandBuildException("ArgumentNode can not be registered behind RootNode.");
        }

        currentNode = argumentNode;

        return this;
    }

    /**
     * 构建指令执行器, 将其注册到当前构建出的节点中, 作为一个可执行节点<br/>
     * 前驱节点不能是 RootNode<br/>
     *
     * @param commandExecutor 指令执行器
     * @throws CommandBuildException 指令构建异常, 属于运行时异常
     */
    public void executor(CommandExecutor commandExecutor) throws CommandBuildException {
        if (currentNode instanceof RootNode) {
            throw new CommandBuildException("RootNode can not have a command executor");
        }

        currentNode.setCommandExecutor(commandExecutor);
    }

    /**
     * 构建指令执行器, 将其注册到当前构建出的节点中, 作为一个可执行节点<br/>
     * 前驱节点不能是 RootNode<br/>
     *
     * @param commandExecutor 指令执行器
     * @param usage 指令用途说明
     * @throws CommandBuildException 指令构建异常, 属于运行时异常
     */
    public void executor(CommandExecutor commandExecutor, final String usage) throws CommandBuildException {
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
