package com.skyline.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * [FEATURE INFO]<br/>
 * 指令 action 部分
 *
 * @author Skyline
 * @create 2022-10-15 15:07
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Action {

    /**
     * action 名称
     *
     * @return action 指令名称
     */
    String name();

    /**
     * 子 action, 默认为空
     *
     * @return subAction 注解
     */
    SubAction subAction() default @SubAction(name = "");

    /**
     * 选项, 默认为空
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
