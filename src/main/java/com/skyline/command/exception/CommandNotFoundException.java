package com.skyline.command.exception;

/**
 * [FEATURE INFO]<br/>
 * 无法找到对应指令异常
 *
 * @author Skyline
 * @create 2022-10-16 18:57
 * @since 1.0.0
 */
public class CommandNotFoundException extends RuntimeException {

    public CommandNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
