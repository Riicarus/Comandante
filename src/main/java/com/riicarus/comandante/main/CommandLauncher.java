package com.riicarus.comandante.main;

import com.riicarus.comandante.command.InnerCommand;
import com.riicarus.comandante.config.CommandConfig;
import com.riicarus.comandante.exception.CommandLoadException;
import com.riicarus.comandante.exception.CommandProduceException;
import com.riicarus.comandante.manage.CommandRegister;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * [FEATURE INFO]<br/>
 * The API for outsider, there's only static method for global usage.<br/>
 * The main feature is accomplished by the maintained Comandante instance.
 * <br/>
 * <br/>
 * Main feature:<br/>
 * 1. Provide command register api: register()<br/>
 * 2. Provide command process thread's(CommandRunner) open and close api: enable(), disable()<br/>
 * 3. Provide global output redirection api: redirectOutput()<br/>
 * 4. Provide command string input api, used to dispatch and execute command: dispatchToCache()<br/>
 *
 * @author Riicarus
 * @create 2022-11-8 13:16
 * @since 1.2
 */
public class CommandLauncher {

    /**
     * The single instance of core class Comandante.
     */
    public static final Comandante COMANDANTE = new Comandante();

    static {
        InnerCommand.defineCommand();
        CommandConfig.loadConfig();
    }

    private CommandLauncher() {
    }

    /**
     * The command register method, return a command register, used to get command builder from it.
     *
     * @return CommandRegister
     */
    public static CommandRegister register() {
        return COMANDANTE.getCommandRegister();
    }

    /**
     * Start a CommandRunner thread.
     */
    public static void enable() {
        COMANDANTE.startCommandRunner();
    }

    /**
     * Stop the current CommandRunner thread.
     */
    public static void disable() {
        COMANDANTE.stop();
    }

    /**
     * Redirect CommandLogger's global output.
     *
     * @param outputStream destination output stream
     */
    public static void redirectOutput(OutputStream outputStream) {
        try {
            CommandLogger.setOut(outputStream);
        } catch (CommandLoadException e) {
            CommandLogger.log(e.getMessage());
        }
    }

    /**
     * Redirect CommandLogger's global output with the given charset.
     *
     * @param outputStream destination output stream
     * @param charsets     output stream's charset
     */
    public static void redirectOutput(OutputStream outputStream, StandardCharsets charsets) {
        try {
            CommandLogger.setOut(outputStream, charsets);
        } catch (CommandLoadException | UnsupportedEncodingException e) {
            CommandLogger.setOut(outputStream);
        }
    }

    /**
     * Set the output stream of CommandLogger's log file.
     *
     * @param path the absolute path of log file
     */
    public static void setLogFile(String path) {
        try {
            CommandLogger.setLog(path);
        } catch (CommandLoadException e) {
            CommandLogger.log(e.getMessage());
        }
    }

    /**
     * Set the output stream of CommandLogger's log file with the given charset.
     *
     * @param path     the absolute path of log file
     * @param charsets the charset of the log file stream
     */
    public static void setLogFile(String path, StandardCharsets charsets) {
        try {
            CommandLogger.setLog(path, charsets);
        } catch (CommandLoadException e) {
            CommandLogger.log(e.getMessage());
        }
    }

    /**
     * The interface for input command string.<br/>
     * We firstly store the command to the cache queue of CommandInputHandler and wait for consume.
     *
     * @param command command input stream
     */
    public static void dispatchToCache(String command) {
        try {
            COMANDANTE.getIoHandler().input(command);
        } catch (CommandProduceException e) {
            CommandLogger.log(e.getMessage());
        }
    }
}
