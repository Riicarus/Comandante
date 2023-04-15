package com.riicarus.comandante.manage;

/**
 * [FEATURE INFO]<br/>
 * Some fixed value for command item.
 *
 * @author Riicarus
 * @create 2023-4-11 11:23
 * @since 2.0
 */
public enum FixedLexicalItemValue {

    ARGUMENT_QUOTE("'"),
    OPT_PREFIX("-"),
    ESCAPE_IDENTIFIER("\\"),
    PIPELINE_IDENTIFIER("|"),
    COMMAND_LINKER("&"),
    ARGUMENT("#");

    private final String value;

    FixedLexicalItemValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
