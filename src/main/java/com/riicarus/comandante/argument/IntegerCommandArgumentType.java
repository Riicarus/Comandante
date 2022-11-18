package com.riicarus.comandante.argument;

import com.riicarus.comandante.exception.CommandSyntaxException;

/**
 * [FEATURE INFO]<br/>
 * Integer 类型参数
 *
 * @author Skyline
 * @create 2022-10-15 16:00
 * @since 1.0
 */
public class IntegerCommandArgumentType extends CommandArgumentType<Integer> {

    public IntegerCommandArgumentType() {
        super("int");
    }

    @Override
    public Integer parse(final String arg) {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw new CommandSyntaxException("arg[" + arg + "] cannot be converted to Integer.", e.getCause());
        }
    }

}
