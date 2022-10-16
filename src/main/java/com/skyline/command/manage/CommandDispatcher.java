package com.skyline.command.manage;

import com.skyline.command.exception.CommandNotFoundException;
import com.skyline.command.executor.CommandExecutor;
import com.skyline.command.tree.CommandNode;
import com.skyline.command.tree.OptionCommandNode;
import com.skyline.command.tree.RootCommandNode;

import java.util.ArrayList;
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

    private final CommandRegister commandRegister;

    public CommandDispatcher() {
        this.commandRegister = new CommandRegister();
    }

    public CommandDispatcher(CommandRegister commandRegister) {
        this.commandRegister = commandRegister;
    }

    public void dispatch(final String commandStr) {
        RootCommandNode rootCommandNode = commandRegister.getRootCommandNode();

        String[] commandParts = commandStr.split(COMMAND_PART_SPLIT_STRING);
        if (commandParts.length <= 1) {
            throw new CommandNotFoundException("Command: " + commandStr + " not found.", null);
        }

        List<Object> args = new ArrayList<>();
        CommandNode commandNode = findNext(rootCommandNode, commandParts, 0, false, args);

        // args 依照 指令从左到右的顺序传入
        CommandExecutor executor = commandNode.getCommandExecutor();
        if (executor == null) {
            throw new CommandNotFoundException("No executor bound with this command.", null);
        }
        executor.execute(args.toArray());
    }

    private CommandNode findNext(CommandNode commandNode, String[] commandParts, int index, boolean isOptionOrArg, List<Object> args) {
        CommandNode node = null;

        String commandPart = commandParts[index];

        if (isOptionOrArg && !commandPart.startsWith(SHORT_OPTION_PREFIX_STRING)) {
            // argument
            args.add(commandPart);

            // 这里要求: 参数节点的名称必须和 对应 option 节点的 long-option 名称相同
            node = commandNode.getChildren().get(commandNode.getName());
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
                    if (((OptionCommandNode) child).getAlias().equals(commandPart)) {
                        node = child;
                        break;
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
                    if (((OptionCommandNode) child).getAlias().equals(commandPart)) {
                        node = child;
                        break;
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

        return findNext(node, commandParts, index, isOptionOrArg, args);
    }


    public CommandRegister getCommandRegister() {
        return commandRegister;
    }
}
