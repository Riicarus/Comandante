package com.riicarus.comandante.executor;

import com.riicarus.comandante.manage.CommandContext;

/**
 * [FEATURE INFO]<br/>
 * The executable interface.
 *
 * @author Riicarus
 * @create 2022-11-24 13:16
 * @since 1.0
 */
public interface Executable {

    /**
     * Execute method uses command context to get or put intermediate data to interact with related executors.
     *
     * @param context command context
     * @throws Exception command execute exception
     */
    void execute(CommandContext context) throws Exception;

}
