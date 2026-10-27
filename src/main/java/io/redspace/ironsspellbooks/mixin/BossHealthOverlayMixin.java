package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.api.util.BossbarManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

/** Lets custom boss bars take over rendering and tallies how much space the bars take each frame. */
@Mixin(BossHealthOverlay.class)
public abstract class BossHealthOverlayMixin {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    @Final
    private Map<UUID, LerpingBossEvent> events;

    @Shadow
    private native void extractBar(GuiGraphicsExtractor guiGraphics, int x, int y, BossEvent bossEvent);

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$renderWithCustomBars(GuiGraphicsExtractor guiGraphics, CallbackInfo ci) {
        ci.cancel();
        BossbarManager.beginFrame();
        if (this.events.isEmpty()) {
            return;
        }
        int width = guiGraphics.guiWidth();
        int y = 12;
        for (LerpingBossEvent bossEvent : this.events.values()) {
            int increment = 10 + this.minecraft.font.lineHeight;
            var custom = BossbarManager.renderCustomBossbar(guiGraphics, bossEvent, y, increment);
            if (custom.isPresent()) {
                increment = custom.getAsInt();
            } else {
                int x = width / 2 - 91;
                this.extractBar(guiGraphics, x, y, bossEvent);
                Component name = bossEvent.getName();
                int nameWidth = this.minecraft.font.width(name);
                guiGraphics.text(this.minecraft.font, name, width / 2 - nameWidth / 2, y - 9, 16777215);
            }
            BossbarManager.addFrameHeight(increment);
            y += increment;
            if (y >= guiGraphics.guiHeight() / 3) {
                break;
            }
        }
    }
}
