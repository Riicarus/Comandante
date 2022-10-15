package com.skyline.command.exception;

/**
 * [FEATURE INFO]<br/>
 * 指令构建错误
 *
 * @author Skyline
 * @create 2022-10-16 0:09
 * @since 1.0.0
 */
public class CommandBuildException extends RuntimeException {

    public CommandBuildException(String message, Throwable cause) {
        super(message, cause);
    }

}
