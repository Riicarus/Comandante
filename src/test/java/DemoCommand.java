import com.skyline.command.SkyCommand;
import com.skyline.command.argument.StringCommandArgumentType;

/**
 * [FEATURE INFO]<br/>
 *
 * @author Skyline
 * @create 2022-10-15 15:52
 * @since 1.0.0
 */
public class DemoCommand {

    public static final SkyCommand skyCommand = SkyCommand.startSkyCommand();

    public static void defineCommand() {
        skyCommand.register().execution("plugin").action("load").option("dir", "d").argument("dir", new StringCommandArgumentType()).executor(
                (args) -> System.out.println("load plugin from dir: " + args[0])
        );

        skyCommand.register().execution("plugin").action("unload").option("dir", "d").argument("dir", new StringCommandArgumentType()).executor(
                (args) -> System.out.println("unload plugin from dir: " + args[0])
        );

        skyCommand.register().execution("plugin").action("list").option("info", "i").option("all", "a").option("dir", "d").argument("dir", new StringCommandArgumentType()).executor(
                (args) -> System.out.println("list all plugin info of dir: " + args[0])
        );


        skyCommand.getCommandRegister().getRootCommandNode().getChildren().get("plugin").getChildren().get("list").getChildren().get("info").getChildren().get("all").getChildren().get("dir").getChildren().get("dir").getCommandExecutor().execute("D:\\tmp\\jars");

    }

}
