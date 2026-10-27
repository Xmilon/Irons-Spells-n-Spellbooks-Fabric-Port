package io.redspace.ironsspellbooks.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Posts the NeoForge living events that Fabric has no callback for. */
@Mixin(LivingEntity.class)
public abstract class LivingEntityEventsMixin {
    @Shadow
    protected ItemStack useItem;

    @Unique
    private boolean irons_spellbooks$postingIncomingDamage;

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$incomingDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level().isClientSide() || irons_spellbooks$postingIncomingDamage) {
            return;
        }
        var event = NeoForge.EVENT_BUS.post(new LivingIncomingDamageEvent(self, source, amount));
        if (event.isCanceled()) {
            cir.setReturnValue(false);
            return;
        }
        if (event.getAmount() != amount) {
            // Re-enter with the adjusted amount; the guard stops the event from being posted twice.
            irons_spellbooks$postingIncomingDamage = true;
            try {
                cir.setReturnValue(self.hurt(source, event.getAmount()));
            } finally {
                irons_spellbooks$postingIncomingDamage = false;
            }
        }
    }

    @ModifyVariable(method = "heal", at = @At("HEAD"), argsOnly = true)
    private float irons_spellbooks$healEvent(float amount) {
        return NeoForge.EVENT_BUS.post(new LivingHealEvent((LivingEntity) (Object) this, amount)).getAmount();
    }

    @Inject(method = "knockback", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$knockbackEvent(double strength, double x, double z, CallbackInfo ci) {
        if (NeoForge.EVENT_BUS.post(new LivingKnockBackEvent((LivingEntity) (Object) this)).isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = "completeUsingItem", at = @At("HEAD"))
    private void irons_spellbooks$finishUsingItem(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!this.useItem.isEmpty()) {
            NeoForge.EVENT_BUS.post(new LivingEntityUseItemEvent.Finish(self, this.useItem));
        }
    }

    @ModifyVariable(method = "actuallyHurt", at = @At("HEAD"), argsOnly = true)
    private float irons_spellbooks$damagePreEvent(float amount, DamageSource source) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level().isClientSide()) {
            return amount;
        }
        return NeoForge.EVENT_BUS.post(new LivingDamageEvent.Pre(self, source, amount)).getAmount();
    }
}
