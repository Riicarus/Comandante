package com.skyline.command;

import com.skyline.command.command.InnerCommand;
import com.skyline.command.config.Config;
import com.skyline.command.exception.CommandLoadException;
import com.skyline.command.manage.*;
import com.skyline.command.tree.ExecutionCommandNode;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * [FEATURE INFO]<br/>
 * 对外提供 api 的类, 单例
 *
 * @author Skyline
 * @create 2022-10-15 16:23
 * @since 1.0.0
 */
public class SkyCommand {
    /**
     * 是否正在运行中
     */
    private final AtomicInteger running = new AtomicInteger(0);
    /**
     * 最大运行数量
     */
    private final int maxRunning;
    /**
     * 指令分发器
     */
    private final CommandDispatcher commandDispatcher;
    /**
     * 单例
     */
    private volatile static SkyCommand SKY_COMMAND;

    public static SkyCommand getSkyCommand() {
        return getSkyCommand(new CommandDispatcher(), 6);
    }

    public static SkyCommand getSkyCommand(int maxRunning) {
        return getSkyCommand(new CommandDispatcher(), maxRunning);
    }

    private static SkyCommand getSkyCommand(final CommandDispatcher commandDispatcher, int maxRunning) {
        if (SKY_COMMAND == null) {
            synchronized (SkyCommand.class) {
                if (SKY_COMMAND == null) {
                    SKY_COMMAND = new SkyCommand(commandDispatcher, maxRunning);
                }
            }
        }

        return SKY_COMMAND;
    }

    private SkyCommand(final CommandDispatcher commandDispatcher, int maxRunning) {
        this.commandDispatcher = commandDispatcher;
        this.maxRunning = maxRunning;
    }

    public void startSkyCommand(final IOHandler ioHandler) {
        if (running.get() >= maxRunning) {
            throw new CommandLoadException("已达到最大运行数量. ");
        }

        new InnerCommand(this).defineCommand();
        Config.loadConfig();

        Thread thread = new Thread(new InnerRunner(this, ioHandler));
        thread.start();

        running.incrementAndGet();
    }

    public CommandBuilder register() {
        return commandDispatcher.getCommandRegister().getBuilder();
    }

    public CommandRegister getCommandRegister() {
        return commandDispatcher.getCommandRegister();
    }

    public Set<String> listAllExecutionCommand() {
        ConcurrentHashMap<String, ExecutionCommandNode> executions = getCommandRegister().getRootCommandNode().getExecutions();
        return executions == null ? new HashSet<>() : executions.keySet();
    }

    static class InnerRunner implements Runnable {

        private final SkyCommand skyCommand;

        private final IOHandler ioHandler;

        public InnerRunner(SkyCommand skyCommand, IOHandler ioHandler) {
            this.skyCommand = skyCommand;
            this.ioHandler = ioHandler;
        }

        @Override
        public void run() {
            String commandStr;

            while ((commandStr = ioHandler.doGetCommand()) != null) {
                try {
                    skyCommand.commandDispatcher.dispatch(commandStr);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
