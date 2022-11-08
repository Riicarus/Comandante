package com.skyline.command;

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
 * command 交互中心
 *
 * @author Skyline
 * @create 2022-10-15 16:23
 * @since 1.0.0
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

    protected SkyCommand() {
        this.commandDispatcher = new CommandDispatcher();
        this.commandInputHandler = new CommandInputHandler();
        this.skyCommandThread = new Thread(new InnerRunner(this), "SkyCommandThread");
    }

    /**
     * 启用命令工具, 保证只有一个线程在运行
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

    protected Set<String> listAllExecutionCommand() {
        ConcurrentHashMap<String, ExecutionCommandNode> executions = getCommandRegister().getRootCommandNode().getExecutions();
        return executions == null ? new HashSet<>() : executions.keySet();
    }

    protected void stop() {
        run = false;
    }

    static class InnerRunner implements Runnable {

        private final SkyCommand skyCommand;

        public InnerRunner(SkyCommand skyCommand) {
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
