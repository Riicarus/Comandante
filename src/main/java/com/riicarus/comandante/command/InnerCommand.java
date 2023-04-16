package com.riicarus.comandante.command;

import com.riicarus.comandante.config.CommandConfig;
import com.riicarus.comandante.main.CommandLauncher;
import com.riicarus.comandante.main.CommandLogger;
import com.riicarus.comandante.manage.CommandContext;

import java.util.List;

/**
 * [FEATURE INFO]<br/>
 * 内置指令
 *
 * @author Riicarus
 * @create 2022-10-17 17:57
 * @since 1.0
 */
public class InnerCommand {

    public static void defineCommand() {
        CommandLauncher.register().builder()
                .main("comandante")
                .opt("version", "v")
                .executor(
                        context -> {
                            String version = CommandConfig.getVersion();
                            context.put(CommandContext.INNER_DATA_PREFIX + "comandante_version", version);
                            CommandLogger.log(version);
                        },
                        "Get Comandante's version"
                );
        CommandLauncher.register().builder()
                .main("comandante")
                .opt("author", "a")
                .executor(
                        context -> {
                            String author = CommandConfig.getAuthor();
                            context.put(CommandContext.INNER_DATA_PREFIX + "comandante_author", author);
                            CommandLogger.log(author);
                        },
                        "Get Comandante's author"
                );
        CommandLauncher.register().builder()
                .main("comandante")
                .opt("doc", "d")
                .executor(
                        context -> {
                            String doc = CommandConfig.getDoc();
                            context.put(CommandContext.INNER_DATA_PREFIX + "comandante_doc", doc);
                            CommandLogger.log(doc);
                        },
                        "Get Comandante's document link"
                );
        CommandLauncher.register().builder()
                .main("comandante")
                .opt("info", "i")
                .executor(
                        context -> {
                            String info = "version=" + CommandConfig.getVersion() + "\n" +
                                    "author=" + CommandConfig.getAuthor() + "\n" +
                                    "doc_link=" + CommandConfig.getDoc();
                            context.put(CommandContext.INNER_DATA_PREFIX + "comandante_info", info);
                            CommandLogger.log(info);
                        },
                        "Get Comandante's information"
                );
        CommandLauncher.register().builder()
                .main("comandante")
                .opt("list", "l")
                .executor(
                        context -> {
                            List<String> commandUsages = CommandLauncher.register().getCommandItemManager().listAllCommandUsage();
                            context.put(CommandContext.INNER_DATA_PREFIX + "command_list", commandUsages);
                            commandUsages.forEach(CommandLogger::log);
                        },
                        "List all registered command's and their usages"
                );
    }
}
