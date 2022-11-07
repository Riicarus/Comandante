package com.skyline.command.command;

import com.skyline.command.SkyCommand;
import com.skyline.command.config.Config;

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

    public InnerCommand(SkyCommand SKY_COMMAND) {
        super(SKY_COMMAND);
    }

    public void defineCommand() {
        SKY_COMMAND.register().execution("command")
                .option("version", "v")
                .executor(
                        (args) -> System.out.println(Config.getVersion()),
                        "查看 SkyCommand 版本号"
        );
        SKY_COMMAND.register().execution("command")
                .option("author", "a")
                .executor(
                        (args) -> System.out.println(Config.getAuthor()),
                        "查看 SkyCommand 作者"
        );
        SKY_COMMAND.register().execution("command")
                .option("doc", "d")
                .executor(
                        (args) -> System.out.println(Config.getDoc()),
                        "查看 SkyCommand 文档"
        );
        SKY_COMMAND.register().execution("command")
                .option("info", "i")
                .executor(
                        (args) -> {
                            System.out.println(Config.getVersion());
                            System.out.println(Config.getAuthor());
                            System.out.println(Config.getDoc());
                        },
                        "查看 SkyCommand 信息"
                );
        SKY_COMMAND.register().execution("command").action("list")
                .option("all", "a")
                .executor(
                        args -> {
                            Set<String> commandSet = SKY_COMMAND.listAllExecutionCommand();
                            commandSet.forEach(System.out::println);
                        },
                        "列出所有已注册指令"
                );
    }
}
