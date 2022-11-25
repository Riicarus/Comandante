package com.riicarus.comandante.manage;

import com.riicarus.comandante.executor.CommandExecutor;
import com.riicarus.comandante.executor.CommandHelper;
import com.riicarus.comandante.tree.*;

import java.util.*;

/**
 * [FEATURE INFO]<br/>
 * 指令注册器, 维护指令树根节点, 提供指令构建器
 *
 * @author Skyline
 * @create 2022-10-15 10:46
 * @since 1.0
 */
public class CommandRegister {

    /**
     * 指令树根节点
     */
    private final RootNode rootNode = new RootNode();

    public RootNode getRootNode() {
        return rootNode;
    }

    /**
     * 获取指令构建器
     *
     * @return 指令构建器
     */
    public CommandBuilder getBuilder() {
        return new CommandBuilder(rootNode);
    }

    /**
     * 列出指令使用情况
     *
     * @return 指令使用情况列表
     */
    public HashMap<String, Integer> listCommandUsage() {
        HashMap<String, Integer> commandUsage = new HashMap<>();

        ArrayList<CommandExecutor> executors = rootNode.getExecutors();
        for (CommandExecutor executor : executors) {
            StringBuilder commandStr = new StringBuilder();
            AbstractNode node = executor.getNode();
            while (node != rootNode) {
                String name = node.getName();
                if (node instanceof ExecutionNode) {
                    commandStr.insert(0, name + CommandDispatcher.COMMAND_PART_SEPARATOR);
                } else if (node instanceof OptionNode) {
                    commandStr.insert(0, CommandDispatcher.LONG_OPTION_PREFIX_STRING + name + CommandHelper.LONG_SHORT_OPTION_SEPARATOR + ((OptionNode) node).getAlias() + CommandDispatcher.COMMAND_PART_SEPARATOR);
                } else if (node instanceof ArgumentNode) {
                    commandStr.insert(0, name + CommandHelper.ARG_TYPE_QUOTE_LEFT + ((ArgumentNode<?>) node).getType().getTypeName() + CommandHelper.ARG_TYPE_QUOTE_RIGHT + CommandDispatcher.COMMAND_PART_SEPARATOR);
                }

                node = node.getPreviousNode();
            }

            commandUsage.put(commandStr.toString(), executor.getUseCount());
        }

        return commandUsage;
    }

    /**
     * 使用情况按从低到高排序, 取前 limit 个元素
     *
     * @param commandUsage 乱序的指令使用情况
     * @param limit 前多少个元素
     * @return 排序后的集合
     */
    public LinkedHashMap<String, Integer> listCommandUsageAsc(HashMap<String, Integer> commandUsage, int limit) {
        LinkedHashMap<String, Integer> commandUsageDesc = new LinkedHashMap<>();

        ArrayList<Map.Entry<String, Integer>> entries = new ArrayList<>(commandUsage.entrySet());
        entries.sort(Comparator.comparingInt(Map.Entry::getValue));

        if (limit > 0) {
            entries.subList(0, limit).forEach(entry -> commandUsageDesc.put(entry.getKey(), entry.getValue()));
        } else {
            entries.forEach(entry -> commandUsageDesc.put(entry.getKey(), entry.getValue()));
        }

        return commandUsageDesc;
    }

    /**
     * 使用情况按从高到低排序, 取前 limit 个元素
     *
     * @param commandUsage 乱序的指令使用情况
     * @param limit 前多少个元素
     * @return 排序后的集合
     */
    public LinkedHashMap<String, Integer> listCommandUsageDesc(HashMap<String, Integer> commandUsage, int limit) {
        LinkedHashMap<String, Integer> commandUsageDesc = new LinkedHashMap<>();

        ArrayList<Map.Entry<String, Integer>> entries = new ArrayList<>(commandUsage.entrySet());
        entries.sort(Comparator.comparingInt(Map.Entry::getValue));
        Collections.reverse(entries);

        if (limit > 0) {
            entries.subList(0, limit).forEach(entry -> commandUsageDesc.put(entry.getKey(), entry.getValue()));
        } else {
            entries.forEach(entry -> commandUsageDesc.put(entry.getKey(), entry.getValue()));
        }

        return commandUsageDesc;
    }
}
