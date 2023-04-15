package com.riicarus.comandante.manage;

import com.riicarus.comandante.exception.CommandSyntaxException;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;

/**
 * [FEATURE INFO]<br/>
 * lexical analyzer for command
 *
 * @author Riicarus
 * @create 2023-4-11 11:14
 * @since 1.0.0
 */
public class LexicalAnalyzer {

    /**
     * The StringBuilder used to build a token.
     */
    private final StringBuilder tokenBuilder = new StringBuilder();
    /**
     * A queue of CommandToken. Some analyze process may produce more than one token, so we use a queue to maintain them.
     */
    private final Queue<CommandToken> tokens = new LinkedList<>();
    /**
     * The command string input, stored as a byte array, used to get byte during analyze process.
     */
    private byte[] buffer;
    /**
     * The current pointer points to the current byte in the buffer.
     */
    private int idx = -1;
    /**
     * The current used byte.
     */
    private byte b = ' ';
    /**
     * The count of the argument quote.
     */
    private int quoteCount = 0;
    /**
     * The flag which represents if next escape character needs to escape.
     */
    private boolean needEscape = true;

    /**
     * Reset variables for next command input.
     */
    protected void reset() {
        this.idx = -1;
        this.b = ' ';
        this.quoteCount = 0;
        tokens.clear();
        resetForNextToken();
    }

    /**
     * Reset variables for next token in one command input.
     */
    protected void resetForNextToken() {
        tokenBuilder.delete(0, tokenBuilder.length());
        this.needEscape = true;
    }

