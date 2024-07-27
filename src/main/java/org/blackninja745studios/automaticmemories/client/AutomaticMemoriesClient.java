package org.blackninja745studios.automaticmemories.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.blackninja745studios.automaticmemories.client.config.Configuration;

public class AutomaticMemoriesClient implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger(AutomaticMemoriesClient.class);

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

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            Configuration.LEFTOVER_INTERVAL_MS = Configuration.INTERVAL_MS - PeriodicTimerSingleton.timeSinceLastScreenshot();
            Configuration.saveToFile(Configuration.CONFIG_PATH);
        });
    }
}
