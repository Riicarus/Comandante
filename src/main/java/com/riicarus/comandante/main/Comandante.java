package com.riicarus.comandante.main;

import com.riicarus.comandante.exception.CommandLoadException;
import com.riicarus.comandante.manage.CommandDispatcher;
import com.riicarus.comandante.manage.CommandInputHandler;
import com.riicarus.comandante.manage.CommandRegister;
import com.riicarus.comandante.tree.ExecutionNode;
import com.riicarus.util.asserts.Asserts;

import java.util.*;

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
     * 不对外暴露核心类, 只提供给 CommandLauncher 进行 API 暴露<br/>
     */
    protected Comandante() {
        this.commandDispatcher = new CommandDispatcher();
        this.commandInputHandler = new CommandInputHandler();
        this.commandRunnerThread = new Thread(new CommandRunner(this), "CommandRunnerThread");
    }

    /**
     * 启用命令处理线程, 保证只有一个线程在运行<br/>
     */
    protected synchronized void startCommandRunner() throws CommandLoadException {
        Asserts.isFalse(run, new CommandLoadException("CommandRunner is already running."));

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
     * 列出指令使用情况
     *
     * @return 指令使用情况列表
     */
    protected List<String> listCommandUsage() {
        return commandDispatcher.getCommandRegister().listCommandUsage();
    }

    /**
     * 停止指令执行线程的工作<br/>
     * 不建议在此处关闭 CommandLogger 的流, 因为可能后续会有指令注册等操作会使用到 CommandLogger<br/>
     */
    protected void stop() {
        run = false;
    }

    /**
     * 用于创建指令执行线程, 在一个单独的线程中进行指令处理工作<br/>
     * 主要用于处理外界通过 CommandLauncher 传入的指令字符串, 对其进行分发和执行<br/>
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
                    CommandLogger.log(e.getMessage());
                } finally {
                    CommandLogger.log("");
                }
            }
        }
    }
}
