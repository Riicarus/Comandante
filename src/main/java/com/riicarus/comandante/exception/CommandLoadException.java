package com.riicarus.comandante.exception;

/**
 * [FEATURE INFO]<br/>
 * Command load exception, used to throw the exception during command loading or launching process.
 *
 * @author Riicarus
 * @create 2022-10-15 19:28
 * @since 1.0
 */
public class CommandLoadException extends RuntimeException {

    public CommandLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandLoadException(String message) {
        super(message);
    }
}
