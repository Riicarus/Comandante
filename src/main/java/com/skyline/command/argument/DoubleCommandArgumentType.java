package com.skyline.command.argument;

import com.skyline.command.exception.CommandSyntaxException;

/**
 * [FEATURE INFO]<br/>
 * Double 类型参数
 *
 * @author Skyline
 * @create 2022-11-5 13:37
 * @since 1.0
 */
public class DoubleCommandArgumentType extends CommandArgumentType<Double> {

    @Override
    public Double parse(String arg) throws CommandSyntaxException {
        try {
            return Double.parseDouble(arg);
        } catch (NumberFormatException e) {
            throw new CommandSyntaxException("arg[" + arg + "] cannot be converted to Double.", e.getCause());
        }
    }

}
