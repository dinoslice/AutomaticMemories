package org.blackninja745studios.automaticmemories.client.config;

import com.mojang.blaze3d.systems.RenderSystem;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClothConfigImpl implements ConfigScreenFactory<Screen> {

    @Override
    public Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setTitle(Text.translatable("fabrishot.config.title"))
                .setSavingRunnable(() -> {})
                .setParentScreen(parent);
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory category = builder.getOrCreateCategory(Text.translatable("fabrishot.config.category"));

        category.addEntry(entryBuilder.startStrField(Text.translatable("fabrishot.config.custom_file_name"), "Config.CUSTOM_FILE_NAME")
                .setTooltip(Text.translatable("fabrishot.config.custom_file_name.tooltip"))
                .setDefaultValue("huge_%time%")
//                .setSaveConsumer(b -> Config.CUSTOM_FILE_NAME = b)
                .build());

        category.addEntry(entryBuilder.startBooleanToggle(Text.translatable("fabrishot.config.override_screenshot_key"), false)
                .setDefaultValue(false)
//                .setSaveConsumer(b -> Config.OVERRIDE_SCREENSHOT_KEY = b)
                .build());

        category.addEntry(entryBuilder.startBooleanToggle(Text.translatable("fabrishot.config.save_file"), false)
                .setDefaultValue(true)
//                .setSaveConsumer(b -> Config.SAVE_FILE = b)
                .build());

        category.addEntry(entryBuilder.startIntField(Text.translatable("fabrishot.config.width"), 2)
                .setDefaultValue(3840)
                .setMin(1)
                .setMax(Math.min(65535, RenderSystem.maxSupportedTextureSize()))
//                .setSaveConsumer(i -> Config.CAPTURE_WIDTH = i)
                .build());

        category.addEntry(entryBuilder.startIntField(Text.translatable("fabrishot.config.height"), 2)
                .setDefaultValue(2160)
                .setMin(1)
                .setMax(Math.min(65535, RenderSystem.maxSupportedTextureSize()))
//                .setSaveConsumer(i -> Config.CAPTURE_HEIGHT = i)
                .build());

        category.addEntry(entryBuilder.startIntField(Text.translatable("fabrishot.config.delay"), 4)
                .setTooltip(Text.translatable("fabrishot.config.delay.tooltip"))
                .setDefaultValue(3)
                .setMin(3)
//                .setSaveConsumer(i -> Config.CAPTURE_DELAY = i)
                .build());
        return builder.build();
    }
}