package com.skyline.comandante.tree;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [FEATURE INFO]<br/>
 * 指令 exe 节点
 *
 * @author Skyline
 * @create 2022-10-15 16:43
 * @since 1.0
 */
public class ExecutionCommandNode extends CommandNode {

    public ExecutionCommandNode(final String name) {
        super(name, null, new ConcurrentHashMap<>(),
                null, new ConcurrentHashMap<>(),
                null, null);
    }

    @Override
    public ConcurrentHashMap<String, ActionCommandNode> getSubActions() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExecutionCommandNode that = (ExecutionCommandNode) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
