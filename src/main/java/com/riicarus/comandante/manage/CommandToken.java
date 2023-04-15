package com.riicarus.comandante.manage;

/**
 * [FEATURE INFO]<br/>
 * Command token, the lexical analyzer parses one command input and generates tokens used in the grammar analyzer.
 *
 * @author Riicarus
 * @create 2023-4-11 20:54
 * @since 2.0
 */
public class CommandToken {
    /**
     * Type of a token.
     */
    private final CommandTokenType type;
    /**
     * Value of a token, for main and opt items, it's their name, while for arg items, it's their value.
     */
    private final String value;

    public CommandToken(CommandTokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public CommandTokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Token[" +
                type +
                ", \"" + value + "\"" +
                ']';
    }
}
