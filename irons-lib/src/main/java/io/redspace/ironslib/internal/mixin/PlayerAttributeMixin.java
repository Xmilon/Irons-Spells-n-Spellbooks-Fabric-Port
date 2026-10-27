package io.redspace.ironslib.internal.mixin;

import io.redspace.ironslib.attribute.AttributeEventsHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerAttributeMixin {
   @ModifyConstant(method = "attack(Lnet/minecraft/world/entity/Entity;)V", constant = @Constant(floatValue = 1.5F), require = 1, allow = 1)
   private float irons_lib$criticalDamage(float multiplier) {
      return AttributeEventsHandler.modifyCriticalMultiplier((Player) (Object) this, multiplier);
   }

   @Inject(method = "getDestroySpeed", at = @At("RETURN"), cancellable = true)
   private void irons_lib$miningSpeed(BlockState state, CallbackInfoReturnable<Float> cir) {
      cir.setReturnValue(AttributeEventsHandler.modifyMiningSpeed((Player) (Object) this, cir.getReturnValueF()));
   }
}
