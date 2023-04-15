package com.riicarus.comandante.manage;

import com.riicarus.comandante.exception.CommandExecutionException;
import com.riicarus.comandante.exception.CommandNotFoundException;
import com.riicarus.comandante.exception.CommandSyntaxException;
import com.riicarus.util.asserts.Asserts;
import com.riicarus.util.exception.NullObjectException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * [FEATURE INFO]<br/>
 * Command Dispatcher
 *
 * @author Riicarus
 * @create 2022-10-15 23:27
 * @since 1.0
 */
public class CommandDispatcher {

    public static final String COMMAND_PART_SEPARATOR = " ";
    public static final String SHORT_OPTION_PREFIX_STRING = "-";
    public static final String LONG_OPTION_PREFIX_STRING = "--";
    public static final String ARGUMENT_QUOTE = "'";
    public static final char ESCAPE_MODIFIER = '\\';
    public static final String EXE_ARG_DATA_SEPARATOR = "#EXE_ARG_DATA#";

    private final CommandRegister commandRegister;

    private final GrammarAnalyzer grammarAnalyzer;

    public CommandDispatcher() {
        this.commandRegister = new CommandRegister();
        this.grammarAnalyzer = new GrammarAnalyzer(commandRegister.getCommandItemManager());
    }

    public CommandDispatcher(CommandRegister commandRegister) {
        this.commandRegister = commandRegister;
        this.grammarAnalyzer = new GrammarAnalyzer(commandRegister.getCommandItemManager());
    }

    /**
     * Dispatch and execute command
     *
     * @param commandStr command string
     * @throws CommandExecutionException runtime exception
     * @throws CommandNotFoundException  runtime exception
     * @throws NullObjectException       runtime exception
     */
    public void dispatch(final String commandStr) throws CommandExecutionException, CommandNotFoundException, NullObjectException {
        grammarAnalyzer.analyze(commandStr);

//        List<String> commandStrParts = expandShortOption(commandRawParts);
//
//        CommandContext context = new CommandContext(commandStrParts, commandRegister.getRootNode());
//
//        findMainExecutionNode(context);
//        extractOptions(context);
//
//        findThroughMainTree(context);
//
//        CommandExecutor commandExecutor = context.getCurrentNode().getCommandExecutor();
//
//        try {
//            for (CommandExecutor executor : context.getOptionExecutors()) {
//                executor.execute(context);
//            }
//            if (commandExecutor != null) {
//                commandExecutor.execute(context);
//            }
//            context.getOutputData().values().forEach(CommandLogger::log);
//        } catch (Exception e) {
//            throw new CommandExecutionException("Command[" + commandStr + "] execute failed.");
//        }
    }

