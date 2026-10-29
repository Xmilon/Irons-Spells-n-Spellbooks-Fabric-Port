package io.redspace.ironsspellbooks.damage;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/** Lifesteal uses health lost; status effects still apply to accepted, absorbed hits. */
public final class SpellHitEffects {
    public static void apply(LivingEntity target, Entity attacker, float healthLost, float lifesteal, int freezeTicks, int fireTicks) {
        if (healthLost < 0 || !Float.isFinite(healthLost)) return;
        if (healthLost > 0 && attacker instanceof LivingEntity living && lifesteal > 0 && Float.isFinite(lifesteal)) {
            living.heal(healthLost * lifesteal);
        }
        if (freezeTicks > 0 && target.canFreeze()) {
            target.setTicksFrozen((int) Math.min(Integer.MAX_VALUE, (long) target.getTicksFrozen() + (long) freezeTicks * 2));
        }
        if (fireTicks > 0) target.igniteForTicks(fireTicks);
    }
    private SpellHitEffects() { }
}
