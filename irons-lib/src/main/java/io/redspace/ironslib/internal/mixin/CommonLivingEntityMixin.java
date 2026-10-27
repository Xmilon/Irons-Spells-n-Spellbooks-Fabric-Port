package io.redspace.ironslib.internal.mixin;

import io.redspace.ironslib.attribute.AttributeEventsHandler;
import io.redspace.ironslib.registry.IronsLibRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class CommonLivingEntityMixin {
   @Inject(
      method = "hurtServer",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)V"),
      cancellable = true
   )
   private void irons_lib$dodge(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
      if (AttributeEventsHandler.shouldDodge((LivingEntity) (Object) this)) {
         cir.setReturnValue(false);
      }
   }

   @ModifyArg(
      method = "hurtServer",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)V"),
      index = 2
   )
   private float irons_lib$projectileDamage(ServerLevel level, DamageSource source, float damage) {
      return AttributeEventsHandler.modifyProjectileDamage(source, damage);
   }

   @ModifyVariable(method = "heal", at = @At("HEAD"), argsOnly = true)
   private float irons_lib$healing(float healing) {
      return AttributeEventsHandler.modifyHealing((LivingEntity) (Object) this, healing);
   }

   @ModifyArg(
      method = "dropExperience",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"),
      index = 2
   )
   private int irons_lib$experience(int experience) {
      return AttributeEventsHandler.modifyExperience(((LivingEntity) (Object) this).getLastHurtByPlayer(), experience);
   }

   @Redirect(method = "getDamageAfterArmorAbsorb", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getArmorValue()I"))
   private int irons_lib$applyArmorPierce(LivingEntity target, DamageSource damageSource, float damageAmount) {
      int armor = target.getArmorValue();
      if (damageSource.getEntity() instanceof LivingEntity attacker && armor > 0) {
         double armorPierce = attacker.getAttributeValue(IronsLibRegistries.AttributeRegistry.ARMOR_PIERCE);
         return armorPierce <= 0.0 ? armor : Math.max(0, (int) Math.floor(armor - armorPierce));
      }
      return armor;
   }
}
