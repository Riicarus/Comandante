package com.riicarus.comandante.manage;

import com.riicarus.comandante.exception.CommandNotFoundException;
import com.riicarus.comandante.exception.CommandSyntaxException;
import com.riicarus.comandante.executor.CommandExecutor;

import java.util.LinkedList;
import java.util.List;

/**
 * [FEATURE INFO]<br/>
 * Grammar analyzer
 *
 * @author Riicarus
 * @create 2023-4-13 13:48
 * @since 1.0.0
 */
public class GrammarAnalyzer {

    private final LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();

    private final CommandItemManager itemManager;

    private final List<String> arguments = new LinkedList<>();
    private final List<CommandExecutor> executors = new LinkedList<>();

    private CommandToken token;
    private int tokenCount = 0;
    private CommandItem prevItem = CommandItem.ROOT;
    private CommandItem prevMainItem = CommandItem.ROOT;

    private boolean end = false;
    private PrevExecutableType prevExecutableType;

    public GrammarAnalyzer(CommandItemManager itemManager) {
        this.itemManager = itemManager;
    }

    public void analyze(String commandStr) {
        lexicalAnalyzer.input(commandStr);
        next();
        S();

        System.out.println(prevItem);
        System.out.println(arguments);
        System.out.println(executors);
    }

    protected void next() {
        token = lexicalAnalyzer.analyzeOne();
        if (token == null) {
            end = true;
        } else {
            tokenCount++;
            System.out.println(token);
        }
    }

    protected void updatePrevItem(CommandItem item) {
        if (item == null) {
            throw new CommandNotFoundException("Command not found, near: " + token.toString() + ", token idx: " + tokenCount + ". \n" +
                    "Please check your command definition.");
        }

        prevItem = item;
    }

    protected void updatePrevMainItem(CommandItem item) {
        if (item == null) {
            throw new CommandNotFoundException("Command not found, near: " + token.toString() + ", token idx: " + tokenCount + ". \n" +
                    "Please check your command definition.");
        }

        prevMainItem = item;
    }

    protected void S() {
        try {
            C();
        } catch (CommandNotFoundException e) {
            throw e;
        } catch (CommandSyntaxException e) {
            if (!end) {
                T();
                C();
            } else {
                throw e;
            }
        }

        // M -> End, O -> End, A -> End
        CommandExecutor executor = itemManager.findExecutor(prevItem);
        if (executor != null) {
            executors.add(executor);
        }
    }

    protected void C() {
        M();

        boolean endM = false;
        while (!endM && !end) {
            try {
                M();
            } catch (CommandNotFoundException e) {
                throw e;
            } catch (CommandSyntaxException e) {
                endM = true;
            }
        }

        while (!end) {
            Y();
        }
    }

    protected void M() {
        if (CommandTokenType.MAIN.equals(token.getType())) {
            CommandItem item = itemManager.getItem(token.getValue(), prevMainItem);
            updatePrevItem(item);
            updatePrevMainItem(item);
            prevExecutableType = PrevExecutableType.MAIN;
            next();
        } else if (CommandTokenType.MAIN_OR_ARGUMENT.equals(token.getType())) {
            CommandItem item = itemManager.getItem(token.getValue(), prevMainItem);
            if (item != null) {
                // Here is main item.
                updatePrevItem(item);
                updatePrevMainItem(item);
                next();
            } else if (!CommandItem.ROOT.equals(prevItem)) {
                A1();
            } else {
                throw new CommandSyntaxException("Command type not feat, near: " + token.toString() + ", token idx: " + tokenCount + ". \n" +
                        "Want: MAIN or ARGUMENT, get: " + token.getType() + ". \n " +
                        "Please check your command input, and the pipeline(|) or command linker(&) can only be followed by MAIN.");
            }
        } else {
            throw new CommandSyntaxException("Command type not feat, near: " + token.toString() + ", token idx: " + tokenCount + ". \n" +
                    "Want: MAIN or ARGUMENT, get: " + token.getType() + ". \n " +
                    "Please check your command input.");
        }
    }

    protected void Y() {
        try {
            O();
        } catch (CommandSyntaxException e) {
            A();
        }
    }

