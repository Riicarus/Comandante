package com.skyline.command.manage;

import com.skyline.command.annotation.Execution;
import com.skyline.command.exception.CommandLoadException;
import com.skyline.command.util.PackageClassScanner;

import java.util.Set;

/**
 * [FEATURE INFO]<br/>
 * 指令定义类加载器
 *
 * @author Skyline
 * @create 2022-10-15 18:57
 * @since 1.0.0
 */
public class CommandLoader {

    private final String commandDefinitionPackage;

    private Set<Class<?>> loadedCommandDefinitionClassSet;

    public CommandLoader(String commandDefinitionPackage) {
        this.commandDefinitionPackage = commandDefinitionPackage;
    }

    public Set<Class<?>> getLoadedCommandDefinitionClassSet() {
        return loadedCommandDefinitionClassSet;
    }

    public void loadCommand() {
        try {
            loadedCommandDefinitionClassSet =
                    new PackageClassScanner().getAnnotationClasses(commandDefinitionPackage, Execution.class);
        } catch (Exception e) {
            throw new CommandLoadException("Command load failed. Please check whether the @Execution annotation is rightly used.", e.getCause());
        }
    }
}
