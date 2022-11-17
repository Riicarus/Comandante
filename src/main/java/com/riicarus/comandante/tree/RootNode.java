package com.riicarus.comandante.tree;

import java.util.HashMap;
import java.util.Set;

/**
 * [FEATURE INFO]<br/>
 * 指令树根节点<br/>
 * 不包含任何可用执行器<br/>
 * 只保存 ExecutionNode 节点
 *
 * @author Skyline
 * @create 2022-11-16 16:55
 * @since 1.0.0
 */
public class RootNode extends AbstractNode {

    private static final String ROOT_NAME = "ROOT";

    public RootNode() {
        super(ROOT_NAME, new HashMap<>(), null, null, null);
    }

    public Set<String> listRegisteredExecutions() {
        return getExecutions().keySet();
    }
}
