package com.riicarus.comandante.tree;

import com.riicarus.comandante.argument.CommandArgumentType;

import java.util.Objects;

/**
 * [FEATURE INFO]<br/>
 * 指令树 Argument 节点
 *
 * @author Skyline
 * @create 2022-11-16 16:53
 * @since 1.0.0
 */
public class ArgumentNode<T> extends AbstractNode {

    /**
     * 参数类型
     */
    private final CommandArgumentType<T> type;

    public ArgumentNode(String name, CommandArgumentType<T> type) {
        super(name, null, null);
        this.type = type;
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
        ArgumentNode<?> that = (ArgumentNode<?>) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
