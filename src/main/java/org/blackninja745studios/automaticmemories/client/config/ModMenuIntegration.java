package org.blackninja745studios.automaticmemories.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

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
                entryBuilder.startLongField(Text.translatable("automaticmemories.config.interval_ms"), Configuration.INTERVAL_MS)
                    .setDefaultValue(3600 * 1000)
                    .setMin(5 * 1000)
                    .setSaveConsumer(l -> Configuration.INTERVAL_MS = l)
                    .setTooltip(Optional.of(new Text[] {
                        Text.translatable("automaticmemories.config.interval_ms.tooltip.main"),
                        Text.translatable("automaticmemories.config.interval_ms.tooltip.minimum"),
                    }))
                    .build()
            );

            return builder.build();
        };
    }
}
