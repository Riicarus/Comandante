package com.riicarus.comandante.manage;

/**
 * [FEATURE INFO]<br/>
 * type of a command token
 *
 * @author Riicarus
 * @create 2023-4-12 11:37
 * @since 1.0.0
 */
public enum CommandTokenType {

    MAIN(1),
    OPT(2),
    PREFIX_IDENTIFIER(3),
    ARGUMENT(4),
    MAIN_OR_ARGUMENT(5);

    private final int value;

    CommandTokenType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
