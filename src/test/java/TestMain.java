import com.skyline.command.SkyCommand;
import com.skyline.command.manage.ConsoleIOHandler;

/**
 * [FEATURE INFO]<br/>
 *
 * @author Skyline
 * @create 2022-10-16 2:06
 * @since 1.0.0
 */
public class TestMain {

    public static void main(String[] args) {

        SkyCommand skyCommand = SkyCommand.getSkyCommand();
        skyCommand.startSkyCommand(new ConsoleIOHandler());
        new DemoCommand(skyCommand).defineCommand();

    }

}
