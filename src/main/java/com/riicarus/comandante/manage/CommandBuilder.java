package com.riicarus.comandante.manage;

import com.riicarus.comandante.argument.CommandArgumentType;
import com.riicarus.comandante.exception.CommandBuildException;
import com.riicarus.comandante.executor.CommandExecutor;
import com.riicarus.comandante.executor.Executable;
import com.riicarus.util.asserts.Asserts;

/**
 * [FEATURE INFO]<br/>
 * Command Builder is used to build a command<br/>
 * <p>
 * function:<br/>
 * 1. use main() to define a MainNode<br/>
 * 4. use opt() to define an OptionNode<br/>
 * 5. use arg() to define an ArgumentNode<br/>
 * 6. use executor() to define a command executor, which will be registered to it's owner node<br/>
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

    private CommandItem prevItem;
    private CommandItem prevMainItem;

    public CommandBuilder(final CommandItemManager commandItemManager) {
        this.commandItemManager = commandItemManager;
        this.prevItem = CommandItem.ROOT;
        this.prevMainItem = CommandItem.ROOT;
    }

    /**
     * Build MainNode, <br/>
     * Register MainNode to the previous node(current node), which must be RootNode or MainNode.<br/>
     * <br/>
     * Update CurrentNode and CurrentExecutionNode.<br/>
     * <br/>
     * Will automatically add a CommandHelper, which is used to provide 'xxx -h/--help' command<br/>
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
     * The OptionNode can only have full name without alias
     *
     * @param name full name of option node
     * @return CommandBuilder
     * @throws CommandBuildException runtime exception
     */
    public CommandBuilder opt(String name) throws CommandBuildException {
        return opt(name, "");
    }


    /**
     * Build OptionNode,<br/>
     * If there's no same node with the name or alias in MainNode, register it to current node.<br/>
     * OptionNodes will not form a chain, but will only be register to the MainNode.<br/>
     * <br/>
     * Update CurrentNode after register.<br/>
     * <br/>
     *
     * @param name  full name of option node
     * @param alias alias for full name, usually the first character of full name
     * @return CommandBuilder
     * @throws CommandBuildException runtime exception
     */
    public CommandBuilder opt(String name, String alias) throws CommandBuildException {
        Asserts.notEmpty(name, new CommandBuildException("Node name can not be null."));

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
     * Build ArgumentNode<br/>
     * Register it to CurrentNode which can not be RootNode.<br/>
     * <br/>
     * Update CurrentNode after register.<br/>
     * <br/>
     *
     * @param name command string item, name of argument node
     * @param type CommandArgumentType
     * @return CommandBuilder
     * @throws CommandBuildException runtime exception
     */
    public <T> CommandBuilder arg(String name, CommandArgumentType<T> type) throws CommandBuildException {
        Asserts.notEmpty(name, new CommandBuildException("Node name can not be null."));
        Asserts.notNull(type, new CommandBuildException("Node type can not be null"));

        if (CommandItem.ROOT.equals(prevItem)) {
            throw new CommandBuildException("Argument item can not be registered behind Root.");
        }

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
     * Build Command Executor and register it to current node which can not be RootNode as one executable node<br/>
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
     * Build command executor and register it to the current built node which can not be RootNode as one executable node<br/>
     *
     * @param executor implements of Executable interface
     * @param usage    usage info for this command
     * @throws CommandBuildException runtime exception
     */
    public void executor(Executable executor, final String usage) throws CommandBuildException {
        if (CommandItem.ROOT.equals(prevItem)) {
            throw new CommandBuildException("Executor can not be registered behind Root.");
        }

        CommandExecutor commandExecutor = new CommandExecutor(executor, usage);

        commandItemManager.bindExecutor(prevItem, commandExecutor);
    }

    protected boolean canRegisterMain() {
        return CommandItem.ROOT.equals(prevItem) || (CommandItemType.RESERVED_WORD.getValue() == prevItem.getType() && prevItem.getSubName() == null);
    }
}
