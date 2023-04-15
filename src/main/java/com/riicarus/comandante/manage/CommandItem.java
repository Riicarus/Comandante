package com.riicarus.comandante.manage;

import java.util.Objects;

/**
 * [FEATURE INFO]<br/>
 * Lexical item is the definition of command tree items.<br/>
 * It's constructed in command building process and stored as a identifier of different command parts.
 *
 * @author Riicarus
 * @create 2023-4-11 11:21
 * @since 1.0.0
 */
public class CommandItem {

    /**
     * The ROOT's item definition.
     */
    public static final CommandItem ROOT = new CommandItem(CommandItemType.ARGUMENT, 0, 0, "ROOT", null);

    /**
     * markword contains three parts: <br/>
     * [0:13]: current item's serial id <br/>
     * [14:27]: previous item's serial id which is used to defer items with the same type and name but in different command context <br/>
     * [28:31]: command item type <br/>
     */
    private final int markword;

    /**
     * Value is the detail info of the item <br/>
     * It's used to defer items with the same type. <br/>
     * Especially, for argument item, name simply means FixCommandItemValue.ARGUMENT--#.
     */
    private final String name;

    /**
     * For opt item, subName is its alias. While for argument item, subName is its name.
     */
    private final String subName;

    public CommandItem(CommandItemType type, int prevSerialId, int serialId, String name, String subName) {
        this.markword = constructMarkword(type.getValue(), prevSerialId, serialId);
        this.name = name;
        this.subName = subName;
    }

    /**
     * Construct a whole markword of current item
     *
     * @param type         item type
     * @param prevSerialId previous item's serial id
     * @param serialId     current item's serial id, which is the current itemCount maintained by CommandItemManager
     * @return current item's markword
     */
    private int constructMarkword(int type, int prevSerialId, int serialId) {
        return type << 28 | prevSerialId << 14 | serialId;
    }

    public int getMarkword() {
        return markword;
    }

    public int getType() {
        return markword >>> 28;
    }

    public int getPrevSerialId() {
        return (markword & 0xfffc000) >>> 14;
    }

    public int getSerialId() {
        return markword & 0x3fff;
    }

    public String getName() {
        return name;
    }

    public String getSubName() {
        return subName;
    }

    /**
     * Compare two items' markword to judge if they are the same
     *
     * @param o object
     * @return is equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandItem token = (CommandItem) o;
        return markword == token.markword;
    }

    @Override
    public int hashCode() {
        return Objects.hash(markword);
    }
}
