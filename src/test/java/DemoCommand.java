import com.riicarus.comandante.argument.StringCommandArgumentType;
import com.riicarus.comandante.main.CommandLauncher;
import com.riicarus.comandante.main.CommandLogger;
import com.riicarus.comandante.manage.CommandDispatcher;

import java.util.HashMap;

/**
 * [FEATURE INFO]<br/>
 *
 * @author Skyline
 * @create 2022-10-15 15:52
 * @since 1.0
 */
public class DemoCommand {

    @SuppressWarnings("unchecked")
    public static void defineCommand() {
        CommandLauncher.register()
                .exe("app")
                .opt("color", "c")
                .arg("color_name", new StringCommandArgumentType())
                .executor(
                        context -> CommandLogger.log("set app color to "
                                + ((HashMap<String, String>) context.getData("color")).get("color_name"))
                );
        CommandLauncher.register()
                .exe("app")
                .opt("font", "f")
                .arg("font_main", new StringCommandArgumentType())
                .arg("font_next", new StringCommandArgumentType())
                .executor(
                        context -> CommandLogger.log("set app font to "
                                + ((HashMap<String, String>) context.getData("font")).get("font_main")
                                + "/"
                                + ((HashMap<String, String>) context.getData("font")).get("font_next"))
                );
        CommandLauncher.register()
                .exe("app")
                .exe("echo")
                .exe("time")
                .arg("hello", new StringCommandArgumentType())
                .executor(
                        context -> CommandLogger.log("app echo: "
                                + context.getData("time" + CommandDispatcher.EXE_ARG_DATA_SEPARATOR + "hello")
                                + "\n"
                                + "time now is "
                                + System.currentTimeMillis())
                );
        CommandLauncher.register()
                .exe("app")
                .exe("echo")
                .arg("message", new StringCommandArgumentType())
                .executor(
                        context -> CommandLogger.log("app echo: " + context.getData("echo" + CommandDispatcher.EXE_ARG_DATA_SEPARATOR + "message"))
                );
        CommandLauncher.register()
                .exe("app")
                .arg("name", new StringCommandArgumentType())
                .executor(
                        context -> CommandLogger.log("app name: " + context.getData("app" + CommandDispatcher.EXE_ARG_DATA_SEPARATOR + "name"))
                );
        CommandLauncher.register()
                .exe("app")
                .exe("echo")
                .exe("move")
                .arg("from", new StringCommandArgumentType())
                .arg("to", new StringCommandArgumentType())
                .executor(
                        context -> CommandLogger.log("app echo: move from " +
                                context.getData("move" + CommandDispatcher.EXE_ARG_DATA_SEPARATOR + "from")
                        + " to "
                        + context.getData("from" + CommandDispatcher.EXE_ARG_DATA_SEPARATOR + "to"))
                );
    }

}
