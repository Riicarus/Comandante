package com.skyline.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * [FEATURE INFO]<br/>
 * 指令 exe 部分
 *
 * @author Skyline
 * @create 2022-10-15 15:05
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Execution {

    /**
     * 指令 exe 部分名称
     *
     * @return exe 部分名称
     */
    String name();

}
