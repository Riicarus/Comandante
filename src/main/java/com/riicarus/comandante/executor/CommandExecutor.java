package com.riicarus.comandante.executor;

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
     * The usage info of this executor.
     */
    private final String usage;
    /**
     * Execute interface, built by CommandBuilder.
     */
    private Executable executor;
    /**
     * The complete command string of this executor.
     */
    private String commandString;

    public CommandExecutor(Executable executor, String usage) {
        this.executor = executor;
        this.usage = usage;
    }

    /**
     * Command execute method, invokes the command executor, and it will be invoked by CommandDispatcher<br/>
     *
     * @param args command's arguments
     * @param pipedArgs command's arguments getting from pipeline
     * @return result
     * @throws Exception command execute exception
     */
    public final Object execute(Object args, Object pipedArgs) throws Exception {
        useCount.incrementAndGet();
        return executor.execute(args, pipedArgs);
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

    public String getCommandString() {
        return commandString;
    }

    public void setCommandString(String commandString) {
        this.commandString = commandString;
    }
}
