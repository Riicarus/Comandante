package com.riicarus.comandante.manage;

import com.riicarus.comandante.exception.CommandExecutionException;
import com.riicarus.comandante.exception.CommandNotFoundException;
import com.riicarus.comandante.exception.CommandSyntaxException;
import com.riicarus.comandante.executor.AnalyzedExecutor;
import com.riicarus.comandante.executor.CommandExecutor;
import com.riicarus.comandante.executor.GeneratedExecutor;
import com.riicarus.comandante.main.CommandLogger;

import java.util.LinkedList;
import java.util.List;

/**
 * [FEATURE INFO]<br/>
 * Grammar analyzer. <br/>
 * The detailed grammar --> To doc.<br/>
 * Every option item will be analyzed and executed. But the main item will only be executed once, which is the last one.
 *
 * @author Riicarus
 * @create 2023-4-13 13:48
 * @since 3.0
 */
public class GrammarAnalyzer {
    /**
     * LexicalAnalyzer produces CommandTokens to GrammarAnalyzer.
     */
    private final LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();
    /**
     * The itemManager maintains the registered command items.
     */
    private final CommandItemManager itemManager;
    /**
     * The arguments which is belongs to the current command executor.
     */
    private final List<String> arguments = new LinkedList<>();
    /**
     * The analyzed executors maintains the executors built by grammar analyzer.<br/>
     * Each of the analyzer refers to a CommandExecutor in the given command.<br/>
     */
    private final List<AnalyzedExecutor> analyzedExecutors = new LinkedList<>();
    /**
     * The generated executor is the executor generated by GrammarAnalyzer and will execute all analyzedExecutors in the given command.
     */
    private GeneratedExecutor generatedExecutor;
    /**
     * Token stores the current analyzing token generated by LexicalAnalyzer.
     */
    private CommandToken token;
    /**
     * TokenCount points to the current analyzing token's index, used in exception handling.
     */
    private int tokenCount = 0;
    /**
     * PrevItem points to the previous analyzed CommandItem.
     */
    private CommandItem prevItem = CommandItem.ROOT;
    /**
     * PrevMainItem points to the previous analyzed main CommandItem.
     */
    private CommandItem prevMainItem = CommandItem.ROOT;
    /**
     * End refers the current command input's token is all analyzed.
     */
    private boolean end = false;
    /**
     * Is the next executor needs the pipe argument from the prev executor.
     */
    private boolean needPipe = false;
    /**
     * PrevExecutableType refers the previous executor's type, which is Main, Opt or Arg.
     */
    private PrevExecutableType prevExecutableType;
    /**
     * PrevException maintains the previous exception to get the real exception when facing another exception.<br/>
     * It's updated in the method which will not throw command syntax exception.
     */
    private CommandSyntaxException prevException;

    public GrammarAnalyzer(CommandItemManager itemManager) {
        this.itemManager = itemManager;
    }

    /**
     * Analyze one command input and returns the analyzed executor list.
     *
     * @param commandStr command input
     * @return generated executor
     */
    public GeneratedExecutor analyze(String commandStr) {
        resetForNextCommand();
        lexicalAnalyzer.input(commandStr);
        next();
        S();

        generatedExecutor = new GeneratedExecutor(() -> {
            if (analyzedExecutors.isEmpty()) {
                throw new CommandExecutionException("No executable command found.");
            }

            for (AnalyzedExecutor executor : analyzedExecutors) {
                CommandLogger.log(executor.execute().toString());
            }
        });

        return generatedExecutor;
    }

    /**
     * Iterate next token, get from lexical analyzer.<br/>
     * If the command's tokens is all analyzed, it will not iterate.
     */
    protected void next() {
        token = lexicalAnalyzer.analyzeOne();
        if (token == null) {
            end = true;
        } else {
            tokenCount++;
        }
    }

    /**
     * Reset arguments for next executor.
     */
    protected void resetArguments() {
        this.arguments.clear();
    }

    /**
     * Reset grammar analyzer's variables for next command input.
     */
    protected void resetForNextCommand() {
        this.token = null;
        this.tokenCount = 0;
        this.prevItem = CommandItem.ROOT;
        this.prevMainItem = CommandItem.ROOT;
        this.end = false;
        this.prevExecutableType = null;
        this.prevException = null;
        this.generatedExecutor = null;
        this.analyzedExecutors.clear();

        resetArguments();
    }

