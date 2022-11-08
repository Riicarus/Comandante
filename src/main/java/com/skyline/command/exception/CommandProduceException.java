package com.skyline.command.exception;

/**
 * [FEATURE INFO]<br/>
 * 指令生产异常, 用于抛出外部指令字符串存入 CommandInputHandler 指令消费缓冲区时出现的受检异常
 *
 * @author Skyline
 * @create 2022-11-8 15:26
 * @since 1.2
 */
public class CommandProduceException extends Exception {

    public CommandProduceException() {
    }

}
