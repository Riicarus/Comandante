package com.skyline.command.argument;

/**
 * [FEATURE INFO]<br/>
 * String 类型参数
 *
 * @author Skyline
 * @create 2022-10-15 15:55
 * @since 1.0
 */
public class StringCommandArgumentType extends CommandArgumentType<String> {

    @Override
    public String parse(final String arg) {
        return arg;
    }

}
