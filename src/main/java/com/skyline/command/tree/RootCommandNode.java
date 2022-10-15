package com.skyline.command.tree;

import java.util.HashMap;

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
        super(ROOT_NAME, new HashMap<>(), null,
                null, null,
                null, null);
    }

    @Override
    public HashMap<String, ActionCommandNode> getSubActions() {
        return null;
    }
}
