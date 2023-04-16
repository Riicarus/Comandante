package com.riicarus.comandante.executor;

import com.riicarus.comandante.manage.CommandContext;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * [FEATURE INFO]<br/>
 * Analyzed command executor with arguments.
 *
 * @author Riicarus
 * @create 2023-4-15 20:53
 * @since 3.0
 */
public class AnalyzedExecutor {

    private final CommandExecutor executor;
    /**
     * Unique id of this analyze executor, used to match executors and arguments.
     */
    private final String uuid;
    /**
     * Argument of this executor.
     */
    private final List<String> arguments = new LinkedList<>();

    public AnalyzedExecutor(CommandExecutor executor, List<String> arguments) {
        this.executor = executor;
        this.arguments.addAll(arguments);
        this.uuid = UUID.randomUUID().toString();
    }

    public void execute(CommandContext context) throws Exception {
        context.put(CommandContext.ARG_DATA_PREFIX + getUuid(), arguments);
        executor.execute(context);
    }

    public CommandExecutor getExecutor() {
        return executor;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return "AnalyzedExecutor{" +
                "executor=" + executor +
                ", arguments=" + arguments +
                '}';
    }
}
