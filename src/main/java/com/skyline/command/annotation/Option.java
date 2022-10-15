package com.skyline.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * [FEATURE INFO]<br/>
 * 指令 option 部分
 *
 * @author Skyline
 * @create 2022-10-15 15:10
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Option {

    /**
     * 长指令
     *
     * @return 长指令
     */
    String name();

    /**
     * 对应的短指令, 默认为空
     *
     * @return 短指令
     */
    String alias() default "";

    /**
     * 参数, 默认为空
     *
     * @return 参数注解数组
     */
    Argument[] args() default {};

}
