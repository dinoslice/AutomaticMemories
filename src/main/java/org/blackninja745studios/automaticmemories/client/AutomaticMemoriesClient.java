package org.blackninja745studios.automaticmemories.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import org.apache.logging.log4j.LogManager;
import org.blackninja745studios.automaticmemories.AutomaticMemories;
import org.blackninja745studios.automaticmemories.client.config.Configuration;

import java.io.File;
import java.io.FileWriter;

public class AutomaticMemoriesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Configuration.loadFromFile(Configuration.CONFIG_PATH);

        if (Configuration.RESTART_TIMER_EACH_SESSION) {
            PeriodicTimerSingleton.restartOrStartTimer(Configuration.INTERVAL_MS, Configuration.INTERVAL_MS);
        } else {
            PeriodicTimerSingleton.restartOrStartTimer(Configuration.LEFTOVER_INTERVAL_MS, Configuration.INTERVAL_MS);
            Configuration.LEFTOVER_INTERVAL_MS = 0;
            Configuration.saveToFile(Configuration.CONFIG_PATH);
        }

        File path = new File("memories");

        boolean _res = path.mkdirs();
        File f = new File(path, "file_test.txt");

        try (FileWriter fileWriter = new FileWriter(f)) {
            fileWriter.write("message w");
        } catch (Exception ignored) {}

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            Configuration.LEFTOVER_INTERVAL_MS = Configuration.INTERVAL_MS - PeriodicTimerSingleton.timeSinceLastScreenshot();
            Configuration.saveToFile(Configuration.CONFIG_PATH);
            LogManager.getLogger(AutomaticMemories.class).warn("saved!" + Configuration.LEFTOVER_INTERVAL_MS);
        });
    }
}