    /**
     * Update previous item. If the item is null, it will throw an exception.
     *
     * @param item current prev item
     * @throws CommandNotFoundException runtime exception
     */
    protected void updatePrevItem(CommandItem item) throws CommandNotFoundException {
        if (item == null) {
            throw new CommandNotFoundException("Command not found, near: " + token.toString() + ", token idx: " + tokenCount + ". \n" +
                    "Please check your command definition.");
        }

        prevItem = item;
    }

    /**
     * Update previous main item. If the item is null, it will throw an exception.
     *
     * @param item current prev main items
     * @throws CommandNotFoundException runtime exception
     */
    protected void updatePrevMainItem(CommandItem item) throws CommandNotFoundException {
        if (item == null) {
            throw new CommandNotFoundException("Command not found, near: " + token.toString() + ", token idx: " + tokenCount + ". \n" +
                    "Please check your command definition.");
        }

        prevMainItem = item;
    }

    /**
     * Add analyzed executor to the analyzedExecutors list.<br/>
     * If it's a common command executor, it will simply add.<br/>
     * But for a command executor after the pipeline identifier, it will be add to the previous executor's pipeToExecutor field.
     *
     * @param executor current CommandExecutor analyzed from command string
     */
    protected void addAnalyzedExecutor(CommandExecutor executor) {
        if (executor != null) {
            AnalyzedExecutor analyzedExecutor = new AnalyzedExecutor();
            analyzedExecutor.setCommandExecutor(executor);
            analyzedExecutor.setArguments(arguments);

            if (needPipe) {
                analyzedExecutor.setPipeFromExecutor(analyzedExecutors.get(analyzedExecutors.size() - 1));
                analyzedExecutors.remove(analyzedExecutors.size() - 1);
                needPipe = false;
            }

            analyzedExecutors.add(analyzedExecutor);

            resetArguments();
        }
    }

    protected void S() throws CommandSyntaxException {
        C();
        N();

        if (!end && prevException != null) {
            throw prevException;
        }

        // M -> End, O -> End, A -> End
        CommandExecutor executor = itemManager.findExecutor(prevItem);
        addAnalyzedExecutor(executor);
    }

    protected void C() throws CommandSyntaxException {
        M();
        M1();
        Y();
    }

    protected void N() {
        if (!end) {
            try {
                T();
                C();
                N();
            } catch (CommandSyntaxException e) {
                prevException = e;
            }
        }
    }

