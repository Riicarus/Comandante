package com.skyline.command.tree;

import java.util.concurrent.ConcurrentHashMap;

/**
 * [FEATURE INFO]<br/>
 * 指令根节点
 *
 * @author Skyline
 * @create 2022-10-14 23:48
 * @since 1.0.0
 */
public class RootCommandNode extends CommandNode {

    private static final String ROOT_NAME = "root";

    public RootCommandNode() {
        super(ROOT_NAME, new ConcurrentHashMap<>(), null,
                null, null,
                null, null);
    }

    @Override
    public ConcurrentHashMap<String, ActionCommandNode> getSubActions() {
        return null;
    }
}
