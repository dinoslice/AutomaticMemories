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

    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("automaticmemories.properties");

    static { loadFromFile(CONFIG_PATH); }

    public static void loadFromFile(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            Properties properties = new Properties(1);
            properties.load(reader);

            Configuration.INTERVAL_MS = Long.parseLong(properties.getProperty("interval_ms", String.valueOf(INTERVAL_MS)));

        } catch (Exception ignored) {
            saveToFile(path);
        }
    }

    public static void saveToFile(Path path) {
        Properties properties = new Properties(1);

        properties.put("interval_ms", String.valueOf(INTERVAL_MS));

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            properties.store(writer, "AutomaticMemories config");
        } catch (IOException exception) {
            LogManager.getLogger(AutomaticMemories.class).error(exception.getMessage(), exception);
        }
    }

}
