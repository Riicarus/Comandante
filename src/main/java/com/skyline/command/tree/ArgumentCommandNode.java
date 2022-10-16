package com.skyline.command.tree;

import com.skyline.command.argument.CommandArgumentType;
import com.skyline.command.executor.CommandExecutor;

import java.util.HashMap;
import java.util.Objects;

/**
 * [FEATURE INFO]<br/>
 * 参数指令节点
 *
 * @author Skyline
 * @create 2022-10-14 23:49
 * @since 1.0.0
 */
public class ArgumentCommandNode<T> extends CommandNode {

    private final CommandArgumentType<T> type;

    public ArgumentCommandNode(final String name,
                               final CommandExecutor commandExecutor,
                               final CommandArgumentType<T> type) {
        super(name, null, null,
                null, new HashMap<>(),
                new HashMap<>(), commandExecutor);

        this.type = type;
    }

    @Override
    public HashMap<String, ActionCommandNode> getSubActions() {
        return null;
    }

    public CommandArgumentType<T> getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArgumentCommandNode<?> that = (ArgumentCommandNode<?>) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}