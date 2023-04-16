package com.riicarus.comandante.manage;

/**
 * [FEATURE INFO]<br/>
 * Type of a command token, used in the lexical analyzer to defer different tokens.
 *
 * @author Riicarus
 * @create 2023-4-12 11:37
 * @since 3.0
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
