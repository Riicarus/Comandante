package com.skyline.command.manage;

import java.util.Scanner;

/**
 * [FEATURE INFO]<br/>
 * IO 设置
 *
 * @author Skyline
 * @create 2022-10-15 16:31
 * @since 1.0.0
 */
public class IOHandler {

    private final Scanner scanner = new Scanner(System.in);

    public String doGetCommand() {
        if (scanner.hasNext()) {
            return scanner.nextLine();
        }

        return null;
    }

}
