package com.riicarus.comandante.argument;

import com.riicarus.comandante.exception.CommandSyntaxException;
import com.riicarus.util.asserts.Asserts;

import java.util.ArrayList;
import java.util.List;

/**
 * [FEATURE INFO]<br/>
 * List 类型参数
 *
 * @author Riicarus
 * @create 2022-11-5 13:44
 * @since 1.0
 */
public class ListCommandArgumentType extends CommandArgumentType<List<String>> {

    private static final String ELEMENT_SPLIT_STRING = ",";

    public ListCommandArgumentType() {
        super("list<string>");
    }

    @Override
    public List<String> parse(String arg) throws CommandSyntaxException {
        Asserts.notNull(arg, new CommandSyntaxException("Arg can not be null."));

        List<String> list = new ArrayList<>();

        String[] elements = arg.split(ELEMENT_SPLIT_STRING);
        for (String element : elements) {
            list.add(element.trim());
        }

        return list;
    }
}
