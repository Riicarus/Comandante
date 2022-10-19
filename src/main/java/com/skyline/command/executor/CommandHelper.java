package com.skyline.command.executor;

import com.skyline.command.exception.CommandExecutionException;
import com.skyline.command.tree.*;

import java.util.HashMap;

/**
 * [FEATURE INFO]<br/>
 * 帮助指令执行器
 *
 * @author Skyline
 * @create 2022-10-19 17:13
 * @since 1.0.0
 */
public class CommandHelper implements CommandExecutor {

    public static final String OPT_QUOTE_LEFT = "[";
    public static final String OPT_QUOTE_RIGHT = "]";
    public static final String ARG_QUOTE_LEFT = "<";
    public static final String ARG_QUOTE_RIGHT = ">";
    private final CommandNode commandNode;

    public CommandHelper(CommandNode commandNode) {
        this.commandNode = commandNode;
    }

    @Override
    public void execute(Object... args) {
        if (commandNode == null) {
            throw new CommandExecutionException("Command node could not be null.", null);
        }
        System.out.println("Format: exe act sub-act [opt] <arg>");

        listAllCommand(commandNode, new StringBuilder());
    }

    private void listAllCommand(CommandNode commandNode, StringBuilder helpStr) {
        if (commandNode == null) {
            System.out.println(helpStr.toString());
            return;
        }

        if (commandNode instanceof ExecutionCommandNode) {
            helpStr.append(commandNode.getName());
        } else if (commandNode instanceof ActionCommandNode) {
            helpStr.append(" ").append(commandNode.getName());
        } else if (commandNode instanceof OptionCommandNode) {
            helpStr.append(" ").append(OPT_QUOTE_LEFT).append(commandNode.getName()).append(OPT_QUOTE_RIGHT);
        } else if (commandNode instanceof ArgumentCommandNode) {
            helpStr.append(" ").append(ARG_QUOTE_LEFT).append(commandNode.getName()).append(ARG_QUOTE_RIGHT);
        }

        if (commandNode.getChildren().isEmpty()) {
            System.out.println(helpStr.toString());
            return;
        }

        String str = helpStr.toString();
        HashMap<String, CommandNode> children = commandNode.getChildren();
        for (CommandNode node : children.values()) {
            listAllCommand(node, new StringBuilder(str));
        }
    }

    public CommandNode getCommandNode() {
        return commandNode;
    }
}
