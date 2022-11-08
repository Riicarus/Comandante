package com.skyline.command.main;

import com.skyline.command.command.InnerCommand;
import com.skyline.command.config.Config;
import com.skyline.command.exception.CommandLoadException;
import com.skyline.command.manage.*;
import com.skyline.command.tree.ExecutionCommandNode;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [FEATURE INFO]<br/>
 * command 交互中心, 维护 指令分发器 和 指令输入控制器, 并控制指令处理线程<br/>
 *
 * @author Skyline
 * @create 2022-10-15 16:23
 * @since 1.0
 */
public class SkyCommand {
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
    private final Thread skyCommandThread;
    /**
     * 是否为运行状态
     */
    private volatile boolean run = false;

    /**
     * 不对外暴露核心类, 只提供给 CommandUtil 进行 API 暴露<br/>
     */
    protected SkyCommand() {
        this.commandDispatcher = new CommandDispatcher();
        this.commandInputHandler = new CommandInputHandler();
        this.skyCommandThread = new Thread(new CommandRunner(this), "SkyCommandThread");
    }

    /**
     * 启用命令工具, 保证只有一个线程在运行<br/>
     * 在这里加载内置指令, 并且从配置文件读入配置<br/>
     */
    protected synchronized void startSkyCommand() {
        if (run) {
            throw new CommandLoadException("命令行已在运行中.");
        }

        InnerCommand.defineCommand();
        Config.loadConfig();

        skyCommandThread.start();

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
        ConcurrentHashMap<String, ExecutionCommandNode> executions = getCommandRegister().getRootCommandNode().getExecutions();
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

        private final SkyCommand skyCommand;

        public CommandRunner(SkyCommand skyCommand) {
            this.skyCommand = skyCommand;
        }

        @Override
        public void run() {
            while (skyCommand.run) {
                String command;
                try {
                    command = skyCommand.commandInputHandler.consume();
                    skyCommand.commandDispatcher.dispatch(command);
                } catch (Exception e) {
                    Logger.log(e.getMessage());
                } finally {
                    Logger.log("");
                }
            }
        }
    }
}
