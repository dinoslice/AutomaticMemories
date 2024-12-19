package org.blackninja745studios.automaticmemories.client.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import org.blackninja745studios.automaticmemories.client.ScreenshotTimer;
import org.blackninja745studios.automaticmemories.client.config.Configuration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "setScreen", at = @At("HEAD"))
    public void setScreen(Screen screen, CallbackInfo info) {
        MinecraftClient client = (MinecraftClient) (Object) this;

        if (screen instanceof DeathScreen) {
            if (Configuration.INSTANCE.getSCREENSHOT_ON_DEATH()) {
                ScreenshotTimer.INSTANCE.takeSpecialScreenshot(client, ScreenshotTimer.SpecialScreenshotType.Death);
            }
        }
    }
}
