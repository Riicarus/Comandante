package com.riicarus.comandante.exception;

/**
 * [FEATURE INFO]<br/>
 * 指令执行异常, 用于抛出指令在执行过程中出现的受检异常
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
