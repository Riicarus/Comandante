package com.skyline.command.exception;

/**
 * [FEATURE INFO]<br/>
 * 指令语法错误
 *
 * @author Skyline
 * @create 2022-10-15 16:01
 * @since 1.0.0
 */
public class CommandSyntaxException extends RuntimeException {

    private static final String ERROR_MESSAGE = "You've got a command syntax exception.";

    public CommandSyntaxException(Throwable cause) {
        super(ERROR_MESSAGE, cause);
    }

    public CommandSyntaxException(String message) {
        super(ERROR_MESSAGE + " " + message);
    }
}
