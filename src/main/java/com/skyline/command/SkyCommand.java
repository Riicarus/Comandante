package com.skyline.command;

import com.skyline.command.command.InnerCommand;
import com.skyline.command.config.Config;
import com.skyline.command.manage.*;

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
    private boolean running;
    /**
     * 指令输入管理
     */
    private final IOHandler ioHandler;
    /**
     * 指令分发器
     */
    private final CommandDispatcher commandDispatcher;
    /**
     * 单例
     */
    private volatile static SkyCommand SKY_COMMAND;

    public static SkyCommand getSkyCommand() {
        return getSkyCommand(new ConsoleIOHandler(), new CommandDispatcher());
    }

    public static SkyCommand getSkyCommand(final IOHandler ioHandler, final CommandDispatcher commandDispatcher) {
        if (SKY_COMMAND == null) {
            synchronized (SkyCommand.class) {
                if (SKY_COMMAND == null) {
                    SKY_COMMAND = new SkyCommand(ioHandler, commandDispatcher);
                }
            }
        }

        return SKY_COMMAND;
    }

    private SkyCommand() {
        this.ioHandler = new ConsoleIOHandler();
        this.commandDispatcher = new CommandDispatcher();
    }

    private SkyCommand(final IOHandler ioHandler, final CommandDispatcher commandDispatcher) {
        this.ioHandler = ioHandler;
        this.commandDispatcher = commandDispatcher;
    }

    public void startSkyCommand() {
        if (running) {
            throw new RuntimeException("Command plugin is running.");
        }

        new InnerCommand(this).defineCommand();
        Config.loadConfig();

        Thread thread = new Thread(new InnerRunner(this));
        thread.start();

        running = true;
    }

    public CommandBuilder register() {
        return commandDispatcher.getCommandRegister().getBuilder();
    }

    public CommandRegister getCommandRegister() {
        return commandDispatcher.getCommandRegister();
    }

    static class InnerRunner implements Runnable {

        private final SkyCommand skyCommand;

        public InnerRunner(SkyCommand skyCommand) {
            this.skyCommand = skyCommand;
        }

        @Override
        public void run() {
            String commandStr;

            while ((commandStr = skyCommand.ioHandler.doGetCommand()) != null) {
                try {
                    skyCommand.commandDispatcher.dispatch(commandStr);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
