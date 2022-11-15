package com.riicarus.comandante.tree;

import com.riicarus.comandante.argument.CommandArgumentType;
import com.riicarus.comandante.executor.CommandExecutor;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [FEATURE INFO]<br/>
 * 参数指令节点<br/>
 * 注意: 参数指令节点的名称要和 其父 OptionCommandNode 的名称相同
 *
 * @author Skyline
 * @create 2022-10-14 23:49
 * @since 1.0
 */
public class ArgumentCommandNode<T> extends CommandNode {

    /**
     * 参数类型
     */
    private final CommandArgumentType<T> type;

    public ArgumentCommandNode(final String name,
                               final CommandExecutor commandExecutor,
                               final CommandArgumentType<T> type) {
        super(name, null, null,
                null, new ConcurrentHashMap<>(),
                null, commandExecutor);

        this.type = type;
    }

    @Override
    public ConcurrentHashMap<String, ActionCommandNode> getSubActions() {
        return null;
    }

    public CommandArgumentType<T> getType() {
        return type;
    }

    /**
     * 将指令字符串中的参数转换为当前节点定义的参数类型的数据
     *
     * @param arg 指令字符串中的参数
     * @return 当前节点定义的参数类型的数据
     */
    public T parse(String arg) {
        return getType().parse(arg);
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