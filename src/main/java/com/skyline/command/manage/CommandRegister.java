package com.skyline.command.manage;

import com.skyline.command.tree.RootCommandNode;

/**
 * [FEATURE INFO]<br/>
 * 指令注册器, 维护指令树根节点, 提供指令构建器
 *
 * @author Skyline
 * @create 2022-10-15 10:46
 * @since 1.0
 */
public class CommandRegister {

    /**
     * 指令树根节点
     */
    private final RootCommandNode rootCommandNode = new RootCommandNode();

    public RootCommandNode getRootCommandNode() {
        return rootCommandNode;
    }

    /**
     * 获取指令构建器
     *
     * @return 指令构建器
     */
    public CommandBuilder getBuilder() {
        return new CommandBuilder(rootCommandNode);
    }

}
