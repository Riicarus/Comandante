package com.riicarus.comandante.exception;

/**
 * [FEATURE INFO]<br/>
 * Command syntax exception, used to throw the syntax exception during the command dispatching process.
 *
 * @author Riicarus
 * @create 2022-10-15 16:01
 * @since 1.0
 */
public class CommandSyntaxException extends RuntimeException {

    private static final String SINGLE_ERROR_MESSAGE = "You've got a command syntax exception.";
    private static final String COMPLEX_ERROR_MESSAGE = "You've got a command syntax exception: ";

    public CommandSyntaxException() {
        super(SINGLE_ERROR_MESSAGE);
    }

    public CommandSyntaxException(Throwable cause) {
        super(SINGLE_ERROR_MESSAGE, cause);
    }

    public CommandSyntaxException(String message) {
        super(COMPLEX_ERROR_MESSAGE + message);
    }

    public CommandSyntaxException(String message, Throwable cause) {
        super(COMPLEX_ERROR_MESSAGE + message, cause);
    }
}
