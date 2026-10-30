package io.redspace.ironsspellbooks.mixin;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import io.redspace.ironsspellbooks.render.PocketDimensionEffects;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkyRenderer.class)
public abstract class PocketDimensionSkyMixin {
    @Shadow @Final private RenderTarget renderTarget;

    @Inject(method = "extractRenderState", at = @At("RETURN"))
    private void irons_spellbooks$capturePocketSky(ClientLevel level, float partialTick, Camera camera, SkyRenderState state, CallbackInfo ci) {
        var snapshot = PocketDimensionEffects.capture(level, partialTick, camera);
        ((FabricRenderState) state).setData(PocketDimensionEffects.SKY, snapshot);
        if (snapshot != null) state.skybox = DimensionType.Skybox.END;
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$renderPocketSky(GpuBufferSlice fog, SkyRenderState state, CallbackInfo ci) {
        var snapshot = ((FabricRenderState) state).getData(PocketDimensionEffects.SKY);
        if (snapshot != null) {
            PocketDimensionEffects.draw(snapshot, fog, renderTarget);
            ci.cancel();
        }
    }
}
