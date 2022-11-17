package com.riicarus.comandante.tree;

import java.util.HashMap;

/**
 * [FEATURE INFO]<br/>
 * 指令树 Option 节点<br/>
 * OptionNode 不会构成链式结构, 任何其父节点只能注册一层 OptionNode 节点<br/>
 * OptionNode 的参数固定, 不支持可选参数<br/>
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

    public OptionNode(final String name, final String alias, final AbstractNode previousNode) {
        super(name, null, null, new HashMap<>(), previousNode);
        this.alias = alias;
    }

    /**
     * 是否包含参数
     *
     * @return boolean-是否包含参数
     */
    public boolean requireArg() {
        return !getArguments().isEmpty();
    }

    public String getAlias() {
        return alias;
    }
}
