package com.riicarus.comandante.manage;

/**
 * [FEATURE INFO]<br/>
 * Command item type, stored in the highest 4 bits of a item's markword.
 *
 * @author Riicarus
 * @create 2023-4-11 11:22
 * @since 3.0
 */
public enum CommandItemType {

    /**
     * The main item or opt item.
     */
    RESERVED_WORD(1),
    /**
     * The prefix of some parts, like '-', ''', '\'.
     */
    PREFIX_IDENTIFIER(2),
    /**
     * Argument.
     */
    ARGUMENT(3);

    private final int value;

    CommandItemType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
