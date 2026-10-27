package io.redspace.ironslib.internal.mixin;

import io.redspace.ironslib.soulfire.client.SoulFireClientHandler;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class SoulFireEntityRendererMixin {
   @Inject(method = "extractRenderState", at = @At("RETURN"))
   private void irons_lib$captureSoulFire(Entity entity, EntityRenderState state, float partialTick, CallbackInfo ci) {
      SoulFireClientHandler.captureRenderState(entity, state);
   }
}
