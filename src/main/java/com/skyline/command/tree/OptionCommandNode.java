package com.skyline.command.tree;

import com.skyline.command.executor.CommandExecutor;

import java.util.HashMap;
import java.util.Objects;

/**
 * [FEATURE INFO]<br/>
 * 指令 option 节点
 *
 * @author Skyline
 * @create 2022-10-15 16:43
 * @since 1.0.0
 */
public class OptionCommandNode extends CommandNode {

    private final String alias;

    public OptionCommandNode(final CommandExecutor commandExecutor, final String name, final String alias) {
        super(name, null, null,
                null, new HashMap<>(),
                new HashMap<>(), commandExecutor);

        this.alias = alias;
    }

    @Override
    public HashMap<String, ActionCommandNode> getSubActions() {
        return null;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OptionCommandNode that = (OptionCommandNode) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
