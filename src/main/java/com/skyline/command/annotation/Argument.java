package com.skyline.command.annotation;

import com.skyline.command.argument.CommandArgumentType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * [FEATURE INFO]<br/>
 * 指令参数部分
 *
 * @author Skyline
 * @create 2022-10-15 15:09
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Argument {

    /**
     * 参数名称
     *
     * @return 参数名称
     */
    String name();

    /**
     * 参数类型
     *
     * @return 参数类型对应的类
     */
    Class<? extends CommandArgumentType<?>> type();

}
