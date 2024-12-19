package org.blackninja745studios.automaticmemories.client.mixins;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.client.toast.AdvancementToast;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import net.minecraft.util.Identifier;
import org.blackninja745studios.automaticmemories.client.ScreenshotTimer;
import org.blackninja745studios.automaticmemories.client.config.Configuration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Map;

@Mixin(ClientAdvancementManager.class)
public class ClientAdvancementManagerMixin {
    /**
     * @author MSKatKing
     * @reason To act as an OnAdvancement event
     */
    @Overwrite
    public void onAdvancements(AdvancementUpdateS2CPacket packet) {
        ClientAdvancementManager manager = (ClientAdvancementManager) (Object) this;

        if (packet.shouldClearCurrent()) {
            manager.getManager().clear();
            manager.advancementProgresses.clear();
        }

        manager.getManager().removeAll(packet.getAdvancementIdsToRemove());
        manager.getManager().load(packet.getAdvancementsToEarn());

        for(Map.Entry<Identifier, AdvancementProgress> entry : packet.getAdvancementsToProgress().entrySet()) {
            Advancement advancement = manager.getManager().get(entry.getKey());
            if (advancement != null) {
                AdvancementProgress advancementProgress = entry.getValue();
                advancementProgress.init(advancement.getCriteria(), advancement.getRequirements());
                manager.advancementProgresses.put(advancement, advancementProgress);
                if (manager.listener != null) {
                    manager.listener.setProgress(advancement, advancementProgress);
                }

                if (!packet.shouldClearCurrent() && advancementProgress.isDone() && advancement.getDisplay() != null && advancement.getDisplay().shouldShowToast()) {
                    MinecraftClient.getInstance().getToastManager().add(new AdvancementToast(advancement));
                    if (Configuration.INSTANCE.getSCREENSHOT_ON_ADVANCEMENT())
                        ScreenshotTimer.INSTANCE.takeSpecialScreenshot(MinecraftClient.getInstance(), ScreenshotTimer.SpecialScreenshotType.Advancement);
                }
            } else {
                ClientAdvancementManager.LOGGER.warn("Server informed client about progress for unknown advancement {}", entry.getKey());
            }
        }
    }
}
