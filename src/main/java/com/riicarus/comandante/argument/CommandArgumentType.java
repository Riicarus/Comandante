package com.riicarus.comandante.argument;

import com.riicarus.comandante.exception.CommandSyntaxException;

/**
 * [FEATURE INFO]<br/>
 * 指令参数类型
 *
 * @author Skyline
 * @create 2022-10-15 0:18
 * @since 1.0
 */
public abstract class CommandArgumentType<T> {

    public abstract T parse(final String arg) throws CommandSyntaxException;

}
