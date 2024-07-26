package org.blackninja745studios.automaticmemories.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ScreenshotRecorder;

import java.util.Timer;
import java.util.TimerTask;

public class AutomaticMemoriesClient implements ClientModInitializer {

    private MinecraftClient client;
    private Timer periodicTimer;

    @Override
    public void onInitializeClient() {
        periodicTimer = new Timer();
        client = MinecraftClient.getInstance();

        periodicTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (client.player != null)
                    takeScreenshot();
            }
        }, 0, 10000);
    }

    private void takeScreenshot() {
        ScreenshotRecorder.saveScreenshot(
                this.client.runDirectory,
                this.client.getFramebuffer(),
                message -> this.client.execute(
                        () -> this.client.inGameHud.getChatHud().addMessage(message)
                )
        );
    }
}
