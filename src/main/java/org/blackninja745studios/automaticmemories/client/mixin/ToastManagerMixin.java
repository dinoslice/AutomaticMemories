package org.blackninja745studios.automaticmemories.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import org.blackninja745studios.automaticmemories.client.ScreenshotRecorderExt;
import org.blackninja745studios.automaticmemories.client.config.Configuration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ToastManager.class)
public class ToastManagerMixin {
    @Inject(method = "add", at = @At("RETURN"))
    public void add(Toast toast, CallbackInfo info) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client != null && Configuration.ENABLED && Configuration.SCREENSHOT_ADVANCEMENT)
            client.execute(() -> ScreenshotRecorderExt.saveScreenshot(
                            Configuration.getFullDirectory(client.runDirectory, Configuration.SAVE_DIRECTORY),
                            Configuration.ADVANCEMENT_PREFIX,
                            client.getFramebuffer(),
                            msg -> client.execute(() -> {
                                if (Configuration.NOTIFY_PLAYER && client.inGameHud != null && client.world != null)
                                    client.inGameHud.getChatHud().addMessage(msg);
                            })
                    )
            );
    }
}
