package com.skyline.command.manage;

import com.skyline.command.tree.RootCommandNode;

/**
 * [FEATURE INFO]<br/>
 * 指令注册器
 *
 * @author Skyline
 * @create 2022-10-15 10:46
 * @since 1.0.0
 */
public class CommandRegister {

    private final RootCommandNode rootCommandNode = new RootCommandNode();

    public RootCommandNode getRootCommandNode() {
        return rootCommandNode;
    }

    public CommandBuilder getBuilder() {
        return new CommandBuilder(rootCommandNode);
    }

}
