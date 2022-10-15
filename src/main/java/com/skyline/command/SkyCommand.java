package com.skyline.command;

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
     * 指令注册器
     */
    private final CommandRegister commandRegister;
    /**
     * 指令输入管理
     */
    private final IOHandler ioHandler;
    /**
     * 指令分发器
     */
    private final CommandDispatcher commandDispatcher;
    /**
     * 指令定义包路径
     */
    private final String commandDefinitionPackage;
    /**
     * 单例
     */
    private volatile static SkyCommand SKY_COMMAND;

    private static SkyCommand createSkyCommand(final String commandDefinitionPackage) {
        return createSkyCommand(new CommandRegister(commandDefinitionPackage), new IOHandler(), new CommandDispatcher(), commandDefinitionPackage);
    }

    private static SkyCommand createSkyCommand(final CommandRegister commandRegister, final IOHandler ioHandler, final CommandDispatcher commandDispatcher, final String commandDefinitionPackage) {
        if (SKY_COMMAND == null) {
            synchronized (SkyCommand.class) {
                if (SKY_COMMAND == null) {
                    SKY_COMMAND = new SkyCommand(commandRegister, ioHandler, commandDispatcher, commandDefinitionPackage);
                }
            }
        }

        return SKY_COMMAND;
    }

    private SkyCommand() {
        this.commandRegister = new CommandRegister("");
        this.ioHandler = new IOHandler();
        this.commandDispatcher = new CommandDispatcher();
        this.commandDefinitionPackage = "";
    }

    private SkyCommand(final String commandDefinitionPackage) {
        this.commandRegister = new CommandRegister(commandDefinitionPackage);
        this.ioHandler = new IOHandler();
        this.commandDispatcher = new CommandDispatcher();
        this.commandDefinitionPackage = commandDefinitionPackage;
    }

    private SkyCommand(final CommandRegister commandRegister, final IOHandler ioHandler, final CommandDispatcher commandDispatcher, final String commandDefinitionPackage) {
        this.commandRegister = commandRegister;
        this.ioHandler = ioHandler;
        this.commandDispatcher = commandDispatcher;
        this.commandDefinitionPackage = commandDefinitionPackage;
    }

    public static void startSkyCommand(final String commandDefinitionPackage) {
        SkyCommand skyCommand = createSkyCommand(commandDefinitionPackage);
        skyCommand.loadCommand();
        skyCommand.commandDispatcher.setRootCommandNode(skyCommand.commandRegister.getRootCommandNode());

        // skyCommand.commandRegister.getRootCommandNode().getChildren().get("plugin").getChildren().get("unload").getChildren().get("dir").getChildren().get("args").getCommandExecutor().execute("hello");

        Thread thread = new Thread(new InnerRunner(skyCommand));
        thread.start();
    }

    private void loadCommand() {
        commandRegister.doRegister();
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
