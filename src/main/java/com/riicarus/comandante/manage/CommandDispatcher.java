package com.riicarus.comandante.manage;

import com.riicarus.comandante.exception.CommandExecutionException;
import com.riicarus.comandante.exception.CommandNotFoundException;
import com.riicarus.comandante.exception.CommandSyntaxException;
import com.riicarus.comandante.executor.CommandExecutor;
import com.riicarus.comandante.tree.*;

import java.util.*;

/**
 * [FEATURE INFO]<br/>
 * 指令分发器
 *
 * @author Skyline
 * @create 2022-10-15 23:27
 * @since 1.0
 */
public class CommandDispatcher {

    private static final String COMMAND_PART_SPLIT_STRING = " ";
    private static final String SHORT_OPTION_PREFIX_STRING = "-";
    private static final String LONG_OPTION_PREFIX_STRING = "--";
    private static final String ARGUMENT_QUOTE = "'";
    private static final char ESCAPE_MODIFIER = '\\';

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
     */
    public void dispatch(final String commandStr) throws CommandExecutionException {
        String[] commandRawParts = split(commandStr);

        List<String> commandStrParts = expandShortOption(commandRawParts);

        CommandContext context = new CommandContext(commandStr, commandStrParts, commandRegister.getRootNode());
        findNextTillLast(context);

        try {
            CommandExecutor commandExecutor = context.getCurrentNode().getCommandExecutor();
            commandExecutor.execute(context);
        } catch (Exception e) {
            throw new CommandExecutionException("Command[" + commandStr + "] execute failed.");
        }
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
     * 分割指令字符串, 将字符串按照指令分割符分开, 但是被参数括符括起来的部分不被分开, 认定为一个参数
     *
     * @param commandStr 指令字符串
     * @return 分割后的字符串数组
     */
    private String[] split(String commandStr) {
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
            return commandStr.split(COMMAND_PART_SPLIT_STRING);
        }

        // 参数括符为奇数
        if (indexOfArgumentQuote.size() % 2 == 1) {
            throw new CommandSyntaxException("The number of argument quote should be even");
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
                commandRawParts.addAll(Arrays.asList(simpleCommandPart.split(COMMAND_PART_SPLIT_STRING)));
            }

            // 解析当前参数括符括起来的参数定义, 把参数括符截取进去, 用于之后的参数判断
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
                commandRawParts.addAll(Arrays.asList(simpleCommandPart.split(COMMAND_PART_SPLIT_STRING)));
            }
        }


        return commandRawParts.toArray(new String[]{});
    }

    /**
     * 递归调用, 直到找到最后一个指令节点<br/>
     * 如有需要会在 CommandContext 中更新对应 data 的值<br/>
     * 每次迭代都会更新 CommandContext 中相关变量的值<br/>
     *
     * @param context 指令上下文
     */
    public void findNextTillLast(final CommandContext context) {
        if (context.isEnd()) {
            return;
        }

        AbstractNode currentNode = context.getCurrentNode();
        int currentIndex = context.getCurrentIndex();
        String commandStrPart = context.getCommandStrParts().get(currentIndex);

        boolean isShortOptionPart = commandStrPart.startsWith(SHORT_OPTION_PREFIX_STRING) && !commandStrPart.startsWith(LONG_OPTION_PREFIX_STRING);
        boolean isLongOptionPart = commandStrPart.startsWith(LONG_OPTION_PREFIX_STRING);
        boolean isArgumentPart = commandStrPart.startsWith(ARGUMENT_QUOTE);
        boolean notOptionPart = !commandStrPart.startsWith(SHORT_OPTION_PREFIX_STRING) && !isArgumentPart;

        boolean isRoot = currentNode instanceof RootNode;
        boolean isExecution = currentNode instanceof ExecutionNode;
        boolean isOption = currentNode instanceof OptionNode;
        boolean isArgument = currentNode instanceof ArgumentNode<?>;

        if (isRoot) {
            // RootNode 之后一定是 ExecutionNode
            ExecutionNode nextNode = currentNode.getExecutions().get(commandStrPart);
            if (nextNode == null) {
                throw new CommandNotFoundException("Can not find ExecutionNode[" + commandStrPart + "].");
            }

            context.setCurrentNode(nextNode);
            context.setCurrentExecutionNode(nextNode);
        } else if ((isExecution || isOption || isArgument) && (isShortOptionPart || isLongOptionPart)) {
            // 当前为 ExecutionNode 或者 OptionNode, 之后是 OptionNode, 只需要迭代节点
            String commandStr = isShortOptionPart ? commandStrPart.substring(SHORT_OPTION_PREFIX_STRING.length()) :
                    commandStrPart.substring(LONG_OPTION_PREFIX_STRING.length());
            // 如果当前是 ExecutionNode, 就从当前取 OptionNode, 如果不是, 就从上下文中当前的 ExecutionNode 中取 OptionNode
            OptionNode nextNode = null;
            if (isLongOptionPart) {
                 nextNode = isExecution ? currentNode.getOptions().get(commandStr) : context.getCurrentExecutionNode().getOptions().get(commandStr);
            } else {
                HashMap<String, OptionNode> optionNodes = isExecution ? currentNode.getOptions() : context.getCurrentExecutionNode().getOptions();
                for (Map.Entry<String, OptionNode> entry : optionNodes.entrySet()) {
                    String name = entry.getKey();
                    OptionNode node = entry.getValue();
                    if (commandStr.equals(node.getAlias())) {
                        nextNode = node;
                        break;
                    }
                }
            }

            if (nextNode == null) {
                throw new CommandNotFoundException("Can not find OptionNode[" + commandStrPart + "].");
            }

            context.setCurrentNode(nextNode);
            context.setCurrentOptionNode(nextNode);
        } else if ((isExecution || isOption || isArgument) && isArgumentPart) {
            // 当前为 ExecutionNode 或者 OptionNode 或者 ArgumentNode, 之后是 ArgumentNode, 只需要迭代节点
            ArgumentNode<?> nextNode = currentNode.getNextArgument();
            if (nextNode == null) {
                throw new CommandNotFoundException("Can not find ArgumentNode[" + commandStrPart + "].");
            }

            // 这里注意要去掉参数括符
            context.putData(nextNode.getName(), nextNode.parse(commandStrPart.substring(1, commandStrPart.length() - 1)));

            context.setCurrentNode(nextNode);
            context.setCurrentArgumentNode(nextNode);
        } else if ((isExecution || isArgument) && notOptionPart) {
            // 当前为 ExecutionNode 或者 ArgumentNode, 之后是 ExecutionNode 或者 ArgumentNode
            // ExecutionNode 的参数不是二元互斥的, 定义了参数但是可以选择不传递参数(即可选参数)
            boolean isArgumentPart_ = isExecution ? currentNode.getExecutions().get(commandStrPart) == null :
                    context.getCurrentExecutionNode().getExecutions().get(commandStrPart) == null;
            if (!isArgumentPart_) {
                // 如果之后是 ExecutionNode, 只需要迭代
                ExecutionNode nextNode = isExecution ? currentNode.getExecutions().get(commandStrPart):
                        context.getCurrentExecutionNode().getExecutions().get(commandStrPart);
                context.setCurrentNode(nextNode);
                context.setCurrentExecutionNode(nextNode);
            } else {
                // 之后是 ArgumentNode, 将其添加进 CommandContext 的 data 中
                ArgumentNode<?> nextNode = currentNode.getNextArgument();
                if (nextNode == null) {
                    throw new CommandNotFoundException("Can not find ArgumentNode[" + commandStrPart + "].");
                }
                context.putData(nextNode.getName(), nextNode.parse(commandStrPart));

                context.setCurrentNode(nextNode);
                context.setCurrentArgumentNode(nextNode);
            }
        } else if (isOption && notOptionPart) {
            // 当前为 OptionNode, 之后是 ExecutionNode 或 ArgumentNode
            // OptionNode 的参数是二元互斥的, 如果定义了参数, 就必须要传递参数
            if (((OptionNode) currentNode).requireArg()) {
                // 这里表示下一个节点应该是 ArgumentNode
                ArgumentNode<?> nextNode = currentNode.getNextArgument();
                if (nextNode == null) {
                    throw new CommandNotFoundException("Can not find ArgumentNode[" + commandStrPart + "].");
                }

                context.putData(nextNode.getName(), nextNode.parse(commandStrPart));

                context.setCurrentNode(nextNode);
                context.setCurrentArgumentNode(nextNode);
            } else {
                // 这里表示下一个节点是 ExecutionNode, 需要从指令上下文中当前的 ExecutionNode 中取 ExecutionNode
                ExecutionNode nextNode = context.getCurrentExecutionNode().getExecutions().get(commandStrPart);
                if (nextNode == null) {
                    throw new CommandNotFoundException("Can not find ExecutionNode[" + commandStrPart + "].");
                }

                context.setCurrentNode(nextNode);
                context.setCurrentExecutionNode(nextNode);
            }
        } else {
            throw new CommandNotFoundException("Can not find Node[" + commandStrPart + "].");
        }

        context.increaseCurrentIndex();

        findNextTillLast(context);
    }

    public CommandRegister getCommandRegister() {
        return commandRegister;
    }
}
