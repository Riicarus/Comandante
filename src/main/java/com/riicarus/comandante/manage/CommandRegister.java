package com.riicarus.comandante.manage;

import com.riicarus.comandante.tree.RootNode;

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
    private final RootNode rootNode = new RootNode();

    public RootNode getRootNode() {
        return rootNode;
    }

    /**
     * 获取指令构建器
     *
     * @return 指令构建器
     */
    public CommandBuilder getBuilder() {
        return new CommandBuilder(rootNode);
    }

}
