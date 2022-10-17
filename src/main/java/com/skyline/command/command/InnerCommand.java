package com.skyline.command.command;

import com.skyline.command.SkyCommand;
import com.skyline.command.config.Config;

/**
 * [FEATURE INFO]<br/>
 * 内置指令
 *
 * @author Skyline
 * @create 2022-10-17 17:57
 * @since 1.0.0
 */
public class InnerCommand {

    private final SkyCommand SKY_COMMAND;

    public InnerCommand(SkyCommand SKY_COMMAND) {
        this.SKY_COMMAND = SKY_COMMAND;
    }

    public void defineCommand() {
        SKY_COMMAND.register().execution("command").option("version", "v").executor(
                (args) -> System.out.println(Config.getVersion())
        );
        SKY_COMMAND.register().execution("command").option("author", "a").executor(
                (args) -> System.out.println(Config.getAuthor())
        );
        SKY_COMMAND.register().execution("command").option("doc", "d").executor(
                (args) -> System.out.println(Config.getDoc())
        );
        SKY_COMMAND.register().execution("command").option("info", "i").executor(
                (args) -> {
                    System.out.println(Config.getVersion());
                    System.out.println(Config.getAuthor());
                    System.out.println(Config.getDoc());
                }
        );
    }
}
