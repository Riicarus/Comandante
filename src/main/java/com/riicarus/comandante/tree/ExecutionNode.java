package com.riicarus.comandante.tree;

import java.util.HashMap;

/**
 * [FEATURE INFO]<br/>
 * 指令树 Execution 节点<br/>
 * 包含以下集合: <br/>
 *   1. ExecutionNodes <br/>
 *   2. OptionNodes <br/>
 *   3. ArgumentNodes <br/>
 * 可以有自己的指令执行器 <br/>
 *
 * @author Skyline
 * @create 2022-11-16 16:42
 * @since 1.0.0
 */
public class ExecutionNode extends AbstractNode {

    /**
     * 是否为 MainExecutionNode, 即注册在 RootNode 下的节点
     */
    private final boolean main;
    /**
     * 如果是 MainExecutionNode, 需要保存包括其子 ExecutionNode 节点在内的所有 OptionNode 子节点集合<br/>
     * 用于唯一确定指令字符串中的 OptionNode 节点
     */
    private final HashMap<String, OptionNode> allOptions;

    public ExecutionNode(final String name, final AbstractNode previousNode) {
        super(name, new HashMap<>(), new HashMap<>(), new HashMap<>(), previousNode);
        this.main = previousNode instanceof RootNode;
        this.allOptions = main ? new HashMap<>() : null;
    }

    public OptionNode getAnyOption(String name) {
        if (main) {
            HashMap<String, OptionNode> allOptions = getAllOptions();
            if (allOptions == null || allOptions.isEmpty() || name == null) {
                return null;
            }

            OptionNode optionNode;
            if ((optionNode = allOptions.get(name)) == null) {
                for (OptionNode node : allOptions.values()) {
                    if (name.equals(node.getAlias())) {
                        optionNode = node;
                        break;
                    }
                }
            }

            return optionNode;
        }

        return null;
    }

    public boolean containsOption(String name, String alias) {
        if (getAllOptions().containsKey(name)) {
            return true;
        } else {
            for (OptionNode node : getAllOptions().values()) {
                if (alias != null && alias.equals(node.getAlias())) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isMain() {
        return main;
    }

    public void addAllOption(OptionNode optionNode) {
        getAllOptions().put(optionNode.getName(), optionNode);
    }

    public HashMap<String, OptionNode> getAllOptions() {
        return allOptions;
    }
}
