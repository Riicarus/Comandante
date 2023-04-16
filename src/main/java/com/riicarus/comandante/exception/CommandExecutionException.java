package com.riicarus.comandante.exception;

/**
 * [FEATURE INFO]<br/>
 * Command execution exception, used to throw the exception during command execution.
 *
 * @author Riicarus
 * @create 2022-10-15 16:15
 * @since 1.0
 */
public class CommandExecutionException extends RuntimeException {

    public CommandExecutionException(String message) {
        super(message);
    }

    public CommandExecutionException(Throwable cause) {
        super(cause);
    }
}
