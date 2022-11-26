package com.riicarus.comandante.tree;

import com.riicarus.comandante.argument.CommandArgumentType;
import com.riicarus.comandante.exception.CommandSyntaxException;

import java.util.HashMap;

/**
 * [FEATURE INFO]<br/>
 * 指令树 Argument 节点
 *
 * @author Riicarus
 * @create 2022-11-16 16:53
 * @since 1.0.0
 */
public class ArgumentNode<T> extends AbstractNode {

    /**
     * 参数类型
     */
    private final CommandArgumentType<T> type;
    /**
     * 是否为 OptionNode 之后的参数节点
     */
    private final boolean optionArg;

    public static final String OPTION_ARGUMENT_NAME = "OPT_ARG";
    public static final String EXECUTION_ARGUMENT_NAME = "EXE_ARG";

    public ArgumentNode(final String name, final CommandArgumentType<T> type, final AbstractNode previousNode) {
        super(name, new HashMap<>(), null, new HashMap<>(), previousNode);
        this.type = type;
        if (previousNode instanceof OptionNode) {
            // 如果前驱节点是 OptionNode, 那就是 OptionNode 之后的参数节点
            this.optionArg = true;
        } else if (previousNode instanceof ExecutionNode) {
            // 如果前驱节点是 ExecutionNode, 那就不是 OptionNode 之后的参数节点
            this.optionArg = false;
        } else if (previousNode instanceof ArgumentNode) {
            // 如果前驱节点是 ArgumentNode, 那就根据前驱节点的 optionArg 进行判断
            this.optionArg = ((ArgumentNode<?>) previousNode).optionArg;
        } else {
            // 不应该存在前驱节点是 RootNode 的情况
            this.optionArg = false;
        }
    }

    public CommandArgumentType<T> getType() {
        return type;
    }

    /**
     * 将指令字符串中的参数转换为当前节点定义的参数类型的数据
     *
     * @param arg 指令字符串中的参数
     * @return 当前节点定义的参数类型的数据
     * @throws CommandSyntaxException 指令语法错误异常
     */
    public T parse(String arg) throws CommandSyntaxException {
        return getType().parse(arg);
    }

    public boolean isOptionArg() {
        return optionArg;
    }

    @Override
    public HashMap<String, ExecutionNode> getExecutions() {
        return isOptionArg() ? null : super.getExecutions();
    }
}
