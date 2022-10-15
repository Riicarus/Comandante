package com.skyline.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * [FEATURE INFO]<br/>
 * 指令 sub action 部分
 *
 * @author Skyline
 * @create 2022-10-15 15:08
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubAction {

    /**
     * subAction 名称
     *
     * @return subAction 指令名称
     */
    String name();

    /**
     * 选项
     *
     * @return 选项(option)注解
     */
    Option option() default @Option(name = "");

    /**
     * 参数, 默认为空
     *
     * @return 参数注解数组
     */
    Argument[] args() default {};

}
