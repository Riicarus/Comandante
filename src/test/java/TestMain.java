import com.riicarus.comandante.main.CommandLauncher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * [FEATURE INFO]<br/>
 *
 * @author Riicarus
 * @create 2022-10-16 2:06
 * @since 1.0
 */
public class TestMain {

    public static void main(String[] args) throws FileNotFoundException {
        DemoCommand.defineCommand();

        // testRedirectOutput();

        CommandLauncher.setLogFile("D:\\tmp\\comandante-log.txt");
        CommandLauncher.enable();
        Thread thread = new Thread(new ConsoleIOListener());
        thread.start();

        // testMultiThread();
    }

    private static void testMultiThread() {
        AtomicInteger integer = new AtomicInteger(0);

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Thread thread_ = new Thread(() -> CommandLauncher.dispatchToCache("app --color 'red' --font 'Soft' " + integer.incrementAndGet()));

            threads.add(thread_);
        }

        threads.forEach(Thread::start);
    }

    private static void testRedirectOutput() throws FileNotFoundException {
        File file = new File("D:\\tmp\\comandante-output.txt");

        CommandLauncher.redirectOutput(new FileOutputStream(file));
    }

}
