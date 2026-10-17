package io.redspace.ironsspellbooks.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

/** Lets custom boss bars take over rendering, mirroring NeoForge's CustomizeGuiOverlayEvent.BossEventProgress. */
@Mixin(BossHealthOverlay.class)
public abstract class BossHealthOverlayMixin {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    @Final
    Map<UUID, LerpingBossEvent> events;

    @Shadow
    private native void drawBar(GuiGraphics guiGraphics, int x, int y, BossEvent bossEvent);

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$renderWithEvent(GuiGraphics guiGraphics, CallbackInfo ci) {
        ci.cancel();
        if (this.events.isEmpty()) {
            return;
        }
        int width = guiGraphics.guiWidth();
        int y = 12;
        for (LerpingBossEvent bossEvent : this.events.values()) {
            int x = width / 2 - 91;
            var event = new CustomizeGuiOverlayEvent.BossEventProgress(guiGraphics, bossEvent, y, 10 + this.minecraft.font.lineHeight);
            NeoForge.EVENT_BUS.post(event);
            if (!event.isCanceled()) {
                this.drawBar(guiGraphics, x, y, bossEvent);
                Component name = bossEvent.getName();
                int nameWidth = this.minecraft.font.width(name);
                guiGraphics.drawString(this.minecraft.font, name, width / 2 - nameWidth / 2, y - 9, 16777215);
            }
            y += event.getIncrement();
            if (y >= guiGraphics.guiHeight() / 3) {
                break;
            }
        }
    }
}
