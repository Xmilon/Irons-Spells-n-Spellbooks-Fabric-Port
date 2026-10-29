package io.redspace.ironsspellbooks.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import io.redspace.ironsspellbooks.api.events.LivingDamageEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityEventsMixin {
    @Shadow protected ItemStack useItem;

    @WrapOperation(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isInvulnerableTo(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;)Z"))
    private boolean irons_spellbooks$incomingDamage(LivingEntity self, ServerLevel level, DamageSource source,
                                                   Operation<Boolean> original, @Local(argsOnly = true) LocalFloatRef amount) {
        if (original.call(self, level, source)) return true;
        if (self.isDeadOrDying()) return false;
        float incoming = amount.get();
        if (!Float.isFinite(incoming) || incoming < 0) return true;
        if (!LivingDamageEvents.ALLOW_DAMAGE.invoker().allow(level, self, source, incoming)) return true;
        float modified = LivingDamageEvents.MODIFY_DAMAGE.invoker().modify(level, self, source, incoming);
        if (!Float.isFinite(modified)) return true;
        amount.set(Math.max(0, modified));
        return false;
    }

    @Inject(method = "hurtServer", at = @At("RETURN"))
    private void irons_spellbooks$acceptedHit(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) LivingDamageEvents.AFTER_HIT.invoker().afterDamage(level, (LivingEntity) (Object) this, source, amount);
    }

    @WrapMethod(method = "actuallyHurt")
    private void irons_spellbooks$healthDamage(ServerLevel level, DamageSource source, float amount, Operation<Void> original) {
        LivingEntity self = (LivingEntity) (Object) this;
        float health = self.getHealth();
        original.call(level, source, amount);
        float lost = health - self.getHealth();
        if (amount > 0) LivingDamageEvents.AFTER_DAMAGE.invoker().afterDamage(level, self, source, Math.max(0, lost));
    }

    // This point follows armor, magic protection and absorption, immediately before health damage is recorded.
    @ModifyVariable(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getCombatTracker()Lnet/minecraft/world/damagesource/CombatTracker;"), argsOnly = true, ordinal = 0)
    private float irons_spellbooks$modifyHealthDamage(float amount, ServerLevel level, DamageSource source, float originalAmount) {
        float modified = LivingDamageEvents.MODIFY_HEALTH_DAMAGE.invoker().modify(level, (LivingEntity) (Object) this, source, amount);
        return Float.isFinite(modified) ? Math.max(0, modified) : 0;
    }

    @ModifyVariable(method = "heal", at = @At("HEAD"), argsOnly = true)
    private float irons_spellbooks$healing(float amount) {
        float modified = LivingDamageEvents.MODIFY_HEALING.invoker().modify((LivingEntity) (Object) this, amount);
        return Float.isFinite(modified) ? Math.max(0, modified) : 0;
    }

    @Inject(method = "knockback(DDDLnet/minecraft/world/damagesource/DamageSource;FZ)V", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$knockback(double strength, double x, double z, DamageSource source, float amount, boolean force, CallbackInfo ci) {
        if (!LivingDamageEvents.ALLOW_KNOCKBACK.invoker().allow((LivingEntity) (Object) this, source)) ci.cancel();
    }

    @WrapMethod(method = "completeUsingItem")
    private void irons_spellbooks$finishItem(Operation<Void> original) {
        LivingEntity self = (LivingEntity) (Object) this;
        ItemStack item = useItem.copy();
        original.call();
        if (!self.level().isClientSide() && !item.isEmpty()) LivingDamageEvents.FINISH_USING_ITEM.invoker().finish(self, item);
    }
}
