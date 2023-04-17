package com.riicarus.comandante.executor;

import java.util.LinkedList;
import java.util.List;

/**
 * [FEATURE INFO]<br/>
 * Analyzed command executor with arguments.
 *
 * @author Riicarus
 * @create 2023-4-15 20:53
 * @since 3.0
 */
public class AnalyzedExecutor {
    /**
     * The command executor needs to be executed.
     */
    private CommandExecutor commandExecutor;
    /**
     * Arguments is the arguments getting from command string.
     */
    private final List<String> arguments = new LinkedList<>();
    /**
     * Used in the pipeline feature. The pipeFromExecutor's result will used as the input argument of executor.
     */
    private AnalyzedExecutor pipeFromExecutor;

    public CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    public void setCommandExecutor(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    public AnalyzedExecutor getPipeFromExecutor() {
        return pipeFromExecutor;
    }

    public void setPipeFromExecutor(AnalyzedExecutor pipeFromExecutor) {
        this.pipeFromExecutor = pipeFromExecutor;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments.addAll(arguments);
    }

    public boolean isPipe() {
        return pipeFromExecutor != null;
    }

    public Object execute() throws Exception {
        if (isPipe()) {
            return commandExecutor.execute(arguments, pipeFromExecutor.execute());
        }
        return commandExecutor.execute(arguments, null);
    }
}
