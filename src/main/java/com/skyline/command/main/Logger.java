package com.skyline.command.main;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * [FEATURE INFO]<br/>
 * 全局日志输出, 支持设置输出流进行输出重定向<br/>
 * 默认输出流为 System.out<br/>
 * 重定向输出流后, 所有的输出都会被重定向<br/>
 * 重定向方法限制为 protected, 由 CommandUtil 调用进行重定向, 避免暴露给外界进行不必要的重定向<br/>
 * <br/>
 * 建议所有的输出都是用 Logger.log() 进行处理<br/>
 * <br/>
 * 只提供静态 api, 不支持新建实例<br/>
 *
 * @author Skyline
 * @create 2022-11-8 17:06
 * @since 1.2
 */
public class Logger {

    /**
     * 输出流
     */
    private static PrintStream printStream = System.out;

    private Logger() {
    }

    protected static void setOut(OutputStream out) {
        printStream = new PrintStream(out, true);
    }

    protected static void setOut(OutputStream out, StandardCharsets charsets) throws UnsupportedEncodingException {
        printStream = new PrintStream(out, true, charsets.toString());
    }

    public static void log(String s) {
        printStream.println(s);
    }

    public static void close() {
        printStream.close();
    }
}
