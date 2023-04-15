package com.riicarus.comandante.manage;

import com.riicarus.comandante.exception.CommandBuildException;
import com.riicarus.comandante.executor.CommandExecutor;
import com.riicarus.comandante.executor.Executable;
import com.riicarus.util.asserts.Asserts;

/**
 * [FEATURE INFO]<br/>
 * CommandBuilder is used to build a command made up of command items.<br/>
 * <p>
 * function:<br/>
 * 1. use main() to define a main item;<br/>
 * 2. use opt() to define an option item;<br/>
 * 3. use arg() to define an argument item;<br/>
 * 4. use executor() to define a command executor, which will be registered to it's owner item
 * (For main and arg items, it's prevItem, while for opt items, it's prevMainItem.);<br/>
 *
 * @author Riicarus
 * @create 2022-10-15 0:23
 * @since 1.0
 */
public class CommandBuilder {

    /**
     * Item manager is used to build lexical items and construct a grammar rule.
     */
    private final CommandItemManager commandItemManager;

    /**
     * Previous built item in one command.
     */
    private CommandItem prevItem;
    /**
     * Previous built main item in one command.
     */
    private CommandItem prevMainItem;

    public CommandBuilder(final CommandItemManager commandItemManager) {
        this.commandItemManager = commandItemManager;
        this.prevItem = CommandItem.ROOT;
        this.prevMainItem = CommandItem.ROOT;
    }

    /**
     * Build main item, <br/>
     * Register a main item to the previous node, which must be ROOT or a main node.<br/>
     * <br/>
     * Update prev item and prev main item.<br/>
     * <br/>
     *
     * @param name command string item
     * @return CommandBuilder
     * @throws CommandBuildException runtime exception
     */
    public CommandBuilder main(String name) throws CommandBuildException {
        Asserts.notEmpty(name, new CommandBuildException("Node name can not be null."));

        if (!canRegisterMain()) {
            throw new CommandBuildException("MainNode's place in this command is wrong, name: " + name);
        }

        // If one item is added before, then will ignore this action.
        CommandItem item;
        if (!commandItemManager.containsItem(name, prevMainItem)) {
            item = new CommandItem(CommandItemType.RESERVED_WORD,
                    prevItem.getSerialId(),
                    commandItemManager.generateSerialId(),
                    name,
                    null);
            commandItemManager.addLexicalItem(item, prevMainItem);
        } else {
            item = commandItemManager.getItem(name, prevMainItem);
        }
        prevMainItem = item;
        prevItem = item;

        return this;
    }

    /**
     * The option item can only have full name without alias.
     *
     * @param name full name of the option item
     * @return CommandBuilder
     * @throws CommandBuildException runtime exception
     */
    public CommandBuilder opt(String name) throws CommandBuildException {
        return opt(name, "");
    }

    /**
     * Build option item.<br/>
     * If there's no the same node with the name or alias register(in the item manager), register it to the previous main item.<br/>
     * Option items will not form a chain, but will only be registered to the previous main item.<br/>
     * <br/>
     * Update prev item after register.<br/>
     * <br/>
     *
     * @param name  full name of the option item
     * @param alias alias for the full name, usually the first character of the full name
     * @return CommandBuilder
     * @throws CommandBuildException runtime exception
     */
    public CommandBuilder opt(String name, String alias) throws CommandBuildException {
        Asserts.notEmpty(name, new CommandBuildException("Node name can not be null."));

        // If one item is added before, then will ignore this action.
        if (!commandItemManager.containsItem(name, prevMainItem)) {
            CommandItem item = new CommandItem(CommandItemType.RESERVED_WORD,
                    prevMainItem.getSerialId(),
                    commandItemManager.generateSerialId(),
                    name,
                    alias);
            commandItemManager.addLexicalItem(item, prevMainItem);

            prevItem = item;
        } else {
            prevItem = commandItemManager.getItem(name, prevMainItem);
        }

        return this;
    }

    /**
     * Build argument item.<br/>
     * Register it to prev item which can not be ROOT.<br/>
     * <br/>
     * Update prev item after register.<br/>
     * <br/>
     *
     * @param name name of argument item
     * @return CommandBuilder
     * @throws CommandBuildException runtime exception
     */
    public <T> CommandBuilder arg(String name) throws CommandBuildException {
        Asserts.notEmpty(name, new CommandBuildException("Node name can not be null."));

        if (CommandItem.ROOT.equals(prevItem)) {
            throw new CommandBuildException("Argument item can not be registered behind Root.");
        }

        // If one item is added before, then will ignore this action.
        if (!commandItemManager.containsItem(FixedLexicalItemValue.ARGUMENT.getValue(), prevItem)) {
            CommandItem item = new CommandItem(CommandItemType.ARGUMENT,
                    prevItem.getSerialId(),
                    commandItemManager.generateSerialId(),
                    FixedLexicalItemValue.ARGUMENT.getValue(),
                    name);
            commandItemManager.addLexicalItem(item, prevItem);

            prevItem = item;
        } else {
            prevItem = commandItemManager.getItem(FixedLexicalItemValue.ARGUMENT.getValue(), prevItem);
        }

        return this;
    }

    /**
     * Build Command Executor and register it to current item which can not be ROOT as one executable item.<br/>
     *
     * @param executor CommandBuilder
     * @throws CommandBuildException runtime exception
     */
    public void executor(Executable executor) throws CommandBuildException {
        if (CommandItem.ROOT.equals(prevItem)) {
            throw new CommandBuildException("Executor can not be registered behind Root.");
        }

        CommandExecutor commandExecutor = new CommandExecutor(executor, "");

        commandItemManager.bindExecutor(prevItem, commandExecutor);
    }

    /**
     * Build command executor and register it to the current built command item which can not be ROOT as one executable node.<br/>
     *
     * @param executor implements of Executable interface
     * @param usage    usage info for this command executor
     * @throws CommandBuildException runtime exception
     */
    public void executor(Executable executor, final String usage) throws CommandBuildException {
        if (CommandItem.ROOT.equals(prevItem)) {
            throw new CommandBuildException("Executor can not be registered behind Root.");
        }

        CommandExecutor commandExecutor = new CommandExecutor(executor, usage);

        commandItemManager.bindExecutor(prevItem, commandExecutor);
    }

    /**
     * Judge can register main item.
     *
     * @return can register
     */
    protected boolean canRegisterMain() {
        return CommandItem.ROOT.equals(prevItem) ||
                (CommandItemType.RESERVED_WORD.getValue() == prevItem.getType() && prevItem.getSubName() == null);
    }
}
