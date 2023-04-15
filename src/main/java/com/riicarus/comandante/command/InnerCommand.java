package com.riicarus.comandante.command;

import com.riicarus.comandante.config.CommandConfig;
import com.riicarus.comandante.main.CommandLauncher;

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
                        context -> context.putOutputData("comandante_version", CommandConfig.getVersion()),
                        "查看 Comandante 版本号"
                );
        CommandLauncher.register().builder()
                .main("comandante")
                .opt("author", "a")
                .executor(
                        context -> context.putOutputData("comandante_author", CommandConfig.getAuthor()),
                        "查看 Comandante 作者"
                );
        CommandLauncher.register().builder()
                .main("comandante")
                .opt("doc", "d")
                .executor(
                        context -> context.putOutputData("comandante_doc", CommandConfig.getDoc()),
                        "查看 Comandante 文档"
                );
        CommandLauncher.register().builder()
                .main("comandante")
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
