package com.riicarus.comandante.main;

import com.riicarus.comandante.exception.CommandLoadException;
import com.riicarus.comandante.manage.CommandDispatcher;
import com.riicarus.comandante.manage.CommandInputHandler;
import com.riicarus.comandante.manage.CommandRegister;
import com.riicarus.util.asserts.Asserts;

/**
 * [FEATURE INFO]<br/>
 * Comandante is the interact center, maintains the CommandDispatcher and InputHandler. <br/>
 * It uses a thread to handle command input, analysis and execution. <br/>
 *
 * @author Riicarus
 * @create 2022-10-15 16:23
 * @since 1.0
 */
public class Comandante {
    /**
     * The command dispatcher dispatches command inputs given by input handler and executes them.
     */
    private final CommandDispatcher commandDispatcher;
    /**
     * Get command input.
     */
    private final CommandInputHandler commandInputHandler;
    /**
     * The thread running CommandRunner, which is the command analyzing and executing thread.
     */
    private final Thread commandRunnerThread;
    /**
     * If the command runner thread is running.
     */
    private volatile boolean run = false;

    /**
     * Do not expose contractor outside, just provide to CommandLauncher to expose least API.
     */
    protected Comandante() {
        this.commandDispatcher = new CommandDispatcher();
        this.commandInputHandler = new CommandInputHandler();
        this.commandRunnerThread = new Thread(new CommandRunner(this), "CommandRunnerThread");
    }

    /**
     * Start the command runner thread, ensure there's only one thread which is running.
     */
    protected synchronized void startCommandRunner() throws CommandLoadException {
        Asserts.isFalse(run, new CommandLoadException("CommandRunner is already running."));

        commandRunnerThread.start();

        run = true;
    }

    protected CommandRegister getCommandRegister() {
        return commandDispatcher.getCommandRegister();
    }

    protected CommandInputHandler getIoHandler() {
        return commandInputHandler;
    }

    public CommandDispatcher getCommandDispatcher() {
        return commandDispatcher;
    }

    /**
     * Stop runner thread.<br/>
     * We do not suppose you to close the stream of CommandLogger
     * because there may be some command register actions which will use the CommandLogger.
     */
    protected void stop() {
        run = false;
    }

    /**
     * Use to create the command runner thread, we prefer that there's a single thread to handle the command process work.<br/>
     * It's mainly used to process the command input strings passing through InputHandler, dispatches and executes them.
     */
    static class CommandRunner implements Runnable {

        private final Comandante comandante;

        public CommandRunner(Comandante comandante) {
            this.comandante = comandante;
        }

        @Override
        public void run() {
            while (comandante.run) {
                String command;
                try {
                    command = comandante.commandInputHandler.consume();
                    comandante.commandDispatcher.dispatch(command);
                } catch (Exception e) {
                    CommandLogger.log(e.getMessage());
                } finally {
                    CommandLogger.log("");
                }
            }
        }
    }
}
