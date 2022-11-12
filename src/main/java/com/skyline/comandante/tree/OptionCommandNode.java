package com.skyline.comandante.tree;

import com.skyline.comandante.executor.CommandExecutor;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [FEATURE INFO]<br/>
 * 指令 option 节点
 *
 * @author Skyline
 * @create 2022-10-15 16:43
 * @since 1.0
 */
public class OptionCommandNode extends CommandNode {

    /**
     * option 类型指令别称, 用于短指令, 在前面加上 '-' 使用
     */
    private final String alias;

    public OptionCommandNode(final CommandExecutor commandExecutor, final String name, final String alias) {
        super(name, null, null,
                null, new ConcurrentHashMap<>(),
                new ConcurrentHashMap<>(), commandExecutor);

        this.alias = alias;
    }

    @Override
    public ConcurrentHashMap<String, ActionCommandNode> getSubActions() {
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