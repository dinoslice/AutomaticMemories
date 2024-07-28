package org.blackninja745studios.automaticmemories.client.config;

import org.blackninja745studios.automaticmemories.client.AutomaticMemories;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;


public class Configuration {
    public static long INTERVAL_MS = 3600 * 1000;
    public static long LEFTOVER_INTERVAL_MS = 0;
    public static boolean RESTART_TIMER_EACH_SESSION = false;
    public static boolean REQUIRE_IN_WORLD = true;

    public static String SAVE_DIRECTORY = "screenshots";
    public static String SCREENSHOT_PREFIX = "auto_";

    public static boolean NOTIFY_PLAYER = false;

    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("automaticmemories.properties");


    public static void loadFromFile(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            Properties properties = new Properties(1);
            properties.load(reader);

            // auto screenshot interval
            INTERVAL_MS = Math.max(0, Long.parseLong(properties.getProperty("interval_ms", String.valueOf(INTERVAL_MS))));

            long leftoverIntervalMs = Long.parseLong(properties.getProperty("leftover_interval_ms", String.valueOf(LEFTOVER_INTERVAL_MS)));
            LEFTOVER_INTERVAL_MS = Math.min(Math.max(0, leftoverIntervalMs), INTERVAL_MS);

            RESTART_TIMER_EACH_SESSION = Boolean.parseBoolean(properties.getProperty("restart_timer_each_session", String.valueOf(RESTART_TIMER_EACH_SESSION)));
            REQUIRE_IN_WORLD = Boolean.parseBoolean(properties.getProperty("require_in_world", String.valueOf(REQUIRE_IN_WORLD)));

            SAVE_DIRECTORY = properties.getProperty("save_directory", SAVE_DIRECTORY);
            SCREENSHOT_PREFIX = properties.getProperty("screenshot_prefix", SCREENSHOT_PREFIX);

            NOTIFY_PLAYER = Boolean.parseBoolean(properties.getProperty("notify_player", String.valueOf(NOTIFY_PLAYER)));
        } catch (Exception ignored) {
            saveToFile(path);
        }
    }

    public static void saveToFile(Path path) {
        Properties properties = new Properties(1);

        properties.put("interval_ms", String.valueOf(INTERVAL_MS));
        properties.put("leftover_interval_ms", String.valueOf(LEFTOVER_INTERVAL_MS));
        properties.put("restart_timer_each_session", String.valueOf(RESTART_TIMER_EACH_SESSION));
        properties.put("require_in_world", String.valueOf(REQUIRE_IN_WORLD));

        properties.put("save_directory", SAVE_DIRECTORY);
        properties.put("screenshot_prefix", SCREENSHOT_PREFIX);

        properties.put("notify_player", String.valueOf(NOTIFY_PLAYER));

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            properties.store(writer, "AutomaticMemories config");
        } catch (IOException exception) {
            AutomaticMemories.LOGGER.error(exception.getMessage(), exception);
        }
    }

    public static File getFullDirectory(File runDirectory, String saveDirectory) {
        File saveDir = new File(saveDirectory);

        if (!saveDir.isAbsolute()) {
            saveDir = new File(runDirectory, saveDirectory);
        }

        return saveDir;
    }
}