    /**
     * Split command string to command items according to command separator, <br/>
     * but would not split the items quoted by '"', <br/>
     * which will be seeing as a hole argument. <br/>
     *
     * @param commandStr command string
     * @return command items array
     * @throws CommandSyntaxException runtime exception
     */
    private String[] split(String commandStr) throws CommandSyntaxException {
        Asserts.notNull(commandStr, new CommandSyntaxException("Command String can not be null."));

        List<Integer> indexOfArgumentQuote = new ArrayList<>();
        List<String> commandRawParts = new ArrayList<>();

        // get argument quote "'"
        int index = -1;
        while ((index = commandStr.indexOf(ARGUMENT_QUOTE, index + 1)) != -1) {
            if (commandStr.charAt(index - 1) != ESCAPE_MODIFIER) {
                indexOfArgumentQuote.add(index);
            } else {
                // remove escape char
                commandStr = commandStr.substring(0, index - 1) + commandStr.substring(index);
                // mention: here needs to get back one char
                index--;
            }
        }

        // no ARGUMENT_QUOTE
        if (indexOfArgumentQuote.size() == 0) {
            return commandStr.split(COMMAND_PART_SEPARATOR);
        }

        // the ARGUMENT_QUOTE can not be odd
        if (indexOfArgumentQuote.size() % 2 == 1) {
            throw new CommandSyntaxException("The number of argument quote should be odd.");
        }

        // split command items
        for (int i = 0; i < indexOfArgumentQuote.size() / 2; i++) {
            // +2 to ignore the blank space and quote("'")
            int startIndex = (i == 0) ? 0 : indexOfArgumentQuote.get(2 * i - 1) + 2;

            // parse the other command items between two argument items(from the prev right ARGUMENT_QUOTE to the left ARGUMENT_QUOTE)
            // there may not exist any other command items, so we need to judge if exists before parsing
            if (startIndex < indexOfArgumentQuote.get(2 * i) - 1) {
                String simpleCommandPart = commandStr.substring(startIndex, indexOfArgumentQuote.get(2 * i) - 1);
                // the simple command items just need to split by blank space(" ") and put them to list
                commandRawParts.addAll(Arrays.asList(simpleCommandPart.split(COMMAND_PART_SEPARATOR)));
            }

            // parse the quoted argument part and remain the quotes to defer from MainNodes and ArgumentNodes
            String quotedArgumentPart =
                    commandStr.substring(indexOfArgumentQuote.get(2 * i), indexOfArgumentQuote.get(2 * i + 1) + 1);

            // put the quoted argument to list as one argument item
            commandRawParts.add(quotedArgumentPart);
        }

        // mention that there may be a few items which have not been parsed
        // +2 to ignore the blank space and quote("'")
        if (indexOfArgumentQuote.get(indexOfArgumentQuote.size() - 1) + 2 < commandStr.length()) {
            String simpleCommandPart = commandStr.substring(indexOfArgumentQuote.get(indexOfArgumentQuote.size() - 1) + 2);
            if (!simpleCommandPart.isEmpty()) {
                commandRawParts.addAll(Arrays.asList(simpleCommandPart.split(COMMAND_PART_SEPARATOR)));
            }
        }

        return commandRawParts.toArray(new String[]{});
    }
//
//    /**
//     * 将指令字符串解析为和节点对应的指令部分字符串数组, 将合并的短指令分解为单个的短指令
//     *
//     * @param commandRawParts 分割后的指令部分字符串数组
//     * @return 指令部分字符串列表
//     */
//    private List<String> expandShortOption(final String[] commandRawParts) {
//        List<String> commandPartList = new ArrayList<>();
//
//        for (String commandRawPart : commandRawParts) {
//            if (!commandRawPart.startsWith(LONG_OPTION_PREFIX_STRING) && commandRawPart.startsWith(SHORT_OPTION_PREFIX_STRING)) {
//                // 是 short-option 的情况
//                String tmp = commandRawPart.substring(SHORT_OPTION_PREFIX_STRING.length());
//                if (tmp.length() > 1) {
//                    char[] chars = tmp.toCharArray();
//                    for (char c : chars) {
//                        commandPartList.add(SHORT_OPTION_PREFIX_STRING + c);
//                    }
//                } else {
//                    commandPartList.add(commandRawPart);
//                }
//            } else {
//                // 其他情况, 直接加入列表
//                commandPartList.add(commandRawPart);
//            }
//        }
//
//        return commandPartList;
//    }
//
//    /**
//     * 根据指令部分的第一个元素获取指令主节点
//     *
//     * @param context 指令上下文
//     * @throws CommandNotFoundException 找不到指令异常, 运行时异常
//     * @throws NullObjectException      空对象异常, 运行时异常
//     */
//    private void findMainExecutionNode(final CommandContext context) throws CommandNotFoundException, NullObjectException {
//        Asserts.notNull(context, "Context can not be null.");
//
//        String mainExecutionStr = context.getCommandStrParts().get(0);
//        // 第一次调用节点肯定为 RootNode
//        RootNode rootNode = (RootNode) context.getCurrentNode();
//
//        ExecutionNode mainExecutionNode = rootNode.getExecution(mainExecutionStr);
//
//        Asserts.notNull(mainExecutionNode, new CommandNotFoundException("Can not find ExecutionNode[" + mainExecutionStr + "].",
//                context, mainExecutionStr));
//
//        context.setMainExecutionNode(mainExecutionNode);
//        context.setCurrentNode(mainExecutionNode);
//    }
//
//    /**
//     * 从指令部分字符串中提取出所有的 OptionNode, 按序保存进 CommandContext 中<br/>
//     * 同时会将指令主干节点对应字符串列表更新到 CommandContext 中<br/>
//     *
//     * @param context 指令上下文
//     * @throws NullObjectException 空对象异常, 运行时异常
//     */
//    private void extractOptions(final CommandContext context) throws NullObjectException {
//        Asserts.notNull(context, "Context can not be null.");
//
//        ExecutionNode mainExecutionNode = context.getMainExecutionNode();
//        HashMap<String, OptionNode> allOptions = mainExecutionNode.getAllOptions();
//
//        List<String> commandStrParts = context.getCommandStrParts();
//
//        // 遍历过程中的指令字符串部分索引
//        int currentStrPartIndex = 0;
//        for (String commandStrPart : commandStrParts) {
//            boolean isLongOption = commandStrPart.startsWith(LONG_OPTION_PREFIX_STRING);
//            boolean isShortOption = !isLongOption && commandStrPart.startsWith(SHORT_OPTION_PREFIX_STRING);
//
//            // 不是 OptionNode 就继续遍历
//            if (!isLongOption && !isShortOption) {
//                currentStrPartIndex++;
//                continue;
//            }
//
//            // 是 OptionNode, 进行处理
//            String commandStr;
//            // 去掉前缀
//            if (isLongOption) {
//                commandStr = commandStrPart.substring(LONG_OPTION_PREFIX_STRING.length());
//            } else {
//                commandStr = commandStrPart.substring(SHORT_OPTION_PREFIX_STRING.length());
//            }
//
//            // 获取 OptionNode
//            OptionNode optionNode = mainExecutionNode.getAnyOption(commandStr);
//            if (optionNode == null) {
//                throw new CommandNotFoundException("Can not find OptionNode[" + commandStr + "].",
//                        context, commandStr);
//            }
//            CommandExecutor commandExecutor = extractOptionAndArgument(optionNode, commandStrParts, currentStrPartIndex, context);
//            context.addOptionExecutor(commandExecutor);
//
//            currentStrPartIndex++;
//        }
//    }
//
//    /**
//     * 从指令部分字符串中抽离 OptionNode 的参数, 组成 ParamUnit
//     *
//     * @param optionNode      当前寻找依据的 OptionNode
//     * @param commandStrParts 指令部分字符串
//     * @param index           当前 OptionNode 对应指令部分字符串中的索引
//     * @param context         指令上下文
//     * @return ParamUnit
//     * @throws NullObjectException 空对象异常, 运行时异常
//     */
//    private CommandExecutor extractOptionAndArgument(final OptionNode optionNode,
//                                                     final List<String> commandStrParts,
//                                                     int index,
//                                                     final CommandContext context) throws NullObjectException {
//        Asserts.notNull(context, "Context can not be null.");
//        Asserts.notNull(optionNode, "OptionNode can not be null.");
//
//        // 移除 OptionNode 对应的字符串部分
//        context.deleteNotMainPart(index - context.getDeletedCount());
//
//        if (!optionNode.requireArg()) {
//            return optionNode.getCommandExecutor();
//        }
//
//        HashMap<String, Object> args = new HashMap<>();
//        // OptionNode 后的参数节点的注册名称一定为 ArgumentNode.OPTION_ARGUMENT_NAME
//        ArgumentNode<?> argumentNode = optionNode.getArgument(ArgumentNode.OPTION_ARGUMENT_NAME);
//
//        // 处理 OptionNode 的后续参数节点
//        while (!argumentNode.getArguments().isEmpty()) {
//            String raw_arg;
//            try {
//                raw_arg = commandStrParts.get(index + 1);
//            } catch (IndexOutOfBoundsException e) {
//                throw new CommandSyntaxException("option[" + optionNode.getName() + "] need arguments.");
//            }
//            String arg = raw_arg.startsWith(ARGUMENT_QUOTE) ? raw_arg.substring(1, raw_arg.length() - 1) : raw_arg;
//            args.put(argumentNode.getName(), argumentNode.parse(arg));
//
//            argumentNode = argumentNode.getArgument(ArgumentNode.OPTION_ARGUMENT_NAME);
//            index++;
//            context.deleteNotMainPart(index - context.getDeletedCount());
//        }
//        // 处理最后一个参数节点
//        String raw_arg;
//        try {
//            raw_arg = commandStrParts.get(index + 1);
//        } catch (IndexOutOfBoundsException e) {
//            throw new CommandSyntaxException("option[" + optionNode.getName() + "] need arguments.");
//        }
//        String arg = raw_arg.startsWith(ARGUMENT_QUOTE) ? raw_arg.substring(1, raw_arg.length() - 1) : raw_arg;
//        args.put(argumentNode.getName(), argumentNode.parse(arg));
//        index++;
//        context.deleteNotMainPart(index - context.getDeletedCount());
//        context.putArgument(optionNode.getName(), args);
//
//        return argumentNode.getCommandExecutor();
//    }
//
//    /**
//     * 递归调用, 沿着指令树主干寻找下一个节点, 直到找到最后一个指令节点<br/>
//     * 遇到节点时, 先判断是否为 ExecutionNode, 不是就认为是 ArgumentNode<br/>
//     * <br/>
//     * 如有需要会在 CommandContext 中更新对应 data 的值<br/>
//     * 每次迭代都会更新 CommandContext 中相关变量的值<br/>
//     *
//     * @param context 指令上下文
//     * @throws CommandNotFoundException 找不到指令异常, 运行时异常
//     * @throws NullObjectException      空对象异常, 运行时异常
//     */
//    private void findThroughMainTree(final CommandContext context) throws CommandNotFoundException, NullObjectException {
//        Asserts.notNull(context, "Context can not be null.");
//
//        // 第一个指令部分字符串已经在 findMainExecutionNode() 中被解析过了, 因此从第二个开始
//        List<String> commandMainParts = context.getCommandMainParts().subList(1, context.getCommandMainParts().size());
//        // 遍历主干节点字符串, 直到全部找到对应的节点
//        for (String commandMainPart : commandMainParts) {
//            // 先判断是否为 ExecutionNode
//            ExecutionNode executionNode = context.getCurrentNode().getExecution(commandMainPart);
//            if (executionNode != null) {
//                context.setCurrentNode(executionNode);
//                continue;
//            }
//
//            // 不是 ExecutionNode, 就应该为 ArgumentNode
//            ArgumentNode<?> argumentNode = context.getCurrentNode().getArgument(ArgumentNode.EXECUTION_ARGUMENT_NAME);
//            if (argumentNode != null) {
//                String key = context.getCurrentNode().getName() + EXE_ARG_DATA_SEPARATOR + argumentNode.getName();
//                String arg = commandMainPart.startsWith(ARGUMENT_QUOTE) ?
//                        commandMainPart.substring(1, commandMainPart.length() - 1) : commandMainPart;
//                context.putArgument(key, argumentNode.parse(arg));
//                context.setCurrentNode(argumentNode);
//
//                continue;
//            }
//
//            throw new CommandNotFoundException("Can not find node of part[" + commandMainPart + "].",
//                    context, commandMainPart);
//        }
//    }

    public CommandRegister getCommandRegister() {
        return commandRegister;
    }
}
