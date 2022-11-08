import com.skyline.command.main.CommandUtil;

import java.util.Scanner;

/**
 * [FEATURE INFO]<br/>
 *
 * @author Skyline
 * @create 2022-11-8 15:49
 * @since 1.0
 */
public class ConsoleIOListener implements Runnable {

    private final Scanner scanner = new Scanner(System.in);

    public String doGetCommand() {
        if (scanner.hasNext()) {
            return scanner.nextLine();
        }

        return null;
    }

    @Override
    public void run() {
        String str;
        while ((str = doGetCommand()) != null) {
            CommandUtil.dispatchToCache(str);
        }
    }
}
