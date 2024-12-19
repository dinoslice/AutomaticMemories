package org.blackninja745studios.automaticmemories.client

import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import org.blackninja745studios.automaticmemories.client.config.Configuration
import java.time.Duration
import java.time.Instant
import java.util.Timer
import java.util.TimerTask

object ScreenshotTimer {
    private lateinit var timer: Timer

    private var lastScreenshotTime: Instant = Instant.now()

    fun restartOrStartTimer(delayBeforeFirst: Long, intervalMs: Long) {
        cancelTimer()
        timer = Timer()
        lastScreenshotTime = Instant.now().minusMillis(intervalMs - delayBeforeFirst)

        timer.schedule(ScreenshotTimerTask(), delayBeforeFirst, intervalMs)
    }

    private fun cancelTimer() {
        if (this::timer.isInitialized) {
            timer.cancel()
        }
    }

    fun timeSinceLastScreenshot() = Duration.between(lastScreenshotTime, Instant.now()).toMillis()

    fun takeScreenshot(client: MinecraftClient) {
        val worldReq = !Configuration.REQUIRE_IN_WORLD || client.world != null
        val unpausedReq = !Configuration.REQUIRE_UNPAUSED || client.world == null || !client.isPaused

        if (worldReq && unpausedReq) {
            ScreenshotRecorder.saveScreenshot(
                Configuration.getFullDirectory(client.runDirectory, Configuration.SAVE_DIRECTORY),
                Configuration.SCREENSHOT_PREFIX,
                client.framebuffer
            ) { msg: Text ->
                client.execute {
                    if (Configuration.NOTIFY_PLAYER)
                        client.inGameHud?.chatHud?.addMessage(msg)
                }
            }
        }
    }

    fun formatTime(millis: Long): String {
        val millisPerSecond: Long = 1000
        val millisPerMinute: Long = millisPerSecond * 60
        val millisPerHour: Long = millisPerMinute * 60
        val millisPerDay: Long = millisPerHour * 24

        val day = millis / millisPerDay
        val hour = millis / millisPerHour % 24
        val min = millis / millisPerMinute % 60
        val sec = millis / millisPerSecond % 60

        val s = StringBuilder()

        if (day != 0L) {
            s.append(day)
            s.append('d')
        }
        if (hour != 0L) {
            s.append(hour)
            s.append('h')
        }

        s.append(String.format("%02dm%02ds", min, sec))

        return s.toString()
    }

    internal class ScreenshotTimerTask : TimerTask() {
        private val client: MinecraftClient = MinecraftClient.getInstance()

        override fun run() {
            if (client.framebuffer != null)
                takeScreenshot(client)

            lastScreenshotTime = Instant.now()
        }
    }
}