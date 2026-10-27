package io.redspace.ironslib.internal.mixin;

import io.redspace.ironslib.soulfire.SoulFireHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.SoulFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BaseFireBlock.class)
public class SoulFireBlockMixin {
   @Inject(method = "entityInside", at = @At("TAIL"))
   private void irons_lib$applySoulFireBlock(
      BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise, CallbackInfo ci
   ) {
      if (state.getBlock() instanceof SoulFireBlock) {
         SoulFireHooks.onInsideSoulFire(entity);
      }
   }
}
