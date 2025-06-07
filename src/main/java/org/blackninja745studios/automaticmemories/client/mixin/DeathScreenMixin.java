package org.blackninja745studios.automaticmemories.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DeathScreen;
import org.blackninja745studios.automaticmemories.client.ScreenshotRecorderExt;
import org.blackninja745studios.automaticmemories.client.config.Configuration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public class DeathScreenMixin {
    private static boolean TOOK_FOR_DEATH = false;

    @Inject(method = "init", at = @At("TAIL"))
    public void init(CallbackInfo info) {
        TOOK_FOR_DEATH = false;
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render(DrawContext matrices, int mouseX, int mouseY, float delta, CallbackInfo info) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client != null && Configuration.ENABLED && Configuration.SCREENSHOT_DEATH && !TOOK_FOR_DEATH) {
            client.execute(() -> ScreenshotRecorderExt.saveScreenshot(
                    Configuration.getFullDirectory(client.runDirectory, Configuration.SAVE_DIRECTORY),
                    Configuration.DEATH_PREFIX,
                    client.getFramebuffer(),
                    msg -> client.execute(() -> {
                        if (Configuration.NOTIFY_PLAYER && client.inGameHud != null && client.world != null)
                            client.inGameHud.getChatHud().addMessage(msg);
                    })
                    )
            );
            TOOK_FOR_DEATH = true;
        }
    }
}
