package com.riicarus.comandante.exception;

/**
 * [FEATURE INFO]<br/>
 * 指令构建错误, 用于抛出指令构建过程中的运行时异常
 *
 * @author Skyline
 * @create 2022-10-16 0:09
 * @since 1.0
 */
public class CommandBuildException extends RuntimeException {

    public CommandBuildException(String message, Throwable cause) {
        super(message, cause);
    }

}
