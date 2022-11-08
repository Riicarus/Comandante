import com.skyline.command.CommandUtil;

/**
 * [FEATURE INFO]<br/>
 *
 * @author Skyline
 * @create 2022-10-16 2:06
 * @since 1.0.0
 */
public class TestMain {

    public static void main(String[] args) {
        DemoCommand.defineCommand();
        CommandUtil.enable();
        Thread thread = new Thread(new ConsoleIOListener());
        thread.start();
    }

}
