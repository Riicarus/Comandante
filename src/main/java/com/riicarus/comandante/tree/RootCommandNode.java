package com.riicarus.comandante.tree;

import java.util.concurrent.ConcurrentHashMap;

/**
 * [FEATURE INFO]<br/>
 * 指令根节点, 维持指令树所有节点的根引用, 只包含 ExecutionCommandNode 类型的子节点
 *
 * @author Skyline
 * @create 2022-10-14 23:48
 * @since 1.0
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
