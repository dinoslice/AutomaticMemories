package org.blackninja745studios.automaticmemories.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ScreenshotRecorder;
import org.apache.logging.log4j.LogManager;
import org.blackninja745studios.automaticmemories.AutomaticMemories;

import java.time.Duration;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;

// TODO: is this the best way to do this?
public class PeriodicTimerSingleton {
    private static Timer periodicTimer;
    private static Instant lastScreenshotTime = Instant.now();

    public static void restartOrStartTimer(long delayBeforeFirst, long intervalMs) {
        LogManager.getLogger(AutomaticMemories.class).warn("starting new timer");

        cancelTimer();

        periodicTimer = new Timer();
        periodicTimer.schedule(new TimerTask() {
            private final MinecraftClient client = MinecraftClient.getInstance();

            @Override
            public void run() {
                if (client.player != null) {
                    LogManager.getLogger(AutomaticMemories.class).warn("took screenshot");
                    takeScreenshot(client);
                }
                lastScreenshotTime = Instant.now();
            }
        }, delayBeforeFirst, intervalMs);

        lastScreenshotTime = Instant.now();
    }

    public static void cancelTimer() {
        if (periodicTimer != null)
            periodicTimer.cancel();
    }

    public static long remainingMsBeforeScreenshot() {
        return Duration.between(lastScreenshotTime, Instant.now()).toMillis();
    }

    private static void takeScreenshot(MinecraftClient client) {
        ScreenshotRecorder.saveScreenshot(
                client.runDirectory,
                client.getFramebuffer(),
                message -> client.execute(
                        () -> client.inGameHud.getChatHud().addMessage(message)
                )
        );
    }
}
