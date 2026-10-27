package io.redspace.ironslib.internal.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironslib.soulfire.client.SoulFireClientHandler;
import io.redspace.ironslib.soulfire.client.SoulFireMaterials;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.FlameFeatureRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FlameFeatureRenderer.class)
public class FlameFeatureRendererMixin {
   @WrapOperation(
      method = "buildGroup",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/feature/FlameFeatureRenderer;prepare(Lnet/minecraft/client/renderer/feature/FlameFeatureRenderer$Submit;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V"
      )
   )
   private void irons_lib$soulFlames(
      FlameFeatureRenderer renderer, FlameFeatureRenderer.Submit submit, VertexConsumer consumer,
      TextureAtlasSprite first, TextureAtlasSprite second, Operation<Void> original,
      @Local(argsOnly = true) FeatureFrameContext context
   ) {
      if (SoulFireClientHandler.isSoulFire(submit.entityRenderState())) {
         first = context.atlasManager().get(SoulFireMaterials.SOUL_FIRE_0);
         second = context.atlasManager().get(SoulFireMaterials.SOUL_FIRE_1);
      }
      original.call(renderer, submit, consumer, first, second);
   }
}
