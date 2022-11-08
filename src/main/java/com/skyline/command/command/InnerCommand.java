package com.skyline.command.command;

import com.skyline.command.CommandUtil;
import com.skyline.command.config.Config;
import com.skyline.command.Logger;

import java.util.Set;

/**
 * [FEATURE INFO]<br/>
 * 内置指令
 *
 * @author Skyline
 * @create 2022-10-17 17:57
 * @since 1.0.0
 */
public class InnerCommand extends BaseCommand {

    public static void defineCommand() {
        CommandUtil.register().execution("command")
                .option("version", "v")
                .executor(
                        (args) -> Logger.log(Config.getVersion()),
                        "查看 SkyCommand 版本号"
        );
        CommandUtil.register().execution("command")
                .option("author", "a")
                .executor(
                        (args) -> Logger.log(Config.getAuthor()),
                        "查看 SkyCommand 作者"
        );
        CommandUtil.register().execution("command")
                .option("doc", "d")
                .executor(
                        (args) -> Logger.log(Config.getDoc()),
                        "查看 SkyCommand 文档"
        );
        CommandUtil.register().execution("command")
                .option("info", "i")
                .executor(
                        (args) -> {
                            Logger.log(Config.getVersion());
                            Logger.log(Config.getAuthor());
                            Logger.log(Config.getDoc());
                        },
                        "查看 SkyCommand 信息"
                );
        CommandUtil.register().execution("command").action("list")
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
