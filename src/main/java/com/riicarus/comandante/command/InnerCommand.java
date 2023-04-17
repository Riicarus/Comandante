package com.riicarus.comandante.command;

import com.riicarus.comandante.config.CommandConfig;
import com.riicarus.comandante.main.CommandLauncher;

/**
 * [FEATURE INFO]<br/>
 * Inner command.
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
                        (args, pipedArgs) -> CommandConfig.getVersion(),
                        "Get Comandante's version"
                );
        CommandLauncher.register().builder()
                .main("comandante")
                .opt("author", "a")
                .executor(
                        (args, pipedArgs) -> CommandConfig.getAuthor(),
                        "Get Comandante's author"
                );
        CommandLauncher.register().builder()
                .main("comandante")
                .opt("doc", "d")
                .executor(
                        (args, pipedArgs) -> CommandConfig.getDoc(),
                        "Get Comandante's document link"
                );
        CommandLauncher.register().builder()
                .main("comandante")
                .opt("info", "i")
                .executor(
                        (args, pipedArgs) -> "version=" + CommandConfig.getVersion() + "\n" +
                                "author=" + CommandConfig.getAuthor() + "\n" +
                                "doc_link=" + CommandConfig.getDoc(),
                        "Get Comandante's information"
                );
        CommandLauncher.register().builder()
                .main("comandante")
                .opt("list", "l")
                .executor(
                        (args, pipedArgs) -> CommandLauncher.register().getCommandItemManager().listAllCommandUsage(),
                        "List all registered command's and their usages"
                );
    }
}
