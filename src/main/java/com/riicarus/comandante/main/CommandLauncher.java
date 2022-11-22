package com.riicarus.comandante.main;

import com.riicarus.comandante.command.InnerCommand;
import com.riicarus.comandante.exception.CommandLoadException;
import com.riicarus.comandante.exception.CommandProduceException;
import com.riicarus.comandante.config.CommandConfig;
import com.riicarus.comandante.manage.CommandBuilder;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * [FEATURE INFO]<br/>
 * 对外 API, 只有静态方法, 可全局使用<br/>
 * 核心功能主要由维护的核心类 Comandante 单例实现的<br/>
 * <br/>
 *
 * 主要功能:<br/>
 *  1. 提供指令注册接口: register()<br/>
 *  2. 提供指令处理线程 CommandRunner 的启动和关闭接口: enable(), disable()<br/>
 *  3. 提供 CommandLogger 全局输出重定向接口: redirectOutput()<br/>
 *  4. 提供指令字符串输入接口, 用于分发并执行外界指令: dispatchToCache()<br/>
 *  5. 提供获取已加载指令接口: listAllExecutionCommand()<br/>
 *
 * @author Skyline
 * @create 2022-11-8 13:16
 * @since 1.2
 */
public class CommandLauncher {

    /**
     * 核心类单例, 饿汉式
     */
    public static final Comandante COMANDANTE = new Comandante();

    static {
        InnerCommand.defineCommand();
        CommandConfig.loadConfig();
    }

    private CommandLauncher() {}

    /**
     * 指令注册接口, 返回一个提供注册功能的指令构建器
     *
     * @return 指令构建器
     */
    public static CommandBuilder register() {
        return COMANDANTE.getCommandRegister().getBuilder();
    }

    /**
     * 获取所有已加载指令的 Execution 部分
     *
     * @return 所有已加载指令的 Execution 部分集合
     */
    public static Set<String> listAllExecutionCommand() {
        return COMANDANTE.listAllExecutionCommand();
    }

    /**
     * 启动 CommandRunner 线程
     */
    public static void enable() {
        COMANDANTE.startCommandRunner();
    }

    /**
     * 结束 CommandRunner 线程
     */
    public static void disable() {
        COMANDANTE.stop();
    }

    /**
     * 重定向指令插件 CommandLogger 全局输出
     *
     * @param outputStream 目标输出流
     */
    public static void redirectOutput(OutputStream outputStream) {
        try {
            CommandLogger.setOut(outputStream);
        } catch (CommandLoadException e) {
            CommandLogger.log(e.getMessage());
        }
    }

    /**
     * 重定向指令插件 CommandLogger 全局输出
     *
     * @param outputStream 目标输出流
     * @param charsets 输出流字符集
     */
    public static void redirectOutput(OutputStream outputStream, StandardCharsets charsets) {
        try {
            CommandLogger.setOut(outputStream, charsets);
        } catch (CommandLoadException | UnsupportedEncodingException e) {
            CommandLogger.setOut(outputStream);
        }
    }

    /**
     * 设置日志文件输出流
     *
     * @param path 日志文件路径, 必须为绝对路径
     */
    public static void setLogFile(String path) {
        try {
            CommandLogger.setLog(path);
        } catch (CommandLoadException e) {
            CommandLogger.log(e.getMessage());
        }
    }

    /**
     * 设置日志文件输出流
     *
     * @param path 日志文件路径, 必须为绝对路径
     * @param charsets 输出流字符集
     */
    public static void setLogFile(String path, StandardCharsets charsets) {
        try {
            CommandLogger.setLog(path, charsets);
        } catch (CommandLoadException e) {
            CommandLogger.log(e.getMessage());
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
            COMANDANTE.getIoHandler().input(command);
        } catch (CommandProduceException e) {
            CommandLogger.log(e.getMessage());
        }
    }
}
