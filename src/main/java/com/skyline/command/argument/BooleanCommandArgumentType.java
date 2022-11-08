package com.skyline.command.argument;

import com.skyline.command.exception.CommandSyntaxException;

/**
 * [FEATURE INFO]<br/>
 * Boolean 类型参数
 *
 * @author Skyline
 * @create 2022-11-5 13:35
 * @since 1.0
 */
public class BooleanCommandArgumentType extends CommandArgumentType<Boolean> {

    @Override
    public Boolean parse(String arg) throws CommandSyntaxException {
        try {
            return Boolean.parseBoolean(arg);
        } catch (NumberFormatException e) {
            throw new CommandSyntaxException("arg[" + arg + "] cannot be converted to Boolean.", e.getCause());
        }
    }

}
