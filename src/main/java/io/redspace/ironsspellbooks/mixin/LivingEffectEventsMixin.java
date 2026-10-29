package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.effect.IMobEffectEndCallback;
import io.redspace.ironsspellbooks.effect.ISyncedMobEffect;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(LivingEntity.class)
public abstract class LivingEffectEventsMixin {
    @Inject(method = "onEffectsRemoved", at = @At("HEAD"))
    private void irons_spellbooks$effectsRemoved(Collection<MobEffectInstance> effects, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self.level() instanceof ServerLevel level)) return;
        for (MobEffectInstance instance : effects) {
            if (instance.getEffect().value() instanceof IMobEffectEndCallback effect) {
                effect.onEffectRemoved(self, instance.getAmplifier());
            }
            if (instance.getEffect().value() instanceof ISyncedMobEffect) {
                level.getChunkSource().sendToTrackingPlayers(self, new ClientboundRemoveMobEffectPacket(self.getId(), instance.getEffect()));
            }
        }
    }

    @Inject(method = "onEffectAdded", at = @At("HEAD"))
    private void irons_spellbooks$effectAdded(MobEffectInstance instance, Entity source, CallbackInfo ci) {
        irons_spellbooks$syncEffect(instance);
    }

    @Inject(method = "onEffectUpdated", at = @At("HEAD"))
    private void irons_spellbooks$effectUpdated(MobEffectInstance instance, boolean forced, Entity source, CallbackInfo ci) {
        irons_spellbooks$syncEffect(instance);
    }

    @org.spongepowered.asm.mixin.Unique
    private void irons_spellbooks$syncEffect(MobEffectInstance instance) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level() instanceof ServerLevel level && instance.getEffect().value() instanceof ISyncedMobEffect) {
            level.getChunkSource().sendToTrackingPlayers(self, new ClientboundUpdateMobEffectPacket(self.getId(), instance, false));
        }
    }
}
