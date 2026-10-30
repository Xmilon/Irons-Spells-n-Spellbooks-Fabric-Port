package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.player.ClientPlayerEvents;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Posts ViewportEvent.ComputeFogColor once vanilla has settled on the fog color, before it is uploaded. */
@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
    @Inject(method = "updateBuffer(Lnet/minecraft/client/renderer/fog/FogData;)V", at = @At("HEAD"))
    private void irons_spellbooks$computeFogColor(FogData data, CallbackInfo ci) {
        var color = data.color;
        var event = new ViewportEvent.ComputeFogColor(0f, color.x, color.y, color.z);
        ClientPlayerEvents.changeFogColor(event);
        NeoForge.EVENT_BUS.post(event);
        color.set(event.getRed(), event.getGreen(), event.getBlue(), color.w);
    }
}
