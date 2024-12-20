package org.blackninja745studios.automaticmemories.client.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import org.blackninja745studios.automaticmemories.client.ScreenshotTimer;
import org.blackninja745studios.automaticmemories.client.config.Configuration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    private static boolean takenScreenshot = false; // Needed because for some reason there's two packets sent so two screenshots would be taken

    @Inject(method = "onDeathMessage", at = @At(value = "HEAD"))
    public void onDeath(DeathMessageS2CPacket packet, CallbackInfo info) {
        if (Configuration.INSTANCE.getSCREENSHOT_ON_DEATH() && !takenScreenshot) {
            takenScreenshot = true;
            ScreenshotTimer.INSTANCE.takeSpecialScreenshot(MinecraftClient.getInstance(), ScreenshotTimer.SpecialScreenshotType.Death);
        }
    }

    @Inject(method = "onPlayerRespawn", at = @At(value = "HEAD"))
    public void onRespawn(PlayerRespawnS2CPacket packet, CallbackInfo info) {
        takenScreenshot = false;
    }
}
