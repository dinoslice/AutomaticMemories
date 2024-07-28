package org.blackninja745studios.automaticmemories.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.blackninja745studios.automaticmemories.client.PeriodicTimerSingleton;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setTitle(Text.translatable("automaticmemories.config.title"))
                    .setSavingRunnable(() -> Configuration.saveToFile(Configuration.CONFIG_PATH))
                    .setParentScreen(parent);

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            ConfigCategory category = builder.getOrCreateCategory(Text.translatable("automaticmemories.config.category"));

            category.addEntry(
                entryBuilder.startSubCategory(Text.translatable("automaticmemories.config.interval.subcategory"), List.of(
                    entryBuilder.startLongField(Text.translatable("automaticmemories.config.interval.interval_ms"), Configuration.INTERVAL_MS)
                        .setDefaultValue(3600 * 1000)
                        .setMin(5 * 1000)
                        .setSaveConsumer(l -> {
                            Configuration.INTERVAL_MS = l;
                            PeriodicTimerSingleton.restartOrStartTimer(0, l);
                        })
                        .setTooltipSupplier(l -> {
                            Text main = Text.translatable("automaticmemories.config.interval.interval_ms.tooltip.main");

                            Text current = Text.translatable("automaticmemories.config.interval.interval_ms.tooltip.editing", PeriodicTimerSingleton.formatTime(l))
                                    .formatted(Formatting.GOLD);

                            Text remaining = Text.translatable("automaticmemories.config.interval.interval_ms.tooltip.remaining",
                                    PeriodicTimerSingleton.formatTime(Configuration.INTERVAL_MS - PeriodicTimerSingleton.timeSinceLastScreenshot()),
                                    PeriodicTimerSingleton.formatTime(Configuration.INTERVAL_MS)
                            ).formatted(Formatting.GRAY);

                            return l == Configuration.INTERVAL_MS ?
                                    Optional.of(new Text[] { main, remaining }) :
                                    Optional.of(new Text[] { main, current, remaining });
                        })
                        .build(),

                    entryBuilder.startBooleanToggle(Text.translatable("automaticmemories.config.interval.restart_timer_each_session"), Configuration.RESTART_TIMER_EACH_SESSION)
                        .setDefaultValue(false)
                        .setSaveConsumer(b -> Configuration.RESTART_TIMER_EACH_SESSION = b)
                        .setTooltip(Optional.of(new Text[] {
                                Text.translatable("automaticmemories.config.interval.restart_timer_each_session.tooltip")
                        }))
                        .build()
                ))
                .setExpanded(true)
                .build()
            );

            category.addEntry(
                entryBuilder.startSubCategory(Text.translatable("automaticmemories.config.save.subcategory"), List.of(
                    entryBuilder.startTextField(Text.translatable("automaticmemories.config.save.save_directory"), Configuration.SAVE_DIRECTORY)
                        .setDefaultValue("screenshots")
                        .setSaveConsumer(s -> Configuration.SAVE_DIRECTORY = s)
                        .setErrorSupplier(s -> {
                            try {
                                Paths.get(s);
                            } catch (Exception e) {
                                return Optional.of(Text.translatable("automaticmemories.config.save.save_directory.error", e.getMessage()));
                            }
                            return Optional.empty();
                        })
                        .setTooltipSupplier(s -> {
                            File runDir = MinecraftClient.getInstance().runDirectory;

                            Text main = Text.translatable("automaticmemories.config.save.save_directory.tooltip.main");

                            Text current = Text.translatable("automaticmemories.config.save.save_directory.tooltip.editing", Configuration.getFullDirectory(runDir, s))
                                .formatted(Formatting.GOLD);

                            Text remaining = Text.translatable("automaticmemories.config.save.save_directory.tooltip.current",
                                Configuration.getFullDirectory(runDir, Configuration.SAVE_DIRECTORY)
                            ).formatted(Formatting.GRAY);

                            return Configuration.SAVE_DIRECTORY.equals(s) ?
                                Optional.of(new Text[] { main, remaining }) :
                                Optional.of(new Text[] { main, current, remaining });
                        })
                        .build(),

                        entryBuilder.startTextField(Text.translatable("automaticmemories.config.save.screenshot_prefix"), Configuration.SCREENSHOT_PREFIX)
                            .setDefaultValue("auto_")
                            .setSaveConsumer(s -> Configuration.SCREENSHOT_PREFIX = s)
                            .setTooltip(Optional.of(new Text[] {
                                Text.translatable("automaticmemories.config.save.screenshot_prefix.tooltip")
                            }))
                            .build()
                ))
                .setExpanded(true)
                .build()
            );

            category.addEntry(
                entryBuilder.startSubCategory(Text.translatable("automaticmemories.config.miscellaneous.subcategory"), List.of(
                    entryBuilder.startBooleanToggle(Text.translatable("automaticmemories.config.miscellaneous.notify_player"), Configuration.NOTIFY_PLAYER)
                        .setSaveConsumer(b -> Configuration.NOTIFY_PLAYER = b)
                        .setDefaultValue(false)
                        .setTooltip(Optional.of(new Text[] {
                            Text.translatable("automaticmemories.config.miscellaneous.notify_player.tooltip.main"),
                            Text.translatable("automaticmemories.config.miscellaneous.notify_player.tooltip.disabled_warnings")
                        }))
                        .build()
                ))
                .setExpanded(true)
                .build()
            );

            return builder.build();
        };
    }
}
