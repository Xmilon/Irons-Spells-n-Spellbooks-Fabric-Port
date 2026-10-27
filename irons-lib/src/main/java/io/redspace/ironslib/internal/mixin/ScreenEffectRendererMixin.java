package io.redspace.ironslib.internal.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.redspace.ironslib.soulfire.client.SoulFireClientHandler;
import io.redspace.ironslib.soulfire.client.SoulFireMaterials;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.client.resources.model.sprite.SpriteId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {
   @ModifyExpressionValue(
      method = "submit",
      at = @At(value = "FIELD", target = "Lnet/minecraft/client/resources/model/ModelBakery;FIRE_1:Lnet/minecraft/client/resources/model/sprite/SpriteId;")
   )
   private SpriteId irons_lib$soulFireOverlay(SpriteId original, @Local(argsOnly = true) PlayerRenderState state) {
      return state.avatarRenderState != null && SoulFireClientHandler.isSoulFire(state.avatarRenderState)
         ? SoulFireMaterials.SOUL_FIRE_1 : original;
   }
}
