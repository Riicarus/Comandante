package com.skyline.command.command;

import com.skyline.command.SkyCommand;

/**
 * [FEATURE INFO]<br/>
 * 基础指令抽象类
 *
 * @author Skyline
 * @create 2022-10-28 19:46
 * @since 1.0.0
 */
public abstract class BaseCommand {

    protected final SkyCommand SKY_COMMAND;

    public BaseCommand(SkyCommand SKY_COMMAND) {
        this.SKY_COMMAND = SKY_COMMAND;
    }

    /**
     * 指令定义
     */
    public abstract void defineCommand();
}
