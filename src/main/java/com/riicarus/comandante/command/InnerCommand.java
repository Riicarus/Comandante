package com.riicarus.comandante.command;

import com.riicarus.comandante.argument.IntegerCommandArgumentType;
import com.riicarus.comandante.main.CommandLauncher;
import com.riicarus.comandante.config.CommandConfig;
import com.riicarus.comandante.main.CommandLogger;

import java.util.HashMap;
import java.util.LinkedHashMap;
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

    @SuppressWarnings("unchecked")
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
                            CommandLogger.log("version=" + CommandConfig.getVersion());
                            CommandLogger.log("author=" + CommandConfig.getAuthor());
                            CommandLogger.log("doc_link=" + CommandConfig.getDoc());
                        },
                        "查看 Comandante 信息"
                );
        CommandLauncher.register().exe("comandante").exe("list")
                .opt("command", "c")
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
                            HashMap<String, Integer> commandUsage = CommandLauncher.listCommandUsage();
                            commandUsage.forEach((k, v) -> CommandLogger.log(k + "   usage: " + v));
                        },
                        "列出指令使用情况"
                );
        CommandLauncher.register().exe("comandante").exe("list")
                .opt("usage-desc")
                .arg("limit", new IntegerCommandArgumentType())
                .executor(
                        context -> {
                            int limit = ((HashMap<String, Integer>) context.getData("usage-desc")).get("limit");
                            LinkedHashMap<String, Integer> commandUsageDesc = CommandLauncher.listCommandUsageDesc(limit);
                            commandUsageDesc.forEach((k, v) -> CommandLogger.log(k + "   usage: " + v));
                        }
                );
        CommandLauncher.register().exe("comandante").exe("list")
                .opt("usage-asc")
                .arg("limit", new IntegerCommandArgumentType())
                .executor(
                        context -> {
                            int limit = ((HashMap<String, Integer>) context.getData("usage-asc")).get("limit");
                            LinkedHashMap<String, Integer> commandUsageAsc = CommandLauncher.listCommandUsageAsc(limit);
                            commandUsageAsc.forEach((k, v) -> CommandLogger.log(k + "   usage: " + v));
                        }
                );
    }
}
