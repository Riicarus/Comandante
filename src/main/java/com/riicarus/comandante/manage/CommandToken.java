package com.riicarus.comandante.manage;

/**
 * [FEATURE INFO]<br/>
 * command token
 *
 * @author Riicarus
 * @create 2023-4-11 20:54
 * @since 1.0.0
 */
public class CommandToken {

    private final CommandTokenType type;

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
