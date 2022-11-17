import com.riicarus.comandante.argument.StringCommandArgumentType;
import com.riicarus.comandante.definition.BaseCommand;
import com.riicarus.comandante.main.CommandUtil;
import com.riicarus.comandante.main.Logger;
import com.riicarus.comandante.manage.CommandDispatcher;

import java.util.HashMap;

/**
 * [FEATURE INFO]<br/>
 *
 * @author Skyline
 * @create 2022-10-15 15:52
 * @since 1.0
 */
public class DemoCommand extends BaseCommand {

    @SuppressWarnings("unchecked")
    public static void defineCommand() {
        CommandUtil.register()
                .exe("app")
                .opt("color", "c")
                .arg("color", new StringCommandArgumentType())
                .executor(
                        context -> Logger.log("set app color to "
                                + ((HashMap<String, String>) context.getData("color")).get("color"))
                );
        CommandUtil.register()
                .exe("app")
                .opt("font", "f")
                .arg("font_main", new StringCommandArgumentType())
                .arg("font_next", new StringCommandArgumentType())
                .executor(
                        context -> Logger.log("set app font to "
                                + ((HashMap<String, String>) context.getData("font")).get("font_main")
                                + "/"
                                + ((HashMap<String, String>) context.getData("font")).get("font_next"))
                );
        CommandUtil.register()
                .exe("app")
                .exe("echo")
                .exe("time")
                .arg("hello", new StringCommandArgumentType())
                .executor(
                        context -> Logger.log("app echo: "
                                + context.getData("time" + CommandDispatcher.EXE_ARG_DATA_SEPARATOR + "hello")
                                + "\n"
                                + "time now is "
                                + System.currentTimeMillis())
                );
    }

}
