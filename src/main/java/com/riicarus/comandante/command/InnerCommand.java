package com.riicarus.comandante.command;

import com.riicarus.comandante.argument.IntegerCommandArgumentType;
import com.riicarus.comandante.main.CommandLauncher;
import com.riicarus.comandante.config.CommandConfig;

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
                        context -> context.putOutputData("comandante_version", CommandConfig.getVersion()),
                        "查看 Comandante 版本号"
        );
        CommandLauncher.register().exe("comandante")
                .opt("author", "a")
                .executor(
                        context -> context.putOutputData("comandante_author", CommandConfig.getAuthor()),
                        "查看 Comandante 作者"
        );
        CommandLauncher.register().exe("comandante")
                .opt("doc", "d")
                .executor(
                        context -> context.putOutputData("comandante_doc", CommandConfig.getDoc()),
                        "查看 Comandante 文档"
        );
        CommandLauncher.register().exe("comandante")
                .opt("info", "i")
                .executor(
                        context -> {
                            String builder = "version=" + CommandConfig.getVersion() + "\n" +
                                    "author=" + CommandConfig.getAuthor() + "\n" +
                                    "doc_link=" + CommandConfig.getDoc();
                            context.putOutputData("comandante_info", builder);
                        },
                        "查看 Comandante 信息"
                );
        CommandLauncher.register().exe("comandante").exe("list")
                .opt("command", "c")
                .executor(
                        context -> {
                            Set<String> commandSet = CommandLauncher.listAllExecutionCommand();
                            StringBuilder builder = new StringBuilder();
                            commandSet.forEach(command -> builder.append(command).append("\n"));
                            builder.deleteCharAt(builder.length() - 1);
                            context.putOutputData("command", builder.toString());
                        },
                        "列出所有已注册指令"
                );
        CommandLauncher.register().exe("comandante").exe("list")
                .opt("usage", "u")
                .executor(
                        context -> {
                            HashMap<String, Integer> commandUsage = CommandLauncher.listCommandUsage();
                            context.putCacheData("command_usage", commandUsage);
                            StringBuilder builder = new StringBuilder();
                            commandUsage.forEach((k, v) -> builder.append(k).append("   usage: ").append(v).append("\n"));
                            builder.deleteCharAt(builder.length() - 1);
                            context.putOutputData("command_usage", builder.toString());
                        },
                        "列出指令使用情况"
                );
        CommandLauncher.register().exe("comandante").exe("list")
                .opt("desc")
                .arg("limit", new IntegerCommandArgumentType())
                .executor(
                        context -> {
                            int limit = ((HashMap<String, Integer>) context.getArgument("desc")).get("limit");
                            HashMap<String, Integer> command_usage = (HashMap<String, Integer>) context.getCacheData("command_usage");

                            LinkedHashMap<String, Integer> commandUsageDesc =
                                    CommandLauncher.listCommandUsageDesc(command_usage, limit);

                            context.putCacheData("command_usage_desc", commandUsageDesc);

                            StringBuilder builder = new StringBuilder();
                            commandUsageDesc.forEach((k, v) -> builder.append(k).append("  usage: ").append(v).append("\n"));
                            builder.deleteCharAt(builder.length() - 1);
                            context.putOutputData("command_usage_desc", builder.toString());
                        }
                );
        CommandLauncher.register().exe("comandante").exe("list")
                .opt("asc")
                .arg("limit", new IntegerCommandArgumentType())
                .executor(
                        context -> {
                            int limit = ((HashMap<String, Integer>) context.getArgument("asc")).get("limit");
                            HashMap<String, Integer> command_usage = (HashMap<String, Integer>) context.getCacheData("command_usage");

                            LinkedHashMap<String, Integer> commandUsageAsc =
                                    CommandLauncher.listCommandUsageAsc(command_usage, limit);

                            context.putCacheData("command_usage_asc", commandUsageAsc);

                            StringBuilder builder = new StringBuilder();
                            commandUsageAsc.forEach((k, v) -> builder.append(k).append("  usage: ").append(v).append("\n"));
                            builder.deleteCharAt(builder.length() - 1);
                            context.putOutputData("command_usage_asc", builder.toString());
                        }
                );
    }
}
