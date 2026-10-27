package io.redspace.ironsspellbooks.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.redspace.ironsspellbooks.player.ClientPlayerEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Posts ViewportEvent.ComputeFogColor once vanilla has settled on the fog color. */
@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
    @Shadow
    private static float fogRed;
    @Shadow
    private static float fogGreen;
    @Shadow
    private static float fogBlue;

    @Inject(method = "setupColor", at = @At("TAIL"))
    private static void irons_spellbooks$computeFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, CallbackInfo ci) {
        var event = new ViewportEvent.ComputeFogColor(partialTick, fogRed, fogGreen, fogBlue);
        ClientPlayerEvents.changeFogColor(event);
        NeoForge.EVENT_BUS.post(event);
        if (event.getRed() != fogRed || event.getGreen() != fogGreen || event.getBlue() != fogBlue) {
            fogRed = event.getRed();
            fogGreen = event.getGreen();
            fogBlue = event.getBlue();
            RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
        }
    }
}
