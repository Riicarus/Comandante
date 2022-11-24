package com.riicarus.comandante.executor;

import com.riicarus.comandante.manage.CommandContext;

/**
 * [FEATURE INFO]<br/>
 * 可执行接口
 *
 * @author Riicarus
 * @create 2022-11-24 13:16
 * @since 1.0.0
 */
public interface Executable {

    /**
     * 执行方法
     *
     * @param context 指令上下文
     * @throws Exception 执行异常
     */
    void execute(CommandContext context) throws Exception;

}