    protected void T() {
        if (CommandTokenType.PREFIX_IDENTIFIER.equals(token.getType())) {
            CommandItem prevCommandItem = this.prevItem;
            if (FixedLexicalItemValue.PIPELINE_IDENTIFIER.getValue().equals(token.getValue())) {
                this.prevItem = CommandItem.ROOT;
                this.prevMainItem = CommandItem.ROOT;
                next();
            } else if (FixedLexicalItemValue.COMMAND_LINKER.getValue().equals(token.getValue())) {
                this.prevItem = CommandItem.ROOT;
                this.prevMainItem = CommandItem.ROOT;
                next();
            } else {
                throw new CommandSyntaxException("Command type not feat, near: " + token.toString() + ", token idx: " + tokenCount + ". \n" +
                        "Want: PIPELINE(|) or COMMAND_LINKER(&), get: " + token.getType() + ". \n " +
                        "Please check your command input.");
            }

            // M -> T, O -> T, A -> T
            CommandExecutor executor = itemManager.findExecutor(prevCommandItem);
            if (executor != null) {
                executors.add(executor);
            }
        }
    }

    protected void A() {
        if (CommandTokenType.PREFIX_IDENTIFIER.equals(token.getType()) && FixedLexicalItemValue.ARGUMENT_QUOTE.getValue().equals(token.getValue())) {
            next();
            A1();
            if (CommandTokenType.PREFIX_IDENTIFIER.equals(token.getType()) && FixedLexicalItemValue.ARGUMENT_QUOTE.getValue().equals(token.getValue())) {
                next();
            } else {
                throw new CommandSyntaxException("Command type not feat, near: " + token.toString() + ", token idx: " + tokenCount + ". \n" +
                        "Want: ARGUMENT_QUOTE, get: " + token.getType() + ". \n " +
                        "Please check your command input.");
            }
        } else if (CommandTokenType.MAIN_OR_ARGUMENT.equals(token.getType()) && !CommandItem.ROOT.equals(prevItem)) {
            A1();
        } else {
            throw new CommandSyntaxException("Command type not feat, near: " + token.toString() + ", token idx: " + tokenCount + ". \n" +
                    "Want: ARGUMENT_QUOTE, get: " + token.getType() + ". \n " +
                    "Please check your command input.");
        }
    }

    protected void A1() {
        if (CommandTokenType.ARGUMENT.equals(token.getType()) || CommandTokenType.MAIN_OR_ARGUMENT.equals(token.getType())) {
            updatePrevItem(itemManager.getItem(FixedLexicalItemValue.ARGUMENT.getValue(), prevItem));
            arguments.add(token.getValue());
            prevExecutableType = PrevExecutableType.ARG;
            next();
        } else {
            throw new CommandSyntaxException("Command type not feat, near: " + token.toString() + ", token idx: " + tokenCount + ". \n" +
                    "Want: ARGUMENT, get: " + token.getType() + ". \n " +
                    "Please check your command input.");
        }
    }

    protected void O() {
        if (CommandTokenType.PREFIX_IDENTIFIER.equals(token.getType()) && FixedLexicalItemValue.OPT_PREFIX.getValue().equals(token.getValue())) {
            next();
            if (CommandTokenType.PREFIX_IDENTIFIER.equals(token.getType()) && FixedLexicalItemValue.OPT_PREFIX.getValue().equals(token.getValue())) {
                next();
                O2(false);
            } else {
                O1();
            }
        } else {
            throw new CommandSyntaxException("Command type not feat, near: " + token.toString() + ", token idx: " + tokenCount + ". \n" +
                    "Want: OPT_PREFIX, get: " + token.getType() + ". \n " +
                    "Please check your command input.");
        }
    }

    protected void O1() {
        O2(true);

        boolean isEnd = false;
        while (!isEnd && !end) {
            try {
                O2(true);
            } catch (CommandNotFoundException e) {
                throw e;
            } catch (CommandSyntaxException e) {
                isEnd = true;
            }
        }
    }

    protected void O2(boolean isAlias) {
        if (PrevExecutableType.OPT.equals(prevExecutableType) || PrevExecutableType.ARG.equals(prevExecutableType)) {
            // O -> O, A -> O
            CommandExecutor executor = itemManager.findExecutor(prevItem);
            if (executor != null) {
                executors.add(executor);
            }
        }

        if (CommandTokenType.OPT.equals(token.getType())) {
            CommandItem item;
            if (isAlias) {
                item = itemManager.getItemAlias(token.getValue(), prevMainItem);
            } else {
                item = itemManager.getItem(token.getValue(), prevMainItem);
            }
            updatePrevItem(item);

            prevExecutableType = PrevExecutableType.OPT;

            next();
        } else {
            throw new CommandSyntaxException("Command type not feat, near: " + token.toString() + ", token idx: " + tokenCount + ". \n" +
                    "Want: OPT, get: " + token.getType() + ". \n " +
                    "Please check your command input.");
        }
    }

    enum PrevExecutableType {
        MAIN("M"),
        OPT("O"),
        ARG("A");

        private final String value;

        PrevExecutableType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
