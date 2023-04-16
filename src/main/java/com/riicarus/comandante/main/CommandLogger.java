package com.riicarus.comandante.main;

import com.riicarus.comandante.exception.CommandLoadException;
import com.riicarus.util.asserts.Asserts;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

/**
 * [FEATURE INFO]<br/>
 * Global logger supports to redirect the output stream.<br/>
 * The default output stream is System.out<br/>
 * While redirecting the output stream, all output will be redirect to it.<br/>
 * The redirecting method is limited to protected and use CommandLauncher to expose the redirect API to avoid unnecessary output redirection.<br/>
 * <br/>
 * We suggest that all output is processed by CommandLogger.log(). <br/>
 * <br/>
 * Just provide static API, do not create new instance.<br/>
 *
 * @author Riicarus
 * @create 2022-11-8 17:06
 * @since 1.2
 */
public class CommandLogger {
    /**
     * Output stream.
     */
    private static PrintStream printStream = System.out;
    /**
     * Log output stream.
     */
    private static PrintStream logStream;
    /**
     * The log output file stream, which can only be absolute path.
     */
    private static String logFilePath;
    /**
     * Can output log.
     */
    private static boolean loggable = false;
    /**
     * The time format.
     */
    private static final SimpleDateFormat SDF_LOG = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
    private static final String LOG_STRING_PREFIX = ">";

    private CommandLogger() {
    }

    /**
     * Set global output stream.
     *
     * @param out output stream
     */
    protected static void setOut(OutputStream out) throws CommandLoadException {
        Asserts.notNull(out, new CommandLoadException("OutputStream can not be null."));

        printStream = new PrintStream(out, true);
    }

    /**
     * Set global output stream with given charset.
     *
     * @param out output stream
     * @param charsets charset of output stream
     * @throws UnsupportedEncodingException IOException
     */
    protected static void setOut(OutputStream out, StandardCharsets charsets) throws UnsupportedEncodingException, CommandLoadException {
        Asserts.notNull(out, new CommandLoadException("OutputStream can not be null."));
        Asserts.notNull(charsets, new CommandLoadException("Charsets can not be null."));

        printStream = new PrintStream(out, true, charsets.toString());
    }

    /**
     * Set log output stream.
     *
     * @param path output file path, must be absolute path
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
     * Set log output stream with charset.
     *
     * @param path output file path, must be absolute path
     * @param charsets log output charset
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
     * Log a stream to output stream. If log file is set, it will also be put to the file.
     *
     * @param s output string
     */
    public static void log(String s) {
        String logTimeStr = SDF_LOG.format(System.currentTimeMillis());
        String formattedLog = "comandante[" + logTimeStr + "]" + LOG_STRING_PREFIX + s;
        printStream.println(s);
        if (loggable) {
            logStream.println(formattedLog);
        }
    }

    /**
     * Close all output stream.
     */
    public static void close() {
        printStream.close();
        if (loggable) {
            logStream.close();
        }
    }
}
