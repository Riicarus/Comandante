package com.riicarus.comandante.manage;

import com.riicarus.comandante.executor.CommandExecutor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * [FEATURE INFO]<br/>
 * Manager for lexical items of command, usually works in the command building process.
 *
 * @author Riicarus
 * @create 2023-4-11 11:45
 * @since 3.0
 */
public class CommandItemManager {
    /**
     * ItemCount increases when a new item is built and stored to lexicalItems.
     */
    private final AtomicInteger itemCount = new AtomicInteger(0);
    /**
     * Use a map to maintain each lexical item. The key is the item's name, the value is the item set with the same name.
     */
    private final HashMap<String, HashSet<CommandItem>> lexicalItems = new HashMap<>();
    /**
     * Use a map to bind opt items with their alias. The key is the item's alias, the value is the item set with the same alias.
     */
    private final HashMap<String, HashSet<CommandItem>> optItemsForAlias = new HashMap<>();
    /**
     * Use a map to maintain the binding of CommandItem and its executor.
     */
    private final HashMap<CommandItem, CommandExecutor> executors = new HashMap<>();

    /**
     * Add a lexical item to lexicalItems set.
     *
     * @param item     current item
     * @param prevItem previous item
     */
    public void addLexicalItem(CommandItem item, CommandItem prevItem) {
        if (!containsItem(item.getName(), prevItem)) {
            doAddLexicalItem(item);
        }
    }

    private void doAddLexicalItem(CommandItem item) {
        // if the set with the name is initialized
        if (this.lexicalItems.containsKey(item.getName())) {
            this.lexicalItems.get(item.getName()).add(item);
        } else {
            HashSet<CommandItem> items = new HashSet<>();
            items.add(item);
            this.lexicalItems.put(item.getName(), items);
        }

        // if the command item is an opt item, update optItemsForAlias
        if (CommandItemType.RESERVED_WORD.getValue() == item.getType() && item.getSubName() != null && !item.getSubName().equals("")) {
            if (this.optItemsForAlias.containsKey(item.getSubName())) {
                this.optItemsForAlias.get(item.getSubName()).add(item);
            } else {
                HashSet<CommandItem> items = new HashSet<>();
                items.add(item);
                this.optItemsForAlias.put(item.getSubName(), items);
            }
        }
    }

    /**
     * Judge if a item is in lexicalItems set.<br/>
     * Note that this function only compares the current item's name and its prev item's serialId, but not compares the item's type,
     * So we must asume that there's no more than one item with the same name following the prev item.<br/>
     * So, even for different type of items following the same prev item, they can not have the same name.
     *
     * @param name     item's name
     * @param prevItem previous CommandItem
     * @return is in
     */
    @SuppressWarnings("all")
    public boolean containsItem(String name, CommandItem prevItem) {
        if (!this.lexicalItems.containsKey(name)) {
            return false;
        }

        HashSet<CommandItem> items = this.lexicalItems.get(name);
        for (CommandItem item : items) {
            if (item.getPrevSerialId() == prevItem.getSerialId()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Judge if a item is in optItemsForAlias set.
     *
     * @param alias    item's alias
     * @param prevItem previous CommandItem
     * @return is in
     */
    public boolean containsItemAlias(String alias, CommandItem prevItem) {
        if (!this.optItemsForAlias.containsKey(alias)) {
            return false;
        }

        HashSet<CommandItem> items = this.optItemsForAlias.get(alias);
        for (CommandItem item : items) {
            if (item.getPrevSerialId() == prevItem.getSerialId()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get a item in lexicalItems set.
     *
     * @param name     item's name
     * @param prevItem previous CommandItem
     * @return CommandItem
     */
    public CommandItem getItem(String name, CommandItem prevItem) {
        if (!this.lexicalItems.containsKey(name)) {
            return null;
        }

        HashSet<CommandItem> items = this.lexicalItems.get(name);

        for (CommandItem item : items) {
            if (item.getPrevSerialId() == prevItem.getSerialId()) {
                return item;
            }
        }

        return null;
    }

    /**
     * Get a item in optItemsForAlias set.
     *
     * @param alias    item's alias
     * @param prevItem previous CommandItem
     * @return CommandItem
     */
    public CommandItem getItemAlias(String alias, CommandItem prevItem) {
        if (!this.optItemsForAlias.containsKey(alias)) {
            return null;
        }

        HashSet<CommandItem> items = this.optItemsForAlias.get(alias);

        for (CommandItem item : items) {
            if (item.getPrevSerialId() == prevItem.getSerialId()) {
                return item;
            }
        }

        return null;
    }

    /**
     * Bind CommandExecutor to a CommandItem.
     *
     * @param item     current item
     * @param executor the current item's executor
     */
    public void bindExecutor(CommandItem item, CommandExecutor executor) {
        if (!executors.containsKey(item)) {
            executors.put(item, executor);
        }
    }

    /**
     * Find bound CommandExecutor of a command item.
     *
     * @param item Current CommandItem
     * @return CommandExecutor
     */
    public CommandExecutor findExecutor(CommandItem item) {
        return executors.get(item);
    }

    /**
     * Generate serial id for next item.
     *
     * @return serial id
     */
    public int generateSerialId() {
        return itemCount.incrementAndGet();
    }

    /**
     * List all registered commands' usage.
     *
     * @return registered command usage list
     */
    public String listAllCommandUsage() {
        StringBuilder usageBuilder = new StringBuilder();
        for (CommandExecutor executor : executors.values()) {
            usageBuilder.append(executor.getCommandString())
                    .append("\t\t")
                    .append(executor.getUsage())
                    .append("[")
                    .append(executor.getUseCount())
                    .append("]")
                    .append("\n");
        }

        return usageBuilder.toString();
    }
}
