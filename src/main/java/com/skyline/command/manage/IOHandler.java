package com.skyline.command.manage;

/**
 * [FEATURE INFO]<br/>
 * IO 设置
 *
 * @author Skyline
 * @create 2022-10-15 16:31
 * @since 1.0.0
 */
public interface IOHandler {

    String doGetCommand();

    void redirectOutput();

}
