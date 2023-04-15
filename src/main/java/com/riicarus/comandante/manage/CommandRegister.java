package com.riicarus.comandante.manage;

import java.util.*;

/**
 * [FEATURE INFO]<br/>
 * Command Register, maintains the RootNode of Command Tree and provides the CommandBuilder
 *
 * @author Riicarus
 * @create 2022-10-15 10:46
 * @since 1.0
 */
public class CommandRegister {

    /**
     * CommandItemManager manages the registered and reserved command items.
     */
    private final CommandItemManager commandItemManager = new CommandItemManager();

    /**
     * 获取指令构建器
     *
     * @return 指令构建器
     */
    public CommandBuilder builder() {
        return new CommandBuilder(commandItemManager);
    }

    public CommandItemManager getCommandItemManager() {
        return commandItemManager;
    }

    /**
     * 列出指令使用情况
     *
     * @return 指令使用情况列表
     */
    public HashMap<String, Integer> listCommandUsage() {
        HashMap<String, Integer> commandUsage = new HashMap<>();

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
