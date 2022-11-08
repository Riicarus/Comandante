import com.skyline.command.CommandUtil;
import com.skyline.command.argument.IntegerCommandArgumentType;
import com.skyline.command.argument.ListCommandArgumentType;
import com.skyline.command.argument.StringCommandArgumentType;
import com.skyline.command.command.BaseCommand;
import com.skyline.command.Logger;

/**
 * [FEATURE INFO]<br/>
 *
 * @author Skyline
 * @create 2022-10-15 15:52
 * @since 1.0.0
 */
public class DemoCommand extends BaseCommand {

    public static void defineCommand() {
        CommandUtil.register().execution("plugin").action("load")
                .option("dir", "d").argument("dir", new StringCommandArgumentType()).executor(
                (args) -> Logger.log("load plugin from dir: " + args[0]),
                "从文件夹加载插件"
        );

        CommandUtil.register().execution("plugin").action("unload")
                .option("dir", "d").argument("dir", new StringCommandArgumentType()).executor(
                (args) -> Logger.log("unload plugin from dir: " + args[0])
        );

        CommandUtil.register().execution("plugin").action("list")
                .option("info", "i")
                .option("all", "a")
                .option("dir", "d").argument("dir", new StringCommandArgumentType()).executor(
                (args) -> Logger.log("list all plugin info of dir: " + args[0])
        );

        CommandUtil.register().execution("plugin").action("move")
                .option("from", "f").argument("from", new StringCommandArgumentType())
                .option("to", "t").argument("to", new StringCommandArgumentType())
                .executor(
                (args) -> Logger.log("move plugin from dir: " + args[0] + " to dir: " + args[1])
        );

        CommandUtil.register().execution("plugin").action("unload")
                .option("id", "i").argument("id", new IntegerCommandArgumentType())
                .executor(
                        (args) -> Logger.log("unload plugin of id: " + args[0])
                );

        CommandUtil.register().execution("plugin").action("munload")
                .option("ids", "i").argument("ids", new ListCommandArgumentType())
                .executor(
                        args -> Logger.log("multi-unload plugin of ids: " + args[0].toString())
                );
    }

}
