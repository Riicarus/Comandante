package com.riicarus.comandante.manage;

import com.riicarus.util.asserts.Asserts;
import com.riicarus.util.exception.EmptyStringException;
import com.riicarus.util.exception.NullObjectException;

import java.util.HashMap;

/**
 * [FEATURE INFO]<br/>
 * Command context is used in command execution, it delivers intermediate data during the list of analyzed executors execute.<br/>
 *
 * @author Riicarus
 * @create 2022-11-16 17:43
 * @since 1.0.0
 */
public class CommandContext {

    public static final String INNER_DATA_PREFIX = "INNER_";
    public static final String ARG_DATA_PREFIX = "ARG_";

    /**
     * The cached intermediate data during command execution.<br/>
     * Specially, for the inner commands' data, the keys have a particular prefix: "INNER_".<br/>
     * For the command arguments, the keys have a particular prefix: "ARG_".
     */
    private final HashMap<String, Object> data = new HashMap<>();

    public void put(String key, Object value) throws NullObjectException, EmptyStringException {
        Asserts.notEmpty(key, "Key of data in CommandContext can not be null or empty.");
        Asserts.notNull(value, "Value of data in CommandContext can not be null.");

        getData().put(key, value);
    }

    public Object get(String key) {
        return getData().get(key);
    }

    public HashMap<String, Object> getData() {
        return data;
    }
}
