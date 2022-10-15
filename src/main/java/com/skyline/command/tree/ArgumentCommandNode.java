package com.skyline.command.tree;

import com.skyline.command.executor.CommandExecutor;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * [FEATURE INFO]<br/>
 * 参数指令节点
 *
 * @author Skyline
 * @create 2022-10-14 23:49
 * @since 1.0.0
 */
public class ArgumentCommandNode extends CommandNode {

    public static final String ARGUMENT_NAME = "args";

    private final List<CommandArgument<?>> argumentList;

    public ArgumentCommandNode(final CommandExecutor commandExecutor, final List<CommandArgument<?>> argumentList) {
        super(ARGUMENT_NAME, null, null,
                null, null,
                null, commandExecutor);

        this.argumentList = argumentList;
    }

    @Override
    public HashMap<String, ActionCommandNode> getSubActions() {
        return null;
    }

    public List<CommandArgument<?>> getArgumentList() {
        return argumentList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArgumentCommandNode that = (ArgumentCommandNode) o;
        return Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}