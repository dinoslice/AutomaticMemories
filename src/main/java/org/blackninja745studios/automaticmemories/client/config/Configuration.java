package org.blackninja745studios.automaticmemories.client.config;

import org.blackninja745studios.automaticmemories.AutomaticMemories;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;


public class Configuration {
    public static long INTERVAL_MS = 3600 * 1000;
    public static long LEFTOVER_INTERVAL_MS = 0;
    public static boolean RESTART_TIMER_EACH_SESSION = false;

    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("automaticmemories.properties");

    public static void loadFromFile(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            Properties properties = new Properties(1);
            properties.load(reader);

            INTERVAL_MS = Math.max(0, Long.parseLong(properties.getProperty("interval_ms", String.valueOf(INTERVAL_MS))));

            long leftoverIntervalMs = Long.parseLong(properties.getProperty("leftover_interval_ms", String.valueOf(LEFTOVER_INTERVAL_MS)));
            LEFTOVER_INTERVAL_MS = Math.min(Math.max(0, leftoverIntervalMs), INTERVAL_MS);

            RESTART_TIMER_EACH_SESSION = Boolean.parseBoolean(properties.getProperty("restart_timer_each_session", String.valueOf(RESTART_TIMER_EACH_SESSION)));
        } catch (Exception ignored) {}
    }

    public static void saveToFile(Path path) {
        Properties properties = new Properties(1);

        properties.put("interval_ms", String.valueOf(INTERVAL_MS));
        properties.put("leftover_interval_ms", String.valueOf(LEFTOVER_INTERVAL_MS));
        properties.put("restart_timer_each_session", String.valueOf(RESTART_TIMER_EACH_SESSION));

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            properties.store(writer, "AutomaticMemories config");
        } catch (IOException exception) {
            LogManager.getLogger(AutomaticMemories.class).error(exception.getMessage(), exception);
        }
    }

}
