import com.riicarus.comandante.argument.StringCommandArgumentType;
import com.riicarus.comandante.main.CommandLauncher;
import com.riicarus.comandante.manage.CommandDispatcher;

import java.util.HashMap;

/**
 * [FEATURE INFO]<br/>
 *
 * @author Riicarus
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
                        context -> context.putOutputData("set_color",
                                "set app color to "
                                + ((HashMap<String, String>) context.getArgument("color")).get("color_name"))
                );
        CommandLauncher.register()
                .exe("app")
                .opt("font", "f")
                .arg("font_main", new StringCommandArgumentType())
                .arg("font_next", new StringCommandArgumentType())
                .executor(
                        context -> context.putOutputData("set_font",
                                "set app font to "
                                + ((HashMap<String, String>) context.getArgument("font")).get("font_main")
                                + "/"
                                + ((HashMap<String, String>) context.getArgument("font")).get("font_next"))
                );
        CommandLauncher.register()
                .exe("app")
                .exe("echo")
                .exe("time")
                .arg("hello", new StringCommandArgumentType())
                .executor(
                        context -> context.putOutputData("echo_time",
                                "app echo: "
                                + context.getArgument("time" + CommandDispatcher.EXE_ARG_DATA_SEPARATOR + "hello")
                                + "\n"
                                + "time now is "
                                + System.currentTimeMillis())
                );
        CommandLauncher.register()
                .exe("app")
                .exe("echo")
                .arg("message", new StringCommandArgumentType())
                .executor(
                        context -> context.putOutputData("echo_message",
                                "app echo: " + context.getArgument("echo" + CommandDispatcher.EXE_ARG_DATA_SEPARATOR + "message"))
                );
        CommandLauncher.register()
                .exe("app")
                .arg("name", new StringCommandArgumentType())
                .executor(
                        context -> context.putOutputData("app_name",
                                "app name: " + context.getArgument("app" + CommandDispatcher.EXE_ARG_DATA_SEPARATOR + "name"))
                );
        CommandLauncher.register()
                .exe("app")
                .exe("echo")
                .exe("move")
                .arg("from", new StringCommandArgumentType())
                .arg("to", new StringCommandArgumentType())
                .executor(
                        context -> context.putOutputData("echo_move",
                                "app echo: move from "
                                + context.getArgument("move" + CommandDispatcher.EXE_ARG_DATA_SEPARATOR + "from")
                                + " to "
                                + context.getArgument("from" + CommandDispatcher.EXE_ARG_DATA_SEPARATOR + "to"))
                );
        CommandLauncher.register()
                .exe("app")
                .exe("register")
                .exe("put")
                .arg("element", new StringCommandArgumentType())
                .executor(
                        context -> {
                            String element = (String) context.getArgument("put" + CommandDispatcher.EXE_ARG_DATA_SEPARATOR + "element");
                            context.putOutputData("register_put",
                                    "app put element to register. Element=" + element);
                            context.putCacheData("register_element", element);
                        }
                );
    }

}
