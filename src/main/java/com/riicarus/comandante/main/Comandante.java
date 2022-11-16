package com.riicarus.comandante.main;

import com.riicarus.comandante.exception.CommandLoadException;
import com.riicarus.comandante.manage.CommandDispatcher;
import com.riicarus.comandante.manage.CommandInputHandler;
import com.riicarus.comandante.manage.CommandRegister;
import com.riicarus.comandante.tree.ExecutionNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * [FEATURE INFO]<br/>
 * comandante 交互中心, 维护 指令分发器 和 指令输入控制器, 并控制指令处理线程<br/>
 *
 * @author Skyline
 * @create 2022-10-15 16:23
 * @since 1.0
 */
public class Comandante {
    /**
     * 指令分发器
     */
    private final CommandDispatcher commandDispatcher;
    /**
     * IO
     */
    private final CommandInputHandler commandInputHandler;
    /**
     * 指令处理线程
     */
    private final Thread commandRunnerThread;
    /**
     * 是否为运行状态
     */
    private volatile boolean run = false;

    /**
     * 不对外暴露核心类, 只提供给 CommandUtil 进行 API 暴露<br/>
     */
    protected Comandante() {
        this.commandDispatcher = new CommandDispatcher();
        this.commandInputHandler = new CommandInputHandler();
        this.commandRunnerThread = new Thread(new CommandRunner(this), "CommandRunnerThread");
    }

    /**
     * 启用命令处理线程, 保证只有一个线程在运行<br/>
     */
    protected synchronized void startCommandRunner() {
        if (run) {
            throw new CommandLoadException("命令行已在运行中.");
        }

        commandRunnerThread.start();

        run = true;
    }

    protected CommandRegister getCommandRegister() {
        return commandDispatcher.getCommandRegister();
    }

    protected CommandInputHandler getIoHandler() {
        return commandInputHandler;
    }

    /**
     * 列出所有已加载的 execution 指令部分<br/>
     *
     * @return 已加载的 Execution 指令部分集合
     */
    protected Set<String> listAllExecutionCommand() {
        HashMap<String, ExecutionNode> executions = getCommandRegister().getRootNode().getExecutions();
        return executions == null ? new HashSet<>() : executions.keySet();
    }

    /**
     * 停止指令执行线程的工作<br/>
     * 不建议在此处关闭 Logger 的流, 因为可能后续会有指令注册等操作会使用到 Logger<br/>
     */
    protected void stop() {
        run = false;
    }

    /**
     * 用于创建指令执行线程, 在一个单独的线程中进行指令处理工作<br/>
     * 主要用于处理外界通过 CommandUtil 传入的指令字符串, 对其进行分发和执行<br/>
     */
    static class CommandRunner implements Runnable {

        private final Comandante comandante;

        public CommandRunner(Comandante comandante) {
            this.comandante = comandante;
        }

        @Override
        public void run() {
            while (comandante.run) {
                String command;
                try {
                    command = comandante.commandInputHandler.consume();
                    comandante.commandDispatcher.dispatch(command);
                } catch (Exception e) {
                    Logger.log(e.getMessage());
                } finally {
                    Logger.log("");
                }
            }
        }
    }
}