package com.skyline.command.manage;

import java.util.Scanner;

/**
 * [FEATURE INFO]<br/>
 * 控制台IO
 *
 * @author Skyline
 * @create 2022-10-19 10:21
 * @since 1.0.0
 */
public class ConsoleIOHandler implements IOHandler {

    private final Scanner scanner = new Scanner(System.in);

    public String doGetCommand() {
        if (scanner.hasNext()) {
            return scanner.nextLine();
        }

        return null;
    }

    @Override
    public void redirectOutput() {}

}
