import com.riicarus.comandante.main.CommandLauncher;

/**
 * [FEATURE INFO]<br/>
 *
 * @author Riicarus
 * @create 2022-10-15 15:52
 * @since 1.0
 */
public class DemoCommand {

    public static void defineCommand() {
        CommandLauncher.register().builder()
                .main("app")
                .main("echo")
                .arg("message")
                .executor(
                        context -> System.out.println("app echos")
                );
        CommandLauncher.register().builder()
                .main("app")
                .main("echo")
                .arg("message")
                .opt("color", "c")
                .arg("color_name")
                .executor(
                        context -> System.out.println("app echos message with color")
                );
        CommandLauncher.register().builder()
                .main("grep")
                .arg("value")
                .executor(
                        context -> System.out.println("grep ...")
                );
    }

}
