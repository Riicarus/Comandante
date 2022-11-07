import com.skyline.command.SkyCommand;
import com.skyline.command.argument.IntegerCommandArgumentType;
import com.skyline.command.argument.ListCommandArgumentType;
import com.skyline.command.argument.StringCommandArgumentType;

/**
 * [FEATURE INFO]<br/>
 *
 * @author Skyline
 * @create 2022-10-15 15:52
 * @since 1.0.0
 */
public class DemoCommand {

    private final SkyCommand skyCommand;

    public DemoCommand(SkyCommand skyCommand) {
        this.skyCommand = skyCommand;
    }

    public void defineCommand() {
        skyCommand.register().execution("plugin").action("load")
                .option("dir", "d").argument("dir", new StringCommandArgumentType()).executor(
                (args) -> System.out.println("load plugin from dir: " + args[0]),
                "从文件夹加载插件"
        );

        skyCommand.register().execution("plugin").action("unload")
                .option("dir", "d").argument("dir", new StringCommandArgumentType()).executor(
                (args) -> System.out.println("unload plugin from dir: " + args[0])
        );

        skyCommand.register().execution("plugin").action("list")
                .option("info", "i")
                .option("all", "a")
                .option("dir", "d").argument("dir", new StringCommandArgumentType()).executor(
                (args) -> System.out.println("list all plugin info of dir: " + args[0])
        );

        skyCommand.register().execution("plugin").action("move")
                .option("from", "f").argument("from", new StringCommandArgumentType())
                .option("to", "t").argument("to", new StringCommandArgumentType())
                .executor(
                (args) -> System.out.println("move plugin from dir: " + args[0] + " to dir: " + args[1])
        );

        skyCommand.register().execution("plugin").action("unload")
                .option("id", "i").argument("id", new IntegerCommandArgumentType())
                .executor(
                        (args) -> System.out.println("unload plugin of id: " + args[0])
                );

        skyCommand.register().execution("plugin").action("munload")
                .option("ids", "i").argument("ids", new ListCommandArgumentType())
                .executor(
                        args -> System.out.println("multi-unload plugin of ids: " + args[0].toString())
                );
    }

}
