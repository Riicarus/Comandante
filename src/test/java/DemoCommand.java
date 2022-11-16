import com.riicarus.comandante.argument.StringCommandArgumentType;
import com.riicarus.comandante.definition.BaseCommand;
import com.riicarus.comandante.main.CommandUtil;
import com.riicarus.comandante.main.Logger;

/**
 * [FEATURE INFO]<br/>
 *
 * @author Skyline
 * @create 2022-10-15 15:52
 * @since 1.0
 */
public class DemoCommand extends BaseCommand {

    public static void defineCommand() {
        CommandUtil.register().exe("app")
                .opt("color", "c").arg("color", new StringCommandArgumentType())
                .executor(
                        context -> Logger.log("set app color to " + context.getData().get("color"))
                );
        CommandUtil.register().exe("app")
                .opt("font", "f")
                .arg("font_main", new StringCommandArgumentType())
                .arg("font_next", new StringCommandArgumentType())
                .executor(
                        context -> Logger.log("set app font to " + context.getData().get("font_main") + "/" + context.getData().get("font_next"))
                );
        CommandUtil.register().exe("app").exe("echo").exe("time")
                .arg("hello", new StringCommandArgumentType())
                .executor(
                        context -> Logger.log("app echo: hello, time now is " + System.currentTimeMillis()
                                + ". The font is " + context.getData().get("font_main") + "/" + context.getData().get("font_next")
                                + ". The color is " + context.getData().get("color") + ".")
                );
    }

}
