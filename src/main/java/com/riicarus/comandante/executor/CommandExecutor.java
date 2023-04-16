package com.riicarus.comandante.executor;

import com.riicarus.comandante.manage.CommandContext;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * [FEATURE INFO]<br/>
 * CommandExecutor is used to define the executable item's execute() method and some related fields.<br/>
 * It also maintains its use count and usage info for extension. <br/>
 *
 * @author Riicarus
 * @create 2022-10-15 0:09
 * @since 1.0
 */
public class CommandExecutor {
    /**
     * Executor's invoke count
     */
    private final AtomicInteger useCount = new AtomicInteger(0);
    /**
     * Execute interface, built by CommandBuilder.
     */
    private Executable executor;
    /**
     * The usage info of this executor.
     */
    private final String usage;

    public CommandExecutor(Executable executor, String usage) {
        this.executor = executor;
        this.usage = usage;
    }

    /**
     * Command execute method, invokes the command executor, and it will be invoked by CommandDispatcher<br/>
     *
     * @param context command context
     * @throws Exception command execute exception
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

    public String getUsage() {
        return usage;
    }
}
