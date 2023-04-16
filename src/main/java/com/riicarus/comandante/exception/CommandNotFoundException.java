package com.riicarus.comandante.exception;

/**
 * [FEATURE INFO]<br/>
 * Command not found exception, used to throw the exception during the command dispatching process in GrammarAnalyzer.<br/>
 * It's a subclass of CommandSyntaxException.
 *
 * @author Riicarus
 * @create 2022-10-16 18:57
 * @since 1.0
 */
public class CommandNotFoundException extends CommandSyntaxException {

    public CommandNotFoundException(String message) {
        super(message);
    }

}
