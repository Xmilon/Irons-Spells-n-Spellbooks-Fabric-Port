package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;

public class SoulBurnMobEffect extends MagicMobEffect {
    public static final float PERCENT_PER_AMPLIFIER = 0.05f;
    public SoulBurnMobEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    public static float soulBurnReduceHealing(net.minecraft.world.entity.LivingEntity entity, float amount) {
        MobEffectInstance inst = entity.getEffect(MobEffectRegistry.SOUL_BURN);
        if (inst == null) {
            return amount;
        }
        return amount * multiplierForAmplifier(inst.getAmplifier());
    }

    public static float multiplierForAmplifier(int amplifier) {
        return Math.clamp(1 - (amplifier + 1) * PERCENT_PER_AMPLIFIER, 0, 1);
    }
}
