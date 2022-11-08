package com.skyline.command.manage;

import com.skyline.command.main.Logger;
import com.skyline.command.exception.CommandConsumeException;
import com.skyline.command.exception.CommandProduceException;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * [FEATURE INFO]<br/>
 * IO
 *
 * @author Skyline
 * @create 2022-10-15 16:31
 * @since 1.0
 */
public class CommandInputHandler {

    private static final BlockingQueue<String> commandQueue = new ArrayBlockingQueue<>(10, true);

    private void produce(String command) throws CommandProduceException {
        try {
            commandQueue.put(command);
        } catch (InterruptedException e) {
            throw new CommandProduceException();
        }
    }

    public String consume() throws CommandConsumeException {
        String command ;
        try {
            command = commandQueue.take();
        } catch (InterruptedException e) {
            throw new CommandConsumeException();
        }

        return command;
    }

    public void input(String command) throws CommandProduceException {
        Logger.log("Input command: " + command + ".");
        produce(command);
    }

}
