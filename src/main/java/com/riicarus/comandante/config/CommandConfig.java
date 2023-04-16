package com.riicarus.comandante.config;

import com.riicarus.comandante.main.CommandLogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

/**
 * [FEATURE INFO]<br/>
 * Comandante config
 *
 * @author Riicarus
 * @create 2022-10-17 17:46
 * @since 1.0
 */
public class CommandConfig {

    protected final static String CONFIG_PATH = "config.properties";
    /**
     * Version of Comandante.
     */
    private static String version;
    /**
     * Author of Comandante.
     */
    private static String author;
    /**
     * Document link of Comandante.
     */
    private static String doc;

    /**
     * Load properties from the given CONFIG_PATH.
     */
    public static void loadConfig() {
        Properties properties = new Properties();

        InputStream in = null;
        InputStreamReader reader = null;
        try {
            in = CommandConfig.class.getClassLoader().getResourceAsStream(CONFIG_PATH);
            reader = new InputStreamReader(Objects.requireNonNull(in), StandardCharsets.UTF_8);
            properties.load(reader);
        } catch (Exception e) {
            throw new RuntimeException("Load config file from path: " + CONFIG_PATH + " failed.");
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                CommandLogger.log("Close stream failed.");
            }
        }

        CommandConfig.version = properties.getProperty("version");
        CommandConfig.author = properties.getProperty("author");
        CommandConfig.doc = properties.getProperty("doc");
    }

    public static String getVersion() {
        return version;
    }

    public static String getAuthor() {
        return author;
    }

    public static String getDoc() {
        return doc;
    }
}
