package com.skyline.command.main;

import com.skyline.command.command.InnerCommand;
import com.skyline.command.config.Config;
import com.skyline.command.exception.CommandProduceException;
import com.skyline.command.manage.CommandBuilder;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * [FEATURE INFO]<br/>
 * 对外 API, 只有静态方法, 可全局使用<br/>
 * 核心功能主要由维护的核心类 SkyCommand 单例实现的<br/>
 * <br/>
 *
 * 主要功能:<br/>
 *  1. 提供指令注册接口: register()<br/>
 *  2. 提供指令处理线程 CommandRunner 的启动和关闭接口: enable(), disable()<br/>
 *  3. 提供 Logger 全局输出重定向接口: redirectOutput()<br/>
 *  4. 提供指令字符串输入接口, 用于分发并执行外界指令: dispatchToCache()<br/>
 *  5. 提供获取已加载指令接口: listAllExecutionCommand()<br/>
 *
 * @author Skyline
 * @create 2022-11-8 13:16
 * @since 1.2
 */
public class CommandUtil {

    /**
     * 核心类单例, 饿汉式
     */
    public static final SkyCommand SKY_COMMAND = new SkyCommand();

    static {
        InnerCommand.defineCommand();
        Config.loadConfig();
    }

    private CommandUtil() {}

    /**
     * 指令注册接口, 返回一个提供注册功能的指令构建器
     *
     * @return 指令构建器
     */
    public static CommandBuilder register() {
        return SKY_COMMAND.getCommandRegister().getBuilder();
    }

    /**
     * 获取所有已加载指令的 Execution 部分
     *
     * @return 所有已加载指令的 Execution 部分集合
     */
    public static Set<String> listAllExecutionCommand() {
        return SKY_COMMAND.listAllExecutionCommand();
    }

    /**
     * 启动 CommandRunner 线程
     */
    public static void enable() {
        SKY_COMMAND.startCommandRunner();
    }

    /**
     * 结束 CommandRunner 线程
     */
    public static void disable() {
        SKY_COMMAND.stop();
    }

    /**
     * 重定向指令插件 Logger 全局输出
     *
     * @param outputStream 目标输出流
     */
    public static void redirectOutput(OutputStream outputStream) {
        Logger.setOut(outputStream);
    }

    /**
     * 重定向指令插件 Logger 全局输出
     *
     * @param outputStream 目标输出流
     * @param charsets 输出流字符集
     */
    public static void redirectOutput(OutputStream outputStream, StandardCharsets charsets) {
        try {
            Logger.setOut(outputStream, charsets);
        } catch (UnsupportedEncodingException e) {
            Logger.setOut(outputStream);
        }
    }

    /**
     * 外界指令字符串输入接口<br/>
     * 先将输入的指令保存到 CommandInputHandler 的缓存队列中, 等待 CommandRunner 去消费<br/>
     *
     * @param command 指令字符串
     */
    public static void dispatchToCache(String command) {
        try {
            SKY_COMMAND.getIoHandler().input(command);
        } catch (CommandProduceException e) {
            Logger.log(e.getMessage());
        }
    }
}
