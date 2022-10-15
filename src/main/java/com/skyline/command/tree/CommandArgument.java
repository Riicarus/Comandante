package com.skyline.command.tree;

import com.skyline.command.argument.CommandArgumentType;

/**
 * [FEATURE INFO]<br/>
 * 指令参数类
 *
 * @author Skyline
 * @create 2022-10-16 1:52
 * @since 1.0.0
 */
public class CommandArgument<T> {

    private String name;

    private CommandArgumentType<T> type;

    public CommandArgument() {
    }

    public CommandArgument(String name, CommandArgumentType<T> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CommandArgumentType<T> getType() {
        return type;
    }

    public void setType(CommandArgumentType<T> type) {
        this.type = type;
    }
}
