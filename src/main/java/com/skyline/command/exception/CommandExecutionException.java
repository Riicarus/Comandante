package com.skyline.command.exception;

/**
 * [FEATURE INFO]<br/>
 * 指令执行错误
 *
 * @author Skyline
 * @create 2022-10-15 16:15
 * @since 1.0.0
 */
public class CommandExecutionException extends RuntimeException {

    public CommandExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

}
