package org.blackninja745studios.automaticmemories.client;

import net.fabricmc.api.ClientModInitializer;
import org.blackninja745studios.automaticmemories.client.config.Configuration;

public class AutomaticMemoriesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Configuration.loadFromFile(Configuration.CONFIG_PATH);
        PeriodicTimerSingleton.restartOrStartTimer(0, Configuration.INTERVAL_MS);
    }
}
