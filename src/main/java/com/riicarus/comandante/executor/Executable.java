package com.riicarus.comandante.executor;

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
     * @param args method arguments
     * @param pipedArgs method arguments getting from pipeline
     * @return result
     * @throws Exception command execute exception
     */
    Object execute(Object args, Object pipedArgs) throws Exception;

}
