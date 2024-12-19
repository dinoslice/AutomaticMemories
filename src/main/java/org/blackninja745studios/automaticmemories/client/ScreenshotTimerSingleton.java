package org.blackninja745studios.automaticmemories.client;

import net.minecraft.client.MinecraftClient;
import org.blackninja745studios.automaticmemories.client.config.Configuration;

import java.time.Duration;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;

// TODO: is this the best way to do this?
public class ScreenshotTimerSingleton {
    private static Timer timer;

    // TODO: eventually change this to the next scheduled time?
    private static Instant lastScreenshotTime = Instant.now();

    public static void restartOrStartTimer(long delayBeforeFirst, long intervalMs) {
        cancelTimer();
        timer = new Timer();
        lastScreenshotTime = Instant.now().minusMillis(intervalMs - delayBeforeFirst);

        timer.schedule(new TimerTask() {
            private final MinecraftClient client = MinecraftClient.getInstance();

            @Override
            public void run() {
                if (client != null && client.getFramebuffer() != null && Configuration.ENABLED)
                    takeScreenshot(client);

                lastScreenshotTime = Instant.now();
            }
        }, delayBeforeFirst, intervalMs);
    }

    public static void cancelTimer() {
        if (timer != null)
            timer.cancel();
    }

    public static long timeSinceLastScreenshot() {
        return Duration.between(lastScreenshotTime, Instant.now()).toMillis();
    }

    private static void takeScreenshot(MinecraftClient client) {
        boolean worldReq = !Configuration.REQUIRE_IN_WORLD || client.world != null;

        boolean unpausedReq = !Configuration.REQUIRE_UNPAUSED || client.world == null || !client.isPaused();

        if (worldReq && unpausedReq) {
            ScreenshotRecorderExt.saveScreenshot(
                    Configuration.getFullDirectory(client.runDirectory, Configuration.SAVE_DIRECTORY),
                    Configuration.SCREENSHOT_PREFIX,
                    client.getFramebuffer(),
                    msg -> client.execute(() -> {
                        if (Configuration.NOTIFY_PLAYER && client.inGameHud != null && client.world != null)
                            client.inGameHud.getChatHud().addMessage(msg);
                    })
            );
        }
    }

    public static String formatTime(long millis) {
        final long MILLIS_PER_SECOND = 1000;
        final long MILLIS_PER_MINUTE = MILLIS_PER_SECOND * 60;
        final long MILLIS_PER_HOUR = MILLIS_PER_MINUTE * 60;
        final long MILLIS_PER_DAY = MILLIS_PER_HOUR * 24;

        long day = millis / MILLIS_PER_DAY;
        long hour = millis / MILLIS_PER_HOUR % 24;
        long min = millis / MILLIS_PER_MINUTE % 60;
        long sec = millis / MILLIS_PER_SECOND % 60;

        StringBuilder s = new StringBuilder();

        if (day != 0) {
            s.append(day);
            s.append('d');
        }
        if (hour != 0) {
            s.append(hour);
            s.append('h');
        }
        s.append(String.format("%02dm%02ds", min, sec));

        return s.toString();
    }
}
