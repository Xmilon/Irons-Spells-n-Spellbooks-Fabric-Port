package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;


public class BlightEffect extends MagicMobEffect {
    public static final float DAMAGE_PER_LEVEL = -.05f;
    public static final float HEALING_PER_LEVEL = -.10f;

    public BlightEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    public static float reduceHealing(LivingEntity target, float amount) {
        var effect = target.getEffect(MobEffectRegistry.BLIGHT);
        if (effect != null) {
            int lvl = effect.getAmplifier() + 1;
            float healingMult = 1 + HEALING_PER_LEVEL * lvl;
            return amount * healingMult;
        }
        return amount;
    }

    public static float reduceDamageOutput(DamageSource source, float amount) {
        Entity attacker = source.getEntity();
        if (attacker instanceof LivingEntity livingAttacker) {
            var effect = livingAttacker.getEffect(MobEffectRegistry.BLIGHT);
            if (effect != null) {
                int lvl = effect.getAmplifier() + 1;
                float multiplier = 1 + BlightEffect.DAMAGE_PER_LEVEL * lvl;
                return amount * multiplier;
            }
        }
        return amount;
    }
}
