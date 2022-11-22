package com.riicarus.comandante.manage;

import com.riicarus.comandante.exception.CommandExecutionException;
import com.riicarus.comandante.executor.CommandHelper;
import com.riicarus.comandante.tree.AbstractNode;
import com.riicarus.comandante.tree.ArgumentNode;
import com.riicarus.comandante.tree.ExecutionNode;
import com.riicarus.comandante.tree.OptionNode;
import com.riicarus.util.Asserts;
import com.riicarus.util.exception.AssertsFailException;
import com.riicarus.util.exception.NullObjectException;

import java.util.*;

/**
 * [FEATURE INFO]<br/>
 * 指令建议<br/>
 * 当指令在解析过程中找不到节点时, 使用指令建议对使用者进行提示<br/>
 * 用在 CommandNotFoundException 中进行处理
 *
 * @author Skyline
 * @create 2022-11-20 13:58
 * @since 1.0.0
 */
public class CommandSuggester {

    /**
     * 指令上下文, 用于获取当前解析到的节点和已解析完成的指令串
     */
    private final CommandContext context;
    /**
     * 没有被找到的指令部分字符串
     */
    private final String notFoundStr;

    public CommandSuggester(CommandContext context, String notFoundStr) {
        this.context = context;
        this.notFoundStr = notFoundStr;
    }

    public String suggest() {
        try {
            Asserts.notEmpty(notFoundStr, "");
        } catch (AssertsFailException e) {
            return "";
        }

        AbstractNode currentNode = context.getCurrentNode();
        Set<AbstractNode> children = new HashSet<>();
        HashMap<String, ExecutionNode> executions = currentNode.getExecutions();
        HashMap<String, OptionNode> options = currentNode.getOptions();
        HashMap<String, ArgumentNode<?>> arguments = currentNode.getArguments();

        if (executions != null && !executions.isEmpty()) {
            children.addAll(executions.values());
        }

        if (options != null && !options.isEmpty()) {
            children.addAll(options.values());
        }

        if (arguments != null && !arguments.isEmpty()) {
            children.addAll(arguments.values());
        }

        List<String> suggestionList = new ArrayList<>();
        for (AbstractNode childNode : children) {
            String name = childNode.getName();
            if (name.startsWith(notFoundStr)) {
                String suggestion = formatSuggestionOfNode(childNode);
                suggestionList.add(suggestion);
            }
        }

        if (suggestionList.isEmpty()) {
            for (AbstractNode childNode : children) {
                String suggestion = formatSuggestionOfNode(childNode);
                suggestionList.add(suggestion);
            }
        }

        return suggestionList.isEmpty() ? "" : "\nYou may want to use one of these (sub)command: " + suggestionList + ".";
    }

    private String formatSuggestionOfNode(AbstractNode node) throws NullObjectException, CommandExecutionException {
        Asserts.notNull(node, "Node can not be null.");

        String name = node.getName();
        if (node instanceof ExecutionNode) {
            return name;
        } else if (node instanceof OptionNode) {
            return CommandDispatcher.LONG_OPTION_PREFIX_STRING
                    + name
                    + CommandHelper.LONG_SHORT_OPTION_SEPARATOR
                    + CommandDispatcher.SHORT_OPTION_PREFIX_STRING
                    + ((OptionNode) node).getAlias();
        } else if (node instanceof ArgumentNode) {
            return name
                   + CommandHelper.ARG_TYPE_QUOTE_LEFT
                   + ((ArgumentNode<?>) node).getType().getTypeName()
                   + CommandHelper.ARG_TYPE_QUOTE_RIGHT;
        }

        throw new CommandExecutionException("Get suggestion of node[" + name + "] failed.");
    }
}
