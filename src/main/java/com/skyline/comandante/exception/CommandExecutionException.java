package com.skyline.comandante.exception;

/**
 * [FEATURE INFO]<br/>
 * 指令执行异常, 用于抛出指令在执行过程中出现的受检异常
 *
 * @author Skyline
 * @create 2022-10-15 16:15
 * @since 1.0
 */
public class CommandExecutionException extends Exception {

    public CommandExecutionException(String message) {
        super(message);
    }

}