    /**
     * Set next command input to analyzer.
     *
     * @param command command string input
     */
    public void input(String command) {
        reset();

        buffer = command.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Analyze next one token, used for GrammarAnalyzer to get a token each time.
     *
     * @return next CommandToken
     */
    public CommandToken analyzeOne() {
        // get if queue has more token
        if (!tokens.isEmpty()) {
            return tokens.poll();
        }

        CommandToken token;

        resetForNextToken();

        if (isEnd()) {
            return null;
        }

        // set pointer to next byte to analyze
        nextByte();
        ignoreBlankSpace();

        if (isLetter() || isDigit()) {
            // the main or argument item starts with ether a letter or a digit
            handleMainOrArgumentItemString();
        } else if (isOptPrefix()) {
            // the opt item must start with opt prefix
            handleOptionItemString();
        } else if (isArgumentQuote()) {
            // the argument item may follow with the argument quote
            handleArgumentItemString();
        } else if (isPipeline()) {
            handleCommandPipeline();
        } else if (isCommandLinker()) {
            handleCommandLinker();
        } else {
            throw new CommandSyntaxException("You have a syntax error near character " + (char) b + ", index: " + idx);
        }

        return tokens.poll();
    }

    /**
     * Handle main item or argument item which is not quoted by the argument quote.<br/>
     */
    protected void handleMainOrArgumentItemString() {
        String itemString = getSimpleItemString();
        tokens.add(new CommandToken(CommandTokenType.MAIN_OR_ARGUMENT, itemString));
    }

    protected void handleOptionItemString() {
        CommandToken token = new CommandToken(CommandTokenType.PREFIX_IDENTIFIER, FixedLexicalItemValue.OPT_PREFIX.getValue());
        tokens.add(token);

        boolean isAlias = false;

        nextByte();
        if (isOptPrefix()) {
            tokens.add(token);
        } else {
            retract();
            isAlias = true;
        }

        nextByte();
        String itemString = getSimpleItemString();

        if (isAlias) {
            byte[] aliases = itemString.getBytes(StandardCharsets.UTF_8);
            for (byte alias : aliases) {
                tokens.add(new CommandToken(CommandTokenType.OPT, String.valueOf((char) alias)));
            }
        } else {
            tokens.add(new CommandToken(CommandTokenType.OPT, itemString));
        }
    }

    /**
     * Handle the argument item quoted by the argument quote.
     */
    protected void handleArgumentItemString() {
        tokens.add(new CommandToken(CommandTokenType.PREFIX_IDENTIFIER, FixedLexicalItemValue.ARGUMENT_QUOTE.getValue()));
        quoteCount++;

        nextByte();

        // If it's the right argument quote.
        if (quoteCount % 2 != 1) {
            if (isBlankSpace()) {
                retract();
            } else {
                throw new CommandSyntaxException("You have a syntax error near character " + (char) b + ", index: " + idx);
            }
            return;
        }


        String itemString = getArgumentItemString();
        tokens.add(new CommandToken(CommandTokenType.ARGUMENT, itemString));
    }

    protected void handleCommandPipeline() {
        tokens.add(new CommandToken(CommandTokenType.PREFIX_IDENTIFIER, FixedLexicalItemValue.PIPELINE_IDENTIFIER.getValue()));
    }

    protected void handleCommandLinker() {
        tokens.add(new CommandToken(CommandTokenType.PREFIX_IDENTIFIER, FixedLexicalItemValue.COMMAND_LINKER.getValue()));
    }

    /**
     * Set idx points to the nearest next no-blank byte.
     */
    protected void ignoreBlankSpace() {
        while (isBlankSpace()) {
            b = buffer[++idx];
        }
    }

    /**
     * Iterate idx to next byte.
     */
    protected void nextByte() {
        b = buffer[++idx];
    }

    /**
     * The simple item means main item, opt item and not-quoted-argument item. <br/>
     * The will get string till meeting a blank space or the command is end.
     *
     * @return item string
     */
    protected String getSimpleItemString() {
        while (!isBlankSpace() && !isEnd()) {
            tokenBuilder.append((char) b);

            nextByte();
        }

        // process the last byte
        if (isEnd() && (!isBlankSpace())) {
            tokenBuilder.append((char) b);
        } else {
            retract();
        }

        return tokenBuilder.toString();
    }

    /**
     * Get quoted argument item string. <br/>
     * This will get the string till meeting an argument quote or the command is end. <br/>
     * We add escape letter process to expand the content character type in quoted arguments.
     *
     * @return item string
     */
    protected String getArgumentItemString() {
        while ((!isArgumentQuote() || isArgumentQuote() && isPreEscapeIdentifier()) && !isEnd()) {
            if (!isEscapeIdentifier()) {
                tokenBuilder.append((char) b);
                needEscape = true;
            } else if (isEscapeIdentifier() && isPreEscapeIdentifier()) {
                // If followed with another escape char, the follower has no escape function and should add to item string.
                // That is the followed escape char is escaped by the former.
                needEscape = false;
                tokenBuilder.append((char)b);
            }

            nextByte();
        }

        if (isEnd() && (!isArgumentQuote() || isArgumentQuote() && isPreEscapeIdentifier())) {
            tokenBuilder.append((char) b);
        } else {
            retract();
        }

        return tokenBuilder.toString();
    }

    protected boolean isLetter() {
        return b >= 'a' && b <= 'z' || b >= 'A' && b <= 'Z';
    }

    protected boolean isDigit() {
        return b >= '0' && b <= '9';
    }

    protected boolean isArgumentQuote() {
        return b == '\'';
    }

    protected boolean isOptPrefix() {
        return b == '-';
    }

    protected boolean isPipeline() {
        return b == '|';
    }

    protected boolean isCommandLinker() {
        return b == '&';
    }

    protected boolean isEscapeIdentifier() {
        return b == '\\';
    }

    protected boolean isPreEscapeIdentifier() {
        return buffer[idx - 1] == '\\' && needEscape;
    }

    protected boolean isBlankSpace() {
        return b == ' ';
    }

    protected void retract() {
        b = buffer[--idx];
    }

    protected boolean isEnd() {
        return idx == buffer.length - 1;
    }
}
