package com.skyline.command.config;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

/**
 * [FEATURE INFO]<br/>
 * 一些配置
 *
 * @author Skyline
 * @create 2022-10-17 17:46
 * @since 1.0.0
 */
public class Config {

    protected final static String CONFIG_PATH = "config.properties";

    private static String version;

    private static String author;

    private static String doc;

    public static void loadConfig() {
        Properties properties = new Properties();

        InputStream in;
        InputStreamReader reader;
        try {
            in = Config.class.getClassLoader().getResourceAsStream(CONFIG_PATH);
            reader = new InputStreamReader(Objects.requireNonNull(in), StandardCharsets.UTF_8);
            properties.load(reader);
        } catch (Exception e) {
            throw new RuntimeException("从路径: " + CONFIG_PATH + " 加载配置文件失败.");
        }

        Config.version = properties.getProperty("version");
        Config.author = properties.getProperty("author");
        Config.doc = properties.getProperty("doc");
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
