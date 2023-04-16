package com.riicarus.comandante.manage;

import com.riicarus.comandante.exception.CommandExecutionException;
import com.riicarus.comandante.exception.CommandNotFoundException;
import com.riicarus.comandante.executor.AnalyzedExecutor;
import com.riicarus.util.exception.NullObjectException;

import java.util.List;

/**
 * [FEATURE INFO]<br/>
 * Command Dispatcher
 *
 * @author Riicarus
 * @create 2022-10-15 23:27
 * @since 1.0
 */
public class CommandDispatcher {

    /**
     * CommandRegister maintains the registered command items, used in the GrammarAnalyzer.
     */
    private final CommandRegister commandRegister;
    /**
     * GrammarAnalyzer is used to parse command input and returns the analyzed executors.
     */
    private final GrammarAnalyzer grammarAnalyzer;

    public CommandDispatcher() {
        this.commandRegister = new CommandRegister();
        this.grammarAnalyzer = new GrammarAnalyzer(commandRegister.getCommandItemManager());
    }

    public CommandDispatcher(CommandRegister commandRegister) {
        this.commandRegister = commandRegister;
        this.grammarAnalyzer = new GrammarAnalyzer(commandRegister.getCommandItemManager());
    }

    /**
     * Dispatch and execute command
     *
     * @param commandStr command string
     * @throws CommandExecutionException runtime exception
     * @throws CommandNotFoundException  runtime exception
     * @throws NullObjectException       runtime exception
     */
    public void dispatch(final String commandStr) throws CommandExecutionException, CommandNotFoundException, NullObjectException {
        List<AnalyzedExecutor> executors = grammarAnalyzer.analyze(commandStr);
        System.out.println(executors);

        CommandContext context = new CommandContext();
        for (AnalyzedExecutor executor : executors) {
            try {
                executor.execute(context);
            } catch (Exception e) {
                throw new CommandExecutionException(e);
            }
        }
    }

    public CommandRegister getCommandRegister() {
        return commandRegister;
    }
}
