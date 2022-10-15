package com.skyline.command.executor;

import com.skyline.command.exception.CommandExecutionException;
import com.skyline.command.tree.CommandNode;

import java.lang.reflect.Method;

/**
 * [FEATURE INFO]<br/>
 * 指令执行器
 *
 * @author Skyline
 * @create 2022-10-15 0:09
 * @since 1.0.0
 */
public class CommandExecutor {

    private final CommandNode commandNode;

    private final Method method;

    public CommandExecutor(final CommandNode commandNode, final Method method) {
        this.commandNode = commandNode;
        this.method = method;
    }

    public final void execute(Object... args) {
        Object methodClassInstance;

        try {
            methodClassInstance = method.getDeclaringClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new CommandExecutionException("Create method instance failed.", e.getCause());
        } catch (NullPointerException e) {
            throw new CommandExecutionException("No method found in this executor.", e.getCause());
        }

        doExecute(methodClassInstance, args);
    }

    void doExecute(final Object methodClassInstance, Object... args) {
        try {
            method.invoke(methodClassInstance, args);
        } catch (Exception e) {
            System.out.println("Method [" + method.getName() + "] invoke failed.");
        }
    }

    public Method getMethod() {
        return method;
    }
}
