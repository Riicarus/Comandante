package com.riicarus.comandante.manage;

import com.riicarus.comandante.exception.CommandConsumeException;
import com.riicarus.comandante.exception.CommandProduceException;
import com.riicarus.comandante.main.CommandLogger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * [FEATURE INFO]<br/>
 * The command input handler uses producer-consumer pattern and maintains a block command input queue with 10 max size.<br/>
 * The consumer is CommandRunner, and the producer is the API provided by CommandLauncher: CommandLauncher.dispatchToCache(String);<br/>
 *
 * @author Riicarus
 * @create 2022-10-15 16:31
 * @since 1.0
 */
public class CommandInputHandler {

    /**
     * The command input blocking queue.
     */
    private static final BlockingQueue<String> commandQueue = new ArrayBlockingQueue<>(10, true);

    /**
     * Produce commands and put them to the blocking queue
     *
     * @param command command input stream
     * @throws CommandProduceException exception
     */
    private void produce(String command) throws CommandProduceException {
        try {
            commandQueue.put(command);
        } catch (InterruptedException e) {
            throw new CommandProduceException();
        }
    }

    /**
     * Consume one command from the blocking queue, invoked by CommandRunner.
     *
     * @return command string
     * @throws CommandConsumeException exception
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
     * The command input method provided to outsider, uses produce() method to put the input command to the blocking queue.
     *
     * @param command command input string
     * @throws CommandProduceException exception
     */
    public void input(String command) throws CommandProduceException {
        CommandLogger.log("COMMAND RECEIVER ECHO: " + command);
        produce(command);
    }

}
