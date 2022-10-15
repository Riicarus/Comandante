import com.skyline.command.annotation.Action;
import com.skyline.command.annotation.Argument;
import com.skyline.command.annotation.Execution;
import com.skyline.command.annotation.Option;
import com.skyline.command.argument.StringCommandArgumentType;

/**
 * [FEATURE INFO]<br/>
 *
 * @author Skyline
 * @create 2022-10-15 15:52
 * @since 1.0.0
 */
@Execution(name = "plugin")
public class DemoCommand {

    @Action(
            name = "load",
            option = @Option(
                    name = "dir", alias = "d",
                    args = {
                            @Argument(name = "dir", type = StringCommandArgumentType.class)
                    }
            )
    )
    public void loadPluginFromDir(String dir) {
        System.out.println("load plugin from dir: " + dir);
    }

    @Action(
            name = "unload",
            option = @Option(
                    name = "dir", alias = "d",
                    args = {
                            @Argument(name = "dir", type = StringCommandArgumentType.class)
                    }
            )
    )
    public void unloadPluginOfDir(String dir) {
        System.out.println("unload plugin of dir: " + dir);
    }

}
