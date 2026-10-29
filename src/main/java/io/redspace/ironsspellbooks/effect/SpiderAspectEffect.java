package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.util.ModTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class SpiderAspectEffect extends MagicMobEffect {
    public static final float DAMAGE_PER_LEVEL = .05f;

    public SpiderAspectEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    public static float increaseDamage(LivingEntity target, DamageSource source, float amount) {
        var attacker = source.getEntity();
        if (attacker instanceof LivingEntity livingAttacker) {
            var effect = livingAttacker.getEffect(MobEffectRegistry.SPIDER_ASPECT);
            if (effect != null) {
                boolean targetHasTagEffect = target.getActiveEffects().stream()
                        .anyMatch(instance -> instance.getEffect().is(ModTags.AFFECTED_BY_SPIDER_ASPECT));

                if (targetHasTagEffect) {
                    int lvl = effect.getAmplifier() + 1;
                    float multiplier = 1 + SpiderAspectEffect.DAMAGE_PER_LEVEL * lvl;
                    return amount * multiplier;
                }
            }
        }
        return amount;
    }
}
