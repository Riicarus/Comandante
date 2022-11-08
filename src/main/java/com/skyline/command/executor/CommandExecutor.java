package com.skyline.command.executor;

/**
 * [FEATURE INFO]<br/>
 * 指令执行方法
 *
 * @author Skyline
 * @create 2022-10-15 0:09
 * @since 1.0.0
 */
public interface CommandExecutor {

    void execute(Object... args) throws Exception;

}
