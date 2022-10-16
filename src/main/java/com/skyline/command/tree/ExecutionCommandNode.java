package com.skyline.command.tree;

import java.util.HashMap;
import java.util.Objects;

/**
 * [FEATURE INFO]<br/>
 * 指令 exe 节点
 *
 * @author Skyline
 * @create 2022-10-15 16:43
 * @since 1.0.0
 */
public class ExecutionCommandNode extends CommandNode {

    public ExecutionCommandNode(final String name) {
        super(name, null, new HashMap<>(),
                null, new HashMap<>(),
                null, null);
    }

    @Override
    public HashMap<String, ActionCommandNode> getSubActions() {
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
