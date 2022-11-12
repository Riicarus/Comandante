package com.skyline.comandante.argument;

import com.skyline.comandante.exception.CommandSyntaxException;

import java.util.ArrayList;
import java.util.List;

/**
 * [FEATURE INFO]<br/>
 * List 类型参数
 *
 * @author Skyline
 * @create 2022-11-5 13:44
 * @since 1.0
 */
public class ListCommandArgumentType extends CommandArgumentType<List<String>> {

    private static final String ELEMENT_SPLIT_STRING = ",";

    @Override
    public List<String> parse(String arg) throws CommandSyntaxException {
        List<String> list = new ArrayList<>();

        String[] elements = arg.split(ELEMENT_SPLIT_STRING);
        for (String element : elements) {
            list.add(element.trim());
        }

        return list;
    }
}
