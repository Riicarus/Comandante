package com.skyline.command.executor;

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
     * @param args 方法需要传入的参数
     * @throws Exception 执行时抛出的异常
     */
    void execute(Object... args) throws Exception;

}
