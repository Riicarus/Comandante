import com.skyline.command.CommandUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * [FEATURE INFO]<br/>
 *
 * @author Skyline
 * @create 2022-10-16 2:06
 * @since 1.0.0
 */
public class TestMain {

    public static void main(String[] args){
        DemoCommand.defineCommand();
        File file = new File("D:\\tmp\\command-log.txt");

        // CommandUtil.redirectOutput(new FileOutputStream(file));
        CommandUtil.enable();
        Thread thread = new Thread(new ConsoleIOListener());
        thread.start();

        AtomicInteger integer = new AtomicInteger(0);

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Thread thread_ = new Thread(() -> CommandUtil.dispatchToCache("plugin load -d " + integer.incrementAndGet()));

            threads.add(thread_);
        }

        threads.forEach(Thread::start);

    }

}
