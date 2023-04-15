package com.riicarus.comandante.executor;

import com.riicarus.comandante.manage.CommandContext;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * [FEATURE INFO]<br/>
 * CommandExecutor, is used to define the executable ownerNode's execute() and some related fields
 *
 * @author Riicarus
 * @create 2022-10-15 0:09
 * @since 1.0
 */
public class CommandExecutor {
    /**
     * executor invoke count
     */
    private final AtomicInteger useCount = new AtomicInteger(0);
    /**
     * execute interface
     */
    private Executable executor;

    private final String usage;

    public CommandExecutor(Executable executor, String usage) {
        this.executor = executor;
        this.usage = usage;
    }

    /**
     * command execute method, invokes the command executor, and will be invoked by CommandDispatcher<br/>
     *
     * @param context command execute context
     * @throws Exception runtime exception
     */
    public final void execute(CommandContext context) throws Exception {
        useCount.incrementAndGet();
        executor.execute(context);
    }

    public Executable getExecutor() {
        return executor;
    }

    public void setExecutor(Executable executor) {
        this.executor = executor;
    }

    public int getUseCount() {
        return useCount.get();
    }
}
