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

    public ExecutionNode(String name) {
        super(name, new HashMap<>(), new HashMap<>());
    }

}
