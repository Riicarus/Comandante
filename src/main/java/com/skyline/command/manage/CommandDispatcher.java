package com.skyline.command.manage;

import com.skyline.command.exception.CommandNotFoundException;
import com.skyline.command.exception.CommandSyntaxException;
import com.skyline.command.executor.CommandExecutor;
import com.skyline.command.tree.ArgumentCommandNode;
import com.skyline.command.tree.CommandNode;
import com.skyline.command.tree.OptionCommandNode;
import com.skyline.command.tree.RootCommandNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * [FEATURE INFO]<br/>
 * 指令分发器
 *
 * @author Skyline
 * @create 2022-10-15 23:27
 * @since 1.0.0
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
    public void dispatch(final String commandStr) {
        RootCommandNode rootCommandNode = commandRegister.getRootCommandNode();

        String[] parsedCommandParts = parseCommand(commandStr);

        if (parsedCommandParts.length <= 1) {
            throw new CommandNotFoundException("Command: " + commandStr + " not found.", null);
        }

        List<Object> args = new ArrayList<>();
        CommandNode commandNode = findLast(rootCommandNode, parsedCommandParts, 0, false, args);

        // args 依照 指令从左到右的顺序传入
        CommandExecutor executor = commandNode.getCommandExecutor();
        if (executor == null) {
            throw new CommandNotFoundException("No executor bound with this command.", null);
        }
        executor.execute(args.toArray());
    }

    /**
     * 将指令字符串解析为和节点对应的指令部分字符串数组, 将合并的短指令分解为单个的短指令
     *
     * @param commandStr 指令字符串
     * @return 指令部分字符串数组
     */
    private String[] parseCommand(final String commandStr) {
        String[] commandRawParts = split(commandStr);

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

        return commandPartList.toArray(new String[]{});
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
            int startIndex = (i == 0) ? 0 : indexOfArgumentQuote.get(2 * i - 1) + 1;
            String simpleCommandPart = commandStr.substring(startIndex, indexOfArgumentQuote.get(2 * i));

            // 普通类型的就直接按 " " 分割放入
            commandRawParts.addAll(Arrays.asList(simpleCommandPart.split(COMMAND_PART_SPLIT_STRING)));

            String quotedArgumentPart =
                    commandStr.substring(indexOfArgumentQuote.get(2 * i) + 1, indexOfArgumentQuote.get(2 * i + 1));

            // 被括起来的就一并放入, 表示一个参数
            commandRawParts.add(quotedArgumentPart);
        }

        // 注意还可能后面有一段指令
        String simpleCommandPart = commandStr.substring(indexOfArgumentQuote.get(indexOfArgumentQuote.size() - 1) + 1);
        if (!simpleCommandPart.isEmpty()) {
            commandRawParts.addAll(Arrays.asList(simpleCommandPart.split(COMMAND_PART_SPLIT_STRING)));
        }

        return commandRawParts.toArray(new String[]{});
    }

    /**
     * 找到在传入指令字符串的环境中, 该指令节点的下一个指令节点
     *
     * @param commandNode 当前指令节点
     * @param commandParts 指令字符串按分隔符 " " 解析后的数组
     * @param index 当前指令字符串数组的 index, 表征下一个要解析的节点在 index 位置
     * @param isOptionOrArg 当前节点是否为 option 或 argument 节点
     * @param args 参数列表, 解析完成后, 应该包含指令中所有传入的参数
     * @return 指令字符串对应的最后一个节点
     */
    private CommandNode findLast(CommandNode commandNode, String[] commandParts, int index, boolean isOptionOrArg, List<Object> args) {
        CommandNode node = null;

        String commandPart = commandParts[index];

        if (isOptionOrArg && !commandPart.startsWith(SHORT_OPTION_PREFIX_STRING)) {
            // 这里要求: 参数节点的名称必须和 对应 option 节点的 long-option 名称相同
            node = commandNode.getChildren().get(commandNode.getName());

            // argument
            args.add(((ArgumentCommandNode<?>) node).parse(commandPart));
        } else if (isOptionOrArg) {
            // option
            if (commandPart.startsWith(LONG_OPTION_PREFIX_STRING)) {
                // long option
                commandPart = commandPart.substring(LONG_OPTION_PREFIX_STRING.length());

                node = commandNode.getChildren().get(commandPart);
            } else if (commandPart.startsWith(SHORT_OPTION_PREFIX_STRING)) {
                // short option
                commandPart = commandPart.substring(SHORT_OPTION_PREFIX_STRING.length());

                for (CommandNode child : commandNode.getChildren().values()) {
                    if (child instanceof OptionCommandNode) {
                        if (((OptionCommandNode) child).getAlias().equals(commandPart)) {
                            node = child;
                            break;
                        }
                    }
                }
            }
        } else {
            // 这里还没有到 option 或 argument 部分
            if (commandPart.startsWith(LONG_OPTION_PREFIX_STRING)) {
                commandPart = commandPart.substring(LONG_OPTION_PREFIX_STRING.length());
                isOptionOrArg = true;

                node = commandNode.getChildren().get(commandPart);
            } else if (commandPart.startsWith(SHORT_OPTION_PREFIX_STRING)) {
                commandPart = commandPart.substring(SHORT_OPTION_PREFIX_STRING.length());
                isOptionOrArg = true;

                for (CommandNode child : commandNode.getChildren().values()) {
                    if (child instanceof OptionCommandNode) {
                        if (((OptionCommandNode) child).getAlias().equals(commandPart)) {
                            node = child;
                            break;
                        }
                    }
                }
            } else {
                node = commandNode.getChildren().get(commandPart);
            }
        }

        if (node == null) {
            throw new CommandNotFoundException("No command definition found for this command.", null);
        }

        if (index == commandParts.length - 1) {
            return node;
        }

        index++;

        return findLast(node, commandParts, index, isOptionOrArg, args);
    }


    public CommandRegister getCommandRegister() {
        return commandRegister;
    }
}
