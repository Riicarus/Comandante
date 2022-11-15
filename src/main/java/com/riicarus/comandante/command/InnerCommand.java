package com.riicarus.comandante.command;

import com.riicarus.comandante.definition.BaseCommand;
import com.riicarus.comandante.main.CommandUtil;
import com.riicarus.comandante.config.Config;
import com.riicarus.comandante.main.Logger;

import java.util.Set;

/**
 * [FEATURE INFO]<br/>
 * 内置指令
 *
 * @author Skyline
 * @create 2022-10-17 17:57
 * @since 1.0
 */
public class InnerCommand extends BaseCommand {

    public static void defineCommand() {
        CommandUtil.register().execution("comandante")
                .option("version", "v")
                .executor(
                        (args) -> Logger.log(Config.getVersion()),
                        "查看 Comandante 版本号"
        );
        CommandUtil.register().execution("comandante")
                .option("author", "a")
                .executor(
                        (args) -> Logger.log(Config.getAuthor()),
                        "查看 Comandante 作者"
        );
        CommandUtil.register().execution("comandante")
                .option("doc", "d")
                .executor(
                        (args) -> Logger.log(Config.getDoc()),
                        "查看 Comandante 文档"
        );
        CommandUtil.register().execution("comandante")
                .option("info", "i")
                .executor(
                        (args) -> {
                            Logger.log(Config.getVersion());
                            Logger.log(Config.getAuthor());
                            Logger.log(Config.getDoc());
                        },
                        "查看 Comandante 信息"
                );
        CommandUtil.register().execution("comandante").action("list")
                .option("all", "a")
                .executor(
                        args -> {
                            Set<String> commandSet = CommandUtil.listAllExecutionCommand();
                            commandSet.forEach(Logger::log);
                        },
                        "列出所有已注册指令"
                );
    }
}
