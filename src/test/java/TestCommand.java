import com.riicarus.comandante.argument.StringCommandArgumentType;
import com.riicarus.comandante.exception.CommandExecutionException;
import com.riicarus.comandante.main.Logger;
import com.riicarus.comandante.manage.CommandBuilder;
import com.riicarus.comandante.manage.CommandDispatcher;
import com.riicarus.comandante.manage.CommandRegister;
import com.riicarus.comandante.tree.RootNode;

/**
 * [FEATURE INFO]<br/>
 * 测试指令
 *
 * @author Skyline
 * @create 2022-11-16 23:31
 * @since 1.0.0
 */
public class TestCommand {

    private static final CommandRegister COMMAND_REGISTER = new CommandRegister();
    private static final RootNode ROOT_NODE = COMMAND_REGISTER.getRootNode();

    public static void main(String[] args) throws CommandExecutionException {
        buildCommand();
        dispatchCommand();
    }

    public static void buildCommand() {

        new CommandBuilder(ROOT_NODE).exe("app")
                .opt("color", "c").arg("color", new StringCommandArgumentType())
                .executor(
                        context -> Logger.log("set app color to " + context.getData().get("color"))
                );
        new CommandBuilder(ROOT_NODE).exe("app")
                .opt("font", "f")
                .arg("font_main", new StringCommandArgumentType())
                .arg("font_next", new StringCommandArgumentType())
                .executor(
                        context -> Logger.log("set app font to " + context.getData().get("font_main") + "/" + context.getData().get("font_next"))
                );
        new CommandBuilder(ROOT_NODE).exe("app").exe("echo").exe("time")
                .arg("hello", new StringCommandArgumentType())
                .executor(
                        context -> Logger.log("app echo: hello, time now is " + System.currentTimeMillis()
                                + ". The font is " + context.getData().get("font_main") + "/" + context.getData().get("font_next")
                                + ". The color is " + context.getData().get("color") + ".")
                );
    }

    public static void dispatchCommand() throws CommandExecutionException {
        CommandDispatcher commandDispatcher = new CommandDispatcher(COMMAND_REGISTER);
        commandDispatcher.dispatch("app --color 'red' --font 'Soft' 'Hard' echo time hello");
        commandDispatcher.dispatch("app --color 'red' --font 'Soft' 'Hard'");
    }

}
