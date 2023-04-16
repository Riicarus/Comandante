package com.riicarus.comandante.command;

import com.riicarus.comandante.config.CommandConfig;
import com.riicarus.comandante.main.CommandLauncher;
import com.riicarus.comandante.main.CommandLogger;
import com.riicarus.comandante.manage.CommandContext;

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
                        "查看 Comandante 版本号"
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
                        "查看 Comandante 作者"
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
                        "查看 Comandante 文档"
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
                        "查看 Comandante 信息"
                );
        CommandLauncher.register().builder()
                .main("comandante")
                .main("list")
                .opt("command", "c")
                .executor(
                        context -> {
                        },
                        "列出所有已注册指令"
                );
    }
}
