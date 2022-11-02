package com.skyline.command.exception;

/**
 * [FEATURE INFO]<br/>
 * 指令加载异常
 *
 * @author Skyline
 * @create 2022-10-15 19:28
 * @since 1.0.0
 */
public class CommandLoadException extends RuntimeException {

    public CommandLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandLoadException(String message) {
        super(message);
    }
}
