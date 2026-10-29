package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.spells.ice_tomb.IceTombEntity;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;

public class ChilledEffect extends MagicMobEffect {

    public ChilledEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity pLivingEntity, int pAmplifier) {
        if (pLivingEntity.getVehicle() instanceof IceTombEntity) {
            return false;
        }
        if (pLivingEntity.isFullyFrozen()) {
            IceTombEntity iceTombEntity = new IceTombEntity(level, null);
            iceTombEntity.snapTo(pLivingEntity.position());
            iceTombEntity.setDeltaMovement(pLivingEntity.getDeltaMovement());
            iceTombEntity.setEvil();
            iceTombEntity.setLifetime(20 * 5);
            if (!level.addFreshEntity(iceTombEntity)) return true;
            if (!pLivingEntity.startRiding(iceTombEntity, true, true)) {
                iceTombEntity.discard();
                return true;
            }
            pLivingEntity.playSound(SoundRegistry.FROSTBITE_FREEZE.get(), 2, Utils.random.nextInt(9, 11) * .1f);
            return false;
        }
        return true;
    }
}

