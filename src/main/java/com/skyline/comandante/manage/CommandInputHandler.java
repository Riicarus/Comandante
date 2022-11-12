package com.skyline.comandante.manage;

import com.skyline.comandante.main.Logger;
import com.skyline.comandante.exception.CommandConsumeException;
import com.skyline.comandante.exception.CommandProduceException;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * [FEATURE INFO]<br/>
 * 指令输入处理器, 使用生产者消费者模式<br/>
 * 维护了一个指令阻塞队列, 最大指令数量为 10<br/>
 * 消费者为 CommandRunner, 生产者为 CommandUtil 提供的 API: CommandUtil.dispatchToCache(String)<br/>
 *
 * @author Skyline
 * @create 2022-10-15 16:31
 * @since 1.0
 */
public class CommandInputHandler {

    private static final BlockingQueue<String> commandQueue = new ArrayBlockingQueue<>(10, true);

    /**
     * 生产指令, 将其放入阻塞队列
     *
     * @param command 指令字符串
     * @throws CommandProduceException 指令生产异常, 属于受检异常
     */
    private void produce(String command) throws CommandProduceException {
        try {
            commandQueue.put(command);
        } catch (InterruptedException e) {
            throw new CommandProduceException();
        }
    }

    /**
     * 消费指令, 从阻塞队列中取出一条指令, 由 CommandRunner 调用
     *
     * @return 指令字符串
     * @throws CommandConsumeException 指令消费异常, 属于受检异常
     */
    public String consume() throws CommandConsumeException {
        String command ;
        try {
            command = commandQueue.take();
        } catch (InterruptedException e) {
            throw new CommandConsumeException();
        }

        return command;
    }

    /**
     * 提供给外界使用的指令输入方法, 调用 produce() 方法将指令放入阻塞队列
     *
     * @param command 指令字符串
     * @throws CommandProduceException 指令生产异常, 属于受检异常
     */
    public void input(String command) throws CommandProduceException {
        Logger.log("Input command: " + command + ".");
        produce(command);
    }

}
