package com.riicarus.comandante.manage;

import com.riicarus.comandante.exception.CommandExecutionException;
import com.riicarus.comandante.exception.CommandNotFoundException;
import com.riicarus.comandante.exception.CommandSyntaxException;
import com.riicarus.comandante.executor.CommandExecutor;
import com.riicarus.comandante.main.CommandLogger;
import com.riicarus.comandante.tree.*;
import com.riicarus.util.asserts.Asserts;
import com.riicarus.util.exception.NullObjectException;

import java.util.*;

/**
 * [FEATURE INFO]<br/>
 * 指令分发器
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

    public CommandDispatcher() {
        this.commandRegister = new CommandRegister();
    }

    public CommandDispatcher(CommandRegister commandRegister) {
        this.commandRegister = commandRegister;
    }

    /**
     * 分发并执行指令
     *
     * @param commandStr 指令字符串
     * @throws CommandExecutionException 指令执行异常, 运行时异常
     * @throws CommandNotFoundException 找不到指令异常, 运行时异常
     * @throws NullObjectException 空对象异常, 运行时异常
     */
    public void dispatch(final String commandStr) throws CommandExecutionException, CommandNotFoundException, NullObjectException {
        String[] commandRawParts = split(commandStr);

        List<String> commandStrParts = expandShortOption(commandRawParts);

        CommandContext context = new CommandContext(commandStrParts, commandRegister.getRootNode());

        findMainExecutionNode(context);
        extractOptions(context);

        findThroughMainTree(context);

        CommandExecutor commandExecutor = context.getCurrentNode().getCommandExecutor();

        try {
            for (CommandExecutor executor : context.getOptionExecutors()) {
                executor.execute(context);
            }
            if (commandExecutor != null) {
                commandExecutor.execute(context);
            }
            context.getOutputData().values().forEach(CommandLogger::log);
        } catch (Exception e) {
            throw new CommandExecutionException("Command[" + commandStr + "] execute failed.");
        }
    }

    /**
     * 分割指令字符串, 将字符串按照指令分割符分开, 但是被参数括符括起来的部分不被分开, 认定为一个参数
     *
     * @param commandStr 指令字符串
     * @return 分割后的字符串数组
     * @throws CommandSyntaxException 指令语法错误异常, 运行时异常
     */
    private String[] split(String commandStr) throws CommandSyntaxException {
        Asserts.notNull(commandStr, new CommandSyntaxException("Command String can not be null."));

        List<Integer> indexOfArgumentQuote = new ArrayList<>();
        List<String> commandRawParts = new ArrayList<>();

        // 获取参数括符
        int index = -1;
        while ((index = commandStr.indexOf(ARGUMENT_QUOTE, index + 1)) != -1) {
            if (commandStr.charAt(index - 1) != ESCAPE_MODIFIER) {
                indexOfArgumentQuote.add(index);
            } else {
                // 去除掉转义字符
                commandStr = commandStr.substring(0, index - 1) + commandStr.substring(index);
                // 这里注意要回退一个字符
                index --;
            }
        }

        // 没有参数括符
        if (indexOfArgumentQuote.size() == 0) {
            return commandStr.split(COMMAND_PART_SEPARATOR);
        }

        // 参数括符为奇数
        if (indexOfArgumentQuote.size() % 2 == 1) {
            throw new CommandSyntaxException("The number of argument quote should be even.");
        }

        // 分割指令
        for (int i = 0; i < indexOfArgumentQuote.size() / 2; i++) {
            // +2 会忽略掉中间的空格和单引号
            int startIndex = (i == 0) ? 0 : indexOfArgumentQuote.get(2 * i - 1) + 2;

            // 解析两个参数定义中间的其他指令部分(上一个右参数引号 --> 下一个左参数引号之间的部分)
            // 两端参数之间可能没有其他指令, 先判断是否存在再决定是否解析
            if (startIndex < indexOfArgumentQuote.get(2 * i) - 1) {
                String simpleCommandPart = commandStr.substring(startIndex, indexOfArgumentQuote.get(2 * i) - 1);
                // 普通类型的就直接按 " " 分割放入
                commandRawParts.addAll(Arrays.asList(simpleCommandPart.split(COMMAND_PART_SEPARATOR)));
            }

            // 解析当前参数括符括起来的参数定义, 保留参数括符, 用于解决后续 ExecutionNode 和 ArgumentNode 的解析冲突
            String quotedArgumentPart =
                    commandStr.substring(indexOfArgumentQuote.get(2 * i), indexOfArgumentQuote.get(2 * i + 1) + 1);

            // 被括起来的就一并放入, 表示一个参数
            commandRawParts.add(quotedArgumentPart);
        }

        // 注意还可能后面有一段指令
        // +2 会忽略掉中间的空格和单引号
        if (indexOfArgumentQuote.get(indexOfArgumentQuote.size() - 1) + 2 < commandStr.length()) {
            String simpleCommandPart = commandStr.substring(indexOfArgumentQuote.get(indexOfArgumentQuote.size() - 1) + 2);
            if (!simpleCommandPart.isEmpty()) {
                commandRawParts.addAll(Arrays.asList(simpleCommandPart.split(COMMAND_PART_SEPARATOR)));
            }
        }

        return commandRawParts.toArray(new String[]{});
    }

    /**
     * 将指令字符串解析为和节点对应的指令部分字符串数组, 将合并的短指令分解为单个的短指令
     *
     * @param commandRawParts 分割后的指令部分字符串数组
     * @return 指令部分字符串列表
     */
    private List<String> expandShortOption(final String[] commandRawParts) {
        List<String> commandPartList = new ArrayList<>();

        for (String commandRawPart : commandRawParts) {
            if (!commandRawPart.startsWith(LONG_OPTION_PREFIX_STRING) && commandRawPart.startsWith(SHORT_OPTION_PREFIX_STRING)) {
                // 是 short-option 的情况
                String tmp = commandRawPart.substring(SHORT_OPTION_PREFIX_STRING.length());
                if (tmp.length() > 1) {
                    char[] chars = tmp.toCharArray();
                    for (char c : chars) {
                        commandPartList.add(SHORT_OPTION_PREFIX_STRING + c);
                    }
                } else {
                    commandPartList.add(commandRawPart);
                }
            } else {
                // 其他情况, 直接加入列表
                commandPartList.add(commandRawPart);
            }
        }

        return commandPartList;
    }

    /**
     * 根据指令部分的第一个元素获取指令主节点
     *
     * @param context 指令上下文
     * @throws CommandNotFoundException 找不到指令异常, 运行时异常
     * @throws NullObjectException 空对象异常, 运行时异常
     */
    private void findMainExecutionNode(final CommandContext context) throws CommandNotFoundException, NullObjectException {
        Asserts.notNull(context, "Context can not be null.");

        String mainExecutionStr = context.getCommandStrParts().get(0);
        // 第一次调用节点肯定为 RootNode
        RootNode rootNode = (RootNode) context.getCurrentNode();

        ExecutionNode mainExecutionNode = rootNode.getExecution(mainExecutionStr);

        Asserts.notNull(mainExecutionNode, new CommandNotFoundException("Can not find ExecutionNode[" + mainExecutionStr + "].",
                context, mainExecutionStr));

        context.setMainExecutionNode(mainExecutionNode);
        context.setCurrentNode(mainExecutionNode);
    }

    /**
     * 从指令部分字符串中提取出所有的 OptionNode, 按序保存进 CommandContext 中<br/>
     * 同时会将指令主干节点对应字符串列表更新到 CommandContext 中<br/>
     *
     * @param context 指令上下文
     * @throws NullObjectException 空对象异常, 运行时异常
     */
    private void extractOptions(final CommandContext context) throws NullObjectException {
        Asserts.notNull(context, "Context can not be null.");

        ExecutionNode mainExecutionNode = context.getMainExecutionNode();
        HashMap<String, OptionNode> allOptions = mainExecutionNode.getAllOptions();

        List<String> commandStrParts = context.getCommandStrParts();

        // 遍历过程中的指令字符串部分索引
        int currentStrPartIndex = 0;
        for (String commandStrPart : commandStrParts) {
            boolean isLongOption = commandStrPart.startsWith(LONG_OPTION_PREFIX_STRING);
            boolean isShortOption = !isLongOption && commandStrPart.startsWith(SHORT_OPTION_PREFIX_STRING);

            // 不是 OptionNode 就继续遍历
            if (!isLongOption && !isShortOption) {
                currentStrPartIndex ++;
                continue;
            }

            // 是 OptionNode, 进行处理
            String commandStr;
            // 去掉前缀
            if (isLongOption) {
                commandStr = commandStrPart.substring(LONG_OPTION_PREFIX_STRING.length());
            } else {
                commandStr = commandStrPart.substring(SHORT_OPTION_PREFIX_STRING.length());
            }

            // 获取 OptionNode
            OptionNode optionNode = mainExecutionNode.getAnyOption(commandStr);
            if (optionNode == null) {
                throw new CommandNotFoundException("Can not find OptionNode[" + commandStr + "].",
                        context, commandStr);
            }
            CommandExecutor commandExecutor = extractOptionAndArgument(optionNode, commandStrParts, currentStrPartIndex, context);
            context.addOptionExecutor(commandExecutor);

            currentStrPartIndex ++;
        }
    }

    /**
     * 从指令部分字符串中抽离 OptionNode 的参数, 组成 ParamUnit
     *
     * @param optionNode 当前寻找依据的 OptionNode
     * @param commandStrParts 指令部分字符串
     * @param index 当前 OptionNode 对应指令部分字符串中的索引
     * @param context 指令上下文
     * @return ParamUnit
     * @throws NullObjectException 空对象异常, 运行时异常
     */
    private CommandExecutor extractOptionAndArgument(final OptionNode optionNode,
                                               final List<String> commandStrParts,
                                               int index,
                                               final CommandContext context) throws NullObjectException {
        Asserts.notNull(context, "Context can not be null.");
        Asserts.notNull(optionNode, "OptionNode can not be null.");

        // 移除 OptionNode 对应的字符串部分
        context.deleteNotMainPart(index - context.getDeletedCount());

        if (!optionNode.requireArg()) {
            return optionNode.getCommandExecutor();
        }

        HashMap<String, Object> args = new HashMap<>();
        // OptionNode 后的参数节点的注册名称一定为 ArgumentNode.OPTION_ARGUMENT_NAME
        ArgumentNode<?> argumentNode = optionNode.getArgument(ArgumentNode.OPTION_ARGUMENT_NAME);

        // 处理 OptionNode 的后续参数节点
        while (!argumentNode.getArguments().isEmpty()) {
            String raw_arg;
            try {
                raw_arg = commandStrParts.get(index + 1);
            } catch (IndexOutOfBoundsException e) {
                throw new CommandSyntaxException("option[" + optionNode.getName() + "] need arguments.");
            }
            String arg = raw_arg.startsWith(ARGUMENT_QUOTE) ? raw_arg.substring(1, raw_arg.length() - 1) : raw_arg;
            args.put(argumentNode.getName(), argumentNode.parse(arg));

            argumentNode = argumentNode.getArgument(ArgumentNode.OPTION_ARGUMENT_NAME);
            index++;
            context.deleteNotMainPart(index - context.getDeletedCount());
        }
        // 处理最后一个参数节点
        String raw_arg;
        try {
            raw_arg = commandStrParts.get(index + 1);
        } catch (IndexOutOfBoundsException e) {
            throw new CommandSyntaxException("option[" + optionNode.getName() + "] need arguments.");
        }
        String arg = raw_arg.startsWith(ARGUMENT_QUOTE) ? raw_arg.substring(1, raw_arg.length() - 1) : raw_arg;
        args.put(argumentNode.getName(), argumentNode.parse(arg));
        index++;
        context.deleteNotMainPart(index - context.getDeletedCount());
        context.putArgument(optionNode.getName(), args);

        return argumentNode.getCommandExecutor();
    }

    /**
     * 递归调用, 沿着指令树主干寻找下一个节点, 直到找到最后一个指令节点<br/>
     * 遇到节点时, 先判断是否为 ExecutionNode, 不是就认为是 ArgumentNode<br/>
     * <br/>
     * 如有需要会在 CommandContext 中更新对应 data 的值<br/>
     * 每次迭代都会更新 CommandContext 中相关变量的值<br/>
     *
     * @param context 指令上下文
     * @throws CommandNotFoundException 找不到指令异常, 运行时异常
     * @throws NullObjectException 空对象异常, 运行时异常
     */
    private void findThroughMainTree(final CommandContext context) throws CommandNotFoundException, NullObjectException {
        Asserts.notNull(context, "Context can not be null.");

        // 第一个指令部分字符串已经在 findMainExecutionNode() 中被解析过了, 因此从第二个开始
        List<String> commandMainParts = context.getCommandMainParts().subList(1, context.getCommandMainParts().size());
        // 遍历主干节点字符串, 直到全部找到对应的节点
        for (String commandMainPart : commandMainParts) {
            // 先判断是否为 ExecutionNode
            ExecutionNode executionNode = context.getCurrentNode().getExecution(commandMainPart);
            if (executionNode != null) {
                context.setCurrentNode(executionNode);
                continue;
            }

            // 不是 ExecutionNode, 就应该为 ArgumentNode
            ArgumentNode<?> argumentNode = context.getCurrentNode().getArgument(ArgumentNode.EXECUTION_ARGUMENT_NAME);
            if (argumentNode != null) {
                String key = context.getCurrentNode().getName() + EXE_ARG_DATA_SEPARATOR + argumentNode.getName();
                String arg = commandMainPart.startsWith(ARGUMENT_QUOTE) ?
                        commandMainPart.substring(1, commandMainPart.length() - 1) : commandMainPart;
                context.putArgument(key, argumentNode.parse(arg));
                context.setCurrentNode(argumentNode);

                continue;
            }

            throw new CommandNotFoundException("Can not find node of part[" + commandMainPart + "].",
                    context, commandMainPart);
        }
    }

    public CommandRegister getCommandRegister() {
        return commandRegister;
    }
}
