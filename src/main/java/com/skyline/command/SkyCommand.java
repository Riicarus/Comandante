package com.skyline.command;

import com.skyline.command.manage.CommandBuilder;
import com.skyline.command.manage.CommandDispatcher;
import com.skyline.command.manage.CommandRegister;
import com.skyline.command.manage.IOHandler;

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

    private static SkyCommand createSkyCommand() {
        return createSkyCommand(new IOHandler(), new CommandDispatcher());
    }

    private static SkyCommand createSkyCommand(final IOHandler ioHandler, final CommandDispatcher commandDispatcher) {
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
        this.ioHandler = new IOHandler();
        this.commandDispatcher = new CommandDispatcher();
    }

    private SkyCommand(final String commandDefinitionPackage) {
        this.ioHandler = new IOHandler();
        this.commandDispatcher = new CommandDispatcher();
    }

    private SkyCommand(final IOHandler ioHandler, final CommandDispatcher commandDispatcher) {
        this.ioHandler = ioHandler;
        this.commandDispatcher = commandDispatcher;
    }

    public static SkyCommand startSkyCommand() {
        SkyCommand skyCommand = createSkyCommand();

        // skyCommand.commandRegister.getRootCommandNode().getChildren().get("plugin").getChildren().get("unload").getChildren().get("dir").getChildren().get("args").getCommandExecutor().execute("hello");

        Thread thread = new Thread(new InnerRunner(skyCommand));
        thread.start();

        return skyCommand;
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
                skyCommand.commandDispatcher.dispatch(commandStr);
            }
        }
    }
}
