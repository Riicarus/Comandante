package com.skyline.command.manage;

import com.skyline.command.tree.RootCommandNode;

import java.util.Set;

/**
 * [FEATURE INFO]<br/>
 * 指令注册器
 *
 * @author Skyline
 * @create 2022-10-15 10:46
 * @since 1.0.0
 */
public class CommandRegister {

    private final String commandDefinitionPackage;

    private final CommandLoader commandLoader;

    private final RootCommandNode rootCommandNode = new RootCommandNode();

    public CommandRegister(String commandDefinitionPackage) {
        this.commandDefinitionPackage = commandDefinitionPackage;
        this.commandLoader = new CommandLoader(commandDefinitionPackage);
    }

    public CommandRegister(String commandDefinitionPackage, CommandLoader commandLoader) {
        this.commandDefinitionPackage = commandDefinitionPackage;
        this.commandLoader = commandLoader;
    }

    public RootCommandNode getRootCommandNode() {
        return rootCommandNode;
    }

    public void doRegister() {
        Set<Class<?>> classSet;
        commandLoader.loadCommand();

        if ((classSet = commandLoader.getLoadedCommandDefinitionClassSet()) != null) {
            classSet.forEach(clazz -> new CommandBuilder(rootCommandNode, clazz).build());
        }
    }

}
