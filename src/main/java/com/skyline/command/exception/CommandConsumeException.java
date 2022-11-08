package com.skyline.command.exception;

/**
 * [FEATURE INFO]<br/>
 * 指令消费异常, 用于抛出 CommandRunner 线程消费指令时出现的受检异常
 *
 * @author Skyline
 * @create 2022-11-8 15:27
 * @since 1.2
 */
public class CommandConsumeException extends Exception {

    public CommandConsumeException() {
    }

}
