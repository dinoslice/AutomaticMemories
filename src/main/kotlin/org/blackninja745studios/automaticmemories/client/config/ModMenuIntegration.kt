package org.blackninja745studios.automaticmemories.client.config

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.blackninja745studios.automaticmemories.client.ScreenshotTimer
import java.nio.file.Paths
import java.util.*

class ModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> = ConfigScreenFactory {
        parent: Screen ->

        val builder = ConfigBuilder.create()
            .setTitle(Text.translatable("automaticmemories.config.title"))
            .setSavingRunnable {
                Configuration.saveToFile(Configuration.CONFIG_PATH)
            }
            .setParentScreen(parent)
        
        val entryBuilder = builder.entryBuilder()
        
        val category = builder.getOrCreateCategory(Text.translatable("automaticmemories.config.category"))
        
        category.addEntry(
            entryBuilder.startSubCategory(Text.translatable("automaticmemories.config.interval.subcategory"), listOf(
                entryBuilder.startLongField(Text.translatable("automaticmemories.config.interval.interval_ms"), Configuration.INTERVAL_MS)
                    .setDefaultValue(3600 * 1000 * 3)
                    .setMin(5 * 1000)
                    .setSaveConsumer {
                        Configuration.INTERVAL_MS = it
                        ScreenshotTimer.restartOrStartTimer(0, Configuration.INTERVAL_MS)
                    }
                    .setTooltipSupplier {
                        l: Long ->
                        val main = Text.translatable("automaticmemories.config.interval.interval_ms.tooltip.main")
                        val current = Text.translatable("automaticmemories.config.interval.interval_ms.tooltip.editing", ScreenshotTimer.formatTime(l))
                        val remaining = Text.translatable("automaticmemories.config.interval.interval_ms.tooltip.remaining",
                            ScreenshotTimer.formatTime(Configuration.INTERVAL_MS - ScreenshotTimer.timeSinceLastScreenshot()),
                            ScreenshotTimer.formatTime(Configuration.INTERVAL_MS)
                        ).formatted(Formatting.GRAY)
                        
                        if (l == Configuration.INTERVAL_MS) {
                            Optional.of(arrayOf(main, remaining))
                        } else {
                            Optional.of(arrayOf(main, current, remaining))
                        }
                    }
                    .build(),
                
                entryBuilder.startBooleanToggle(Text.translatable("automaticmemories.config.interval.restart_timer_each_session"), Configuration.RESTART_TIMER_EACH_SESSION)
                    .setDefaultValue(false)
                    .setSaveConsumer {
                        Configuration.RESTART_TIMER_EACH_SESSION = it
                    }
                    .setTooltip(Text.translatable("automaticmemories.config.interval.restart_timer_each_session.tooltip"))
                    .build(),
                entryBuilder.startBooleanToggle(Text.translatable("automaticmemories.config.interval.require_in_world"), Configuration.REQUIRE_IN_WORLD)
                    .setDefaultValue(true)
                    .setSaveConsumer {
                        Configuration.REQUIRE_IN_WORLD = it
                    }
                    .setTooltip(Text.translatable("automaticmemories.config.interval.require_in_world.tooltip"))
                    .build(),
                entryBuilder.startBooleanToggle(Text.translatable("automaticmemories.config.interval.require_unpaused"), Configuration.REQUIRE_UNPAUSED)
                    .setDefaultValue(false)
                    .setSaveConsumer {
                        Configuration.REQUIRE_UNPAUSED = it
                    }
                    .setTooltip(Text.translatable("automaticmemories.config.interval.require_unpaused.tooltip"))
                    .build()
            ))
                .setExpanded(true)
                .build()
        )
        
        category.addEntry(
            entryBuilder.startSubCategory(Text.translatable("automaticmemories.config.save.subcategory"), listOf(
                entryBuilder.startTextField(Text.translatable("automaticmemories.config.save.save_directory"), Configuration.SAVE_DIRECTORY)
                    .setDefaultValue("screenshots")
                    .setSaveConsumer {
                        Configuration.SAVE_DIRECTORY = it
                    }
                    .setErrorSupplier {
                        try {
                            Paths.get(it)
                        } catch(e: Exception) {
                            Optional.of(Text.translatable("automaticmemories.config.save.save_directory.error", e.message))
                        }
                        Optional.empty()
                    }
                    .setTooltipSupplier {
                        s: String ->
                        val runDir = MinecraftClient.getInstance().runDirectory
                        
                        val main = Text.translatable("automaticmemories.config.save.save_directory.tooltip.main")
                        val current = Text.translatable("automaticmemories.config.save.save_directory.tooltip.editing", Configuration.getFullDirectory(runDir, s))
                            .formatted(Formatting.GOLD)
                        val remaining = Text.translatable("automaticmemories.config.save.save_directory.tooltip.current",
                            Configuration.getFullDirectory(runDir, Configuration.SAVE_DIRECTORY)
                        ).formatted(Formatting.GRAY)
                        
                        if (Configuration.SAVE_DIRECTORY == s) {
                            Optional.of(arrayOf(main, remaining))
                        } else {
                            Optional.of(arrayOf(main, current, remaining))
                        }
                    }
                    .build(),
                
                entryBuilder.startTextField(Text.translatable("automaticmemories.config.save.screenshot_prefix"), Configuration.SCREENSHOT_PREFIX)
                    .setDefaultValue("auto_")
                    .setSaveConsumer {
                        Configuration.SCREENSHOT_PREFIX = it
                    }
                    .setTooltip(Text.translatable("automaticmemories.config.save.screenshot_prefix.tooltip"))
                    .build()
            ))
                .setExpanded(true)
                .build()
        )
        
        category.addEntry(
            entryBuilder.startSubCategory(Text.translatable("automaticmemories.config.miscellaneous.subcategory"), listOf(
                entryBuilder.startBooleanToggle(Text.translatable("automaticmemories.config.miscellaneous.notify_player"), Configuration.NOTIFY_PLAYER)
                    .setDefaultValue(false)
                    .setSaveConsumer { 
                        Configuration.NOTIFY_PLAYER = it
                    }
                    .setTooltip(Optional.of(arrayOf(
                        Text.translatable("automaticmemories.config.miscellaneous.notify_player.tooltip.main"),
                        Text.translatable("automaticmemories.config.miscellaneous.notify_player.tooltip.disabled_warnings")
                    )))
                    .build()
            ))
                .setExpanded(true)
                .build()
        )
        
        builder.build()
    }
}