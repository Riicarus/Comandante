package com.riicarus.comandante.executor;

import com.riicarus.comandante.manage.CommandContext;

/**
 * [FEATURE INFO]<br/>
 * 指令执行器, 用于定义指令可执行节点的执行方法
 *
 * @author Skyline
 * @create 2022-10-15 0:09
 * @since 1.0
 */
public interface CommandExecutor {

    /**
     * 指令执行方法, 在指令注册时被定义, 由 CommandDispatcher 进行调用<br/>
     *
     * @param context 指令上下文
     * @throws Exception 执行时抛出的异常
     */
    void execute(CommandContext context) throws Exception;

}
