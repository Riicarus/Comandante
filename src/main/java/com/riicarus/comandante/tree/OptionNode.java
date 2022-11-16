package com.riicarus.comandante.tree;

import java.util.Objects;

/**
 * [FEATURE INFO]<br/>
 * 指令树 Option 节点<br/>
 * OptionNode 节点不会构成链式结构, 任何其父节点只能注册一层 OptionNode 节点<br/>
 * OptionNode 节点会保存一个 ArgumentNode 组成的有序链表, 用于定义和解析参数类型<br/>
 *
 * @author Skyline
 * @create 2022-11-16 16:52
 * @since 1.0.0
 */
public class OptionNode extends AbstractNode {

    /**
     * option 指令别称, 用于短指令, 在前面加上 '-' 使用
     */
    private final String alias;

    public OptionNode(String name, String alias) {
        super(name, null, null);
        this.alias = alias;
    }

    /**
     * 是否包含参数
     *
     * @return boolean-是否包含参数
     */
    public boolean requireArg() {
        return getNextArgument() != null;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OptionNode that = (OptionNode) o;
        return Objects.equals(alias, that.alias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alias);
    }
}
