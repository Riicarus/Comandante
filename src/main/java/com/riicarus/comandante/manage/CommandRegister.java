package com.riicarus.comandante.manage;

/**
 * [FEATURE INFO]<br/>
 * Command Register provides the CommandBuilder and maintains the command item manager.
 *
 * @author Riicarus
 * @create 2022-10-15 10:46
 * @since 1.0
 */
public class CommandRegister {

    /**
     * CommandItemManager manages the registered and reserved command items.
     */
    private final CommandItemManager commandItemManager = new CommandItemManager();

    /**
     * Get CommandBuilder, used for command building.
     *
     * @return CommandBuilder
     */
    public CommandBuilder builder() {
        return new CommandBuilder(commandItemManager);
    }

    public CommandItemManager getCommandItemManager() {
        return commandItemManager;
    }
}
