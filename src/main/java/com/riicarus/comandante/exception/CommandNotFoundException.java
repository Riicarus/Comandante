package com.riicarus.comandante.exception;

import com.riicarus.comandante.manage.CommandContext;
import com.riicarus.comandante.manage.CommandSuggester;

/**
 * [FEATURE INFO]<br/>
 * 无法找到对应指令异常, 用于抛出指令在分发过程中出现的运行时异常
 *
 * @author Skyline
 * @create 2022-10-16 18:57
 * @since 1.0
 */
public class CommandNotFoundException extends RuntimeException {

    private CommandSuggester suggester;

    public CommandNotFoundException(String message) {
        super(message);
    }

    public CommandNotFoundException(String message, CommandContext context, String notFountStr) {
        super(message + new CommandSuggester(context, notFountStr).suggest());
    }
}
