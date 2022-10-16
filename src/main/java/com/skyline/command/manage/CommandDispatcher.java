package com.skyline.command.manage;

import com.skyline.command.tree.RootCommandNode;

/**
 * [FEATURE INFO]<br/>
 * 指令分发器
 *
 * @author Skyline
 * @create 2022-10-15 23:27
 * @since 1.0.0
 */
public class CommandDispatcher {

    private final CommandRegister commandRegister;

    public CommandDispatcher() {
        this.commandRegister = new CommandRegister();
    }

    public CommandDispatcher(CommandRegister commandRegister) {
        this.commandRegister = commandRegister;
    }

    public void dispatch(final String commandStr) {

    }

    public CommandRegister getCommandRegister() {
        return commandRegister;
    }
}