    protected void M() throws CommandSyntaxException {
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
                if (prevException != null) {
                    throw prevException;
                } else {
                    throw new CommandSyntaxException("Command type not feat, near: " + token.toString() + ", token idx: " + tokenCount + ". \n" +
                            "Want: MAIN or ARGUMENT, get: " + token.getType() + ". \n" +
                            "Please check your command input, and the pipeline(|) or command linker(&) can only be followed by MAIN.");
                }
            }
        } else {
            if (prevException != null) {
                throw prevException;
            } else {
                throw new CommandSyntaxException("Command type not feat, near: " + token.toString() + ", token idx: " + tokenCount + ". \n" +
                        "Want: MAIN or ARGUMENT, get: " + token.getType() + ". \n" +
                        "Please check your command input.");
            }
        }
    }

    protected void M1() {
        if (!end) {
            try {
                M();
                M1();
            } catch (CommandSyntaxException e) {
                prevException = e;
            }
        }
    }

    protected void Y() throws CommandSyntaxException {
        if (!end) {
            try {
                O();
                Y();
                return;
            } catch (CommandSyntaxException e) {
                prevException = e;
            }

            try {
                A();
                Y();
            } catch (CommandSyntaxException e) {
                prevException = e;
            }
        }
    }

    protected void T() throws CommandSyntaxException {
        boolean isPipe = false;

        if (CommandTokenType.PREFIX_IDENTIFIER.equals(token.getType())) {
            CommandItem prevCommandItem = this.prevItem;
            if (FixedLexicalItemValue.PIPELINE_IDENTIFIER.getValue().equals(token.getValue())) {
                this.prevItem = CommandItem.ROOT;
                this.prevMainItem = CommandItem.ROOT;
                isPipe = true;
                next();
            } else if (FixedLexicalItemValue.COMMAND_LINKER.getValue().equals(token.getValue())) {
                this.prevItem = CommandItem.ROOT;
                this.prevMainItem = CommandItem.ROOT;
                next();
            } else {
                if (prevException != null) {
                    throw prevException;
                } else {
                    throw new CommandSyntaxException("Command type not feat, near: " + token.toString() + ", token idx: " + tokenCount + ". \n" +
                            "Want: PIPELINE(|) or COMMAND_LINKER(&), get: " + token.getType() + ". \n" +
                            "Please check your command input.");
                }
            }

            // M -> T, O -> T, A -> T
            CommandExecutor executor = itemManager.findExecutor(prevCommandItem);
            addAnalyzedExecutor(executor);

            // If is pipeline identifier, set needPipe to true.
            needPipe = isPipe;
        }
    }

    protected void A() throws CommandSyntaxException {
        if (CommandTokenType.PREFIX_IDENTIFIER.equals(token.getType()) && FixedLexicalItemValue.ARGUMENT_QUOTE.getValue().equals(token.getValue())) {
            next();
            A1();
            if (CommandTokenType.PREFIX_IDENTIFIER.equals(token.getType()) && FixedLexicalItemValue.ARGUMENT_QUOTE.getValue().equals(token.getValue())) {
                next();
            } else {
                if (prevException != null) {
                    throw prevException;
                } else {
                    throw new CommandSyntaxException("Command type not feat, near: " + token.toString() + ", token idx: " + tokenCount + ". \n" +
                            "Want: ARGUMENT_QUOTE, get: " + token.getType() + ". \n" +
                            "Please check your command input.");
                }
            }
        } else if (CommandTokenType.MAIN_OR_ARGUMENT.equals(token.getType()) && !CommandItem.ROOT.equals(prevItem)) {
            A1();
        } else {
            if (prevException != null) {
                throw prevException;
            } else {
                throw new CommandSyntaxException("Command type not feat, near: " + token.toString() + ", token idx: " + tokenCount + ". \n" +
                        "Want: ARGUMENT_QUOTE, get: " + token.getType() + ". \n" +
                        "Please check your command input.");
            }
        }
    }

    protected void A1() throws CommandSyntaxException {
        if (CommandTokenType.ARGUMENT.equals(token.getType()) || CommandTokenType.MAIN_OR_ARGUMENT.equals(token.getType())) {
            updatePrevItem(itemManager.getItem(FixedLexicalItemValue.ARGUMENT.getValue(), prevItem));
            arguments.add(token.getValue());
            prevExecutableType = PrevExecutableType.ARG;
            next();
        } else {
            if (prevException != null) {
                throw prevException;
            } else {
                throw new CommandSyntaxException("Command type not feat, near: " + token.toString() + ", token idx: " + tokenCount + ". \n" +
                        "Want: ARGUMENT, get: " + token.getType() + ". \n" +
                        "Please check your command input.");
            }
        }
    }

    protected void O() throws CommandSyntaxException {
        if (CommandTokenType.PREFIX_IDENTIFIER.equals(token.getType()) && FixedLexicalItemValue.OPT_PREFIX.getValue().equals(token.getValue())) {
            next();
            if (CommandTokenType.PREFIX_IDENTIFIER.equals(token.getType()) && FixedLexicalItemValue.OPT_PREFIX.getValue().equals(token.getValue())) {
                next();
                O1(false);
            } else {
                O1(true);
                O2();
            }
        } else {
            if (prevException != null) {
                throw prevException;
            } else {
                throw new CommandSyntaxException("Command type not feat, near: " + token.toString() + ", token idx: " + tokenCount + ". \n" +
                        "Want: OPT_PREFIX, get: " + token.getType() + ". \n" +
                        "Please check your command input.");
            }
        }
    }

    protected void O2() throws CommandSyntaxException {
        if (!end) {
            try {
                O1(true);
                O2();
            } catch (CommandSyntaxException e) {
                prevException = e;
            }
        }
    }

    protected void O1(boolean isAlias) throws CommandSyntaxException {
        if (CommandTokenType.OPT.equals(token.getType())) {
            if (PrevExecutableType.OPT.equals(prevExecutableType) || PrevExecutableType.ARG.equals(prevExecutableType)) {
                // O -> O, A -> O
                CommandExecutor executor = itemManager.findExecutor(prevItem);
                addAnalyzedExecutor(executor);
            }

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
            if (prevException != null) {
                throw prevException;
            } else {
                throw new CommandSyntaxException("Command type not feat, near: " + token.toString() + ", token idx: " + tokenCount + ". \n" +
                        "Want: OPT, get: " + token.getType() + ". \n" +
                        "Please check your command input.");
            }
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
