package com.riicarus.comandante.main;

import com.riicarus.comandante.exception.CommandLoadException;
import com.riicarus.util.Asserts;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * [FEATURE INFO]<br/>
 * 全局日志输出, 支持设置输出流进行输出重定向<br/>
 * 默认输出流为 System.out<br/>
 * 重定向输出流后, 所有的输出都会被重定向<br/>
 * 重定向方法限制为 protected, 由 CommandLauncher 调用进行重定向, 避免暴露给外界进行不必要的重定向<br/>
 * <br/>
 * 建议所有的输出都是用 CommandLogger.log() 进行处理<br/>
 * <br/>
 * 只提供静态 api, 不支持新建实例<br/>
 *
 * @author Skyline
 * @create 2022-11-8 17:06
 * @since 1.2
 */
public class CommandLogger {

    /**
     * 输出流
     */
    private static PrintStream printStream = System.out;
    /**
     * 日志输出流
     */
    private static PrintStream logStream;
    /**
     * 日志输出文件路径, 只能是绝对路径
     */
    private static String logFilePath;
    /**
     * 能否进行日志输出
     */
    private static boolean loggable = false;

    private CommandLogger() {
    }

    /**
     * 设置全局输出流
     *
     * @param out 输出流
     */
    protected static void setOut(OutputStream out) throws CommandLoadException {
        Asserts.notNull(out, new CommandLoadException("OutputStream can not be null."));

        printStream = new PrintStream(out, true);
    }

    /**
     * 设置全局输出流
     *
     * @param out 输出流
     * @param charsets 输出流字符集
     * @throws UnsupportedEncodingException 解码方式不支持异常
     */
    protected static void setOut(OutputStream out, StandardCharsets charsets) throws UnsupportedEncodingException, CommandLoadException {
        Asserts.notNull(out, new CommandLoadException("OutputStream can not be null."));
        Asserts.notNull(charsets, new CommandLoadException("Charsets can not be null."));

        printStream = new PrintStream(out, true, charsets.toString());
    }

    /**
     * 设置日志文件输出流
     *
     * @param path 输出文件路径, 必须为绝对路径
     */
    protected static void setLog(String path) throws CommandLoadException {
        Asserts.notEmpty(path, new CommandLoadException("Log path can not be null or empty"));

        if (path.startsWith("/") || path.indexOf(":") > 0) {
            try {
                FileOutputStream fos = new FileOutputStream(path);
                logStream = new PrintStream(fos, true);
                loggable = true;
            } catch (FileNotFoundException e) {
                loggable = false;
                logFilePath = null;
                logStream = null;
            }
        }
    }

    /**
     * 设置日志文件输出流
     *
     * @param path 输出文件路径, 必须为绝对路径
     * @param charsets 输出流字符集
     */
    protected static void setLog(String path, StandardCharsets charsets) throws CommandLoadException {
        Asserts.notEmpty(path, new CommandLoadException("Log path can not be null or empty"));
        Asserts.notNull(charsets, new CommandLoadException("Charsets can not be null."));

        if (path.startsWith("/") || path.indexOf(":") > 0) {
            try {
                FileOutputStream fos = new FileOutputStream(path);
                logStream = new PrintStream(fos, true, charsets.toString());
                loggable = true;
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                loggable = false;
                logFilePath = null;
                logStream = null;
            }
        }
    }

    /**
     * 输出信息
     *
     * @param s 要输出的内容
     */
    public static void log(String s) {
        printStream.println(s);
        if (loggable) {
            logStream.println(s);
        }
    }

    /**
     * 关闭所有的流
     */
    public static void close() {
        printStream.close();
        if (loggable) {
            logStream.close();
        }
    }
}
