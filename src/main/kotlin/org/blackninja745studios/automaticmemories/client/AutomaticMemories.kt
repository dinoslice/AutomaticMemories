package org.blackninja745studios.automaticmemories.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.blackninja745studios.automaticmemories.client.config.Configuration

class AutomaticMemories : ClientModInitializer {
    companion object {
        val LOGGER: Logger = LogManager.getLogger()

        fun addChatPrefix(text: Text): MutableText = Text.literal("")
                .append(Text.literal("[").formatted(Formatting.DARK_GRAY))
                .append(Text.translatable("automaticmemories.chat_prefix.first").formatted(Formatting.LIGHT_PURPLE))
                .append(Text.translatable("automaticmemories.chat_prefix.second").formatted(Formatting.BLUE))
                .append(Text.literal("] ").formatted(Formatting.DARK_GRAY))
                .append(text)
    }

    override fun onInitializeClient() {
        Configuration.loadFromFile(Configuration.CONFIG_PATH)

        if (Configuration.RESTART_TIMER_EACH_SESSION) {
            ScreenshotTimer.restartOrStartTimer(Configuration.INTERVAL_MS, Configuration.INTERVAL_MS)
        } else {
            ScreenshotTimer.restartOrStartTimer(Configuration.LEFTOVER_INTERVAL_MS, Configuration.INTERVAL_MS)
            Configuration.LEFTOVER_INTERVAL_MS = 0
            Configuration.saveToFile(Configuration.CONFIG_PATH)
        }

        ClientLifecycleEvents.CLIENT_STOPPING.register {
            Configuration.LEFTOVER_INTERVAL_MS = Configuration.INTERVAL_MS - ScreenshotTimer.timeSinceLastScreenshot()
            Configuration.saveToFile(Configuration.CONFIG_PATH)
        }
    }
}