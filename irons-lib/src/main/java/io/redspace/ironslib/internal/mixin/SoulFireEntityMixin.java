package io.redspace.ironslib.internal.mixin;

import io.redspace.ironslib.soulfire.ISoulFireEntity;
import io.redspace.ironslib.soulfire.SoulFireHooks;
import io.redspace.ironslib.soulfire.SoulFireState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class SoulFireEntityMixin implements ISoulFireEntity {
   @Shadow
   private int remainingFireTicks;
   @Unique
   private final SoulFireState irons_lib$soulFireState = new SoulFireState();

   @Shadow
   protected abstract boolean getSharedFlag(int var1);

   @Override
   public SoulFireState irons_lib$getSoulFireState() {
      return this.irons_lib$soulFireState;
   }

   @Override
   public boolean irons_lib$isOnFireShared() {
      return this.getSharedFlag(0);
   }

   @Inject(method = "setRemainingFireTicks", at = @At("HEAD"))
   private void irons_lib$pickFireType(int ticks, CallbackInfo ci) {
      SoulFireHooks.onSetRemainingFireTicks((Entity)(Object)this, this.remainingFireTicks, ticks);
   }
}
