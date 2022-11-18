package com.riicarus.comandante.command;

import com.riicarus.comandante.main.CommandLauncher;
import com.riicarus.comandante.config.CommandConfig;
import com.riicarus.comandante.main.CommandLogger;

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
                        (args) -> CommandLogger.log(CommandConfig.getVersion()),
                        "查看 Comandante 版本号"
        );
        CommandLauncher.register().exe("comandante")
                .opt("author", "a")
                .executor(
                        (args) -> CommandLogger.log(CommandConfig.getAuthor()),
                        "查看 Comandante 作者"
        );
        CommandLauncher.register().exe("comandante")
                .opt("doc", "d")
                .executor(
                        (args) -> CommandLogger.log(CommandConfig.getDoc()),
                        "查看 Comandante 文档"
        );
        CommandLauncher.register().exe("comandante")
                .opt("info", "i")
                .executor(
                        (args) -> {
                            CommandLogger.log(CommandConfig.getVersion());
                            CommandLogger.log(CommandConfig.getAuthor());
                            CommandLogger.log(CommandConfig.getDoc());
                        },
                        "查看 Comandante 信息"
                );
        CommandLauncher.register().exe("comandante").exe("list")
                .opt("all")
                .executor(
                        args -> {
                            Set<String> commandSet = CommandLauncher.listAllExecutionCommand();
                            commandSet.forEach(CommandLogger::log);
                        },
                        "列出所有已注册指令"
                );
    }
}
