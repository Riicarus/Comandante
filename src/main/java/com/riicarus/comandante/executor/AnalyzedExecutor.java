package com.riicarus.comandante.executor;

import java.util.LinkedList;
import java.util.List;

/**
 * [FEATURE INFO]<br/>
 * Analyzed command executor with arguments.
 *
 * @author Riicarus
 * @create 2023-4-15 20:53
 * @since 1.0.0
 */
public class AnalyzedExecutor {

    private final CommandExecutor executor;
    /**
     * Argument of this executor.
     */
    private final List<String> arguments = new LinkedList<>();

    public AnalyzedExecutor(CommandExecutor executor, List<String> arguments) {
        this.executor = executor;
        this.arguments.addAll(arguments);
    }

    public CommandExecutor getExecutor() {
        return executor;
    }

    public List<String> getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return "AnalyzedExecutor{" +
                "executor=" + executor +
                ", arguments=" + arguments +
                '}';
    }
}
