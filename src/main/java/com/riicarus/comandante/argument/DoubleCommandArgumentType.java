package com.riicarus.comandante.argument;

import com.riicarus.comandante.exception.CommandSyntaxException;

/**
 * [FEATURE INFO]<br/>
 * Double 类型参数
 *
 * @author Riicarus
 * @create 2022-11-5 13:37
 * @since 1.0
 */
public class DoubleCommandArgumentType extends CommandArgumentType<Double> {

    public DoubleCommandArgumentType() {
        super("double");
    }

    @Override
    public Double parse(String arg) throws CommandSyntaxException {
        try {
            return Double.parseDouble(arg);
        } catch (NumberFormatException e) {
            throw new CommandSyntaxException("arg[" + arg + "] cannot be converted to Double.", e.getCause());
        }
    }

}
