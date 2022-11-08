package com.skyline.command;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * [FEATURE INFO]<br/>
 * 全局日志
 *
 * @author Skyline
 * @create 2022-11-8 17:06
 * @since 1.0.0
 */
public class Logger {

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
