package com.skyline.command;

import com.skyline.command.exception.CommandProduceException;
import com.skyline.command.manage.CommandBuilder;

import java.util.Set;

/**
 * [FEATURE INFO]<br/>
 * API
 *
 * @author Skyline
 * @create 2022-11-8 13:16
 * @since 1.0.0
 */
public class CommandUtil {

    public static final SkyCommand SKY_COMMAND = new SkyCommand();

    private CommandUtil() {}

    public static CommandBuilder register() {
        return SKY_COMMAND.getCommandRegister().getBuilder();
    }

    public static Set<String> listAllExecutionCommand() {
        return SKY_COMMAND.listAllExecutionCommand();
    }

    /**
     * 启动 InnerRunner 线程
     */
    public static void enable() {
        SKY_COMMAND.startSkyCommand();
    }

    public static void disable() {
        SKY_COMMAND.stop();
    }

    /**
     * 先将输入的指令保存到 IOHandler 的缓存队列中, 等待 InnerRunner 去消费
     *
     * @param command 指令字符串
     */
    public static void dispatchToCache(String command) {
        try {
            SKY_COMMAND.getIoHandler().input(command);
        } catch (CommandProduceException e) {
            System.out.println(e.getMessage());
        }
    }
}
