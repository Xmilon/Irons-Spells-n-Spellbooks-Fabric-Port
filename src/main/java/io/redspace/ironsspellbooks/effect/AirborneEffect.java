package io.redspace.ironsspellbooks.effect;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerLevel;

public class AirborneEffect extends MobEffect {
    public AirborneEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        return true;
    }

    public static final float damage_per_amp = 0.5f;

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity livingEntity, int pAmplifier) {
        if (livingEntity.horizontalCollision) {
            double speed = livingEntity.getDeltaMovement().horizontalDistance();
            if (speed * 10.0D - 1.0D > 0) {
                livingEntity.playSound(SoundEvents.HOSTILE_BIG_FALL, 2.0F, 1.5F);
                livingEntity.hurtServer(level, livingEntity.damageSources().flyIntoWall(), getDamageFromLevel(pAmplifier + 1));
                return false;
            }
        }
        return true;
    }

    public static float getDamageFromLevel(int level) {
        return 4 + level * damage_per_amp;
    }
}

