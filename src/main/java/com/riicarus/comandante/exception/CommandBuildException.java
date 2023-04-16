package com.riicarus.comandante.exception;

/**
 * [FEATURE INFO]<br/>
 * Command build exception, used to throw the exception during command build process
 *
 * @author Riicarus
 * @create 2022-10-16 0:09
 * @since 1.0
 */
public class CommandBuildException extends RuntimeException {

    public CommandBuildException(String message) {
        super(message);
    }

}
