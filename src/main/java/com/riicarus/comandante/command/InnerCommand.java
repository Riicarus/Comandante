package com.riicarus.comandante.command;

import com.riicarus.comandante.main.CommandLauncher;
import com.riicarus.comandante.config.CommandConfig;
import com.riicarus.comandante.main.CommandLogger;

import java.util.List;
import java.util.Set;

/**
 * [FEATURE INFO]<br/>
 * 内置指令
 *
 * @author Skyline
 * @create 2022-10-17 17:57
 * @since 1.0
 */
public class InnerCommand {

    public static void defineCommand() {
        CommandLauncher.register().exe("comandante")
                .opt("version", "v")
                .executor(
                        context -> CommandLogger.log(CommandConfig.getVersion()),
                        "查看 Comandante 版本号"
        );
        CommandLauncher.register().exe("comandante")
                .opt("author", "a")
                .executor(
                        context -> CommandLogger.log(CommandConfig.getAuthor()),
                        "查看 Comandante 作者"
        );
        CommandLauncher.register().exe("comandante")
                .opt("doc", "d")
                .executor(
                        context -> CommandLogger.log(CommandConfig.getDoc()),
                        "查看 Comandante 文档"
        );
        CommandLauncher.register().exe("comandante")
                .opt("info", "i")
                .executor(
                        context -> {
                            CommandLogger.log(CommandConfig.getVersion());
                            CommandLogger.log(CommandConfig.getAuthor());
                            CommandLogger.log(CommandConfig.getDoc());
                        },
                        "查看 Comandante 信息"
                );
        CommandLauncher.register().exe("comandante").exe("list")
                .opt("all")
                .executor(
                        context -> {
                            Set<String> commandSet = CommandLauncher.listAllExecutionCommand();
                            commandSet.forEach(CommandLogger::log);
                        },
                        "列出所有已注册指令"
                );
        CommandLauncher.register().exe("comandante").exe("list")
                .opt("usage", "u")
                .executor(
                        context -> {
                            List<String> commandUsage = CommandLauncher.listCommandUsage();
                            commandUsage.forEach(CommandLogger::log);
                        },
                        "列出指令使用情况"
                );
    }
}
