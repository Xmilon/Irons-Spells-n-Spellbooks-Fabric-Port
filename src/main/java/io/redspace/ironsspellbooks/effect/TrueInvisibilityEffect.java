package io.redspace.ironsspellbooks.effect;


import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class TrueInvisibilityEffect extends MagicMobEffect implements ISyncedMobEffect {
    public TrueInvisibilityEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void onEffectAdded(LivingEntity livingEntity, int pAmplifier) {
        super.onEffectAdded(livingEntity, pAmplifier);
        clearNearbyAggro(livingEntity);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        // Keep dropping aggro while active, not only on first application.
        return duration % 10 == 0;
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity livingEntity, int amplifier) {
        clearNearbyAggro(livingEntity);
        // Force entity invisibility flag to keep all model layers hidden.
        livingEntity.setInvisible(true);
        return true;
    }

    @Override
    public void clientTick(LivingEntity livingEntity, net.minecraft.world.effect.MobEffectInstance effectInstance) {
        livingEntity.setInvisible(true);
    }

    private static void clearNearbyAggro(LivingEntity livingEntity) {
        if (!(livingEntity.level() instanceof ServerLevel serverLevel)) return;
        var targetingCondition = TargetingConditions.forCombat().ignoreLineOfSight()
                .selector((entity, level) -> entity instanceof Mob mob && mob.getTarget() == livingEntity);

        //remove aggro from anything targeting us
        serverLevel.getNearbyEntities(Mob.class, targetingCondition, livingEntity, livingEntity.getBoundingBox().inflate(40D))
                .forEach(entityTargetingCaster -> {
                    //IronsSpellbooks.LOGGER.debug("InvisibilitySpell Clear Target From:{}", entityTargetingCaster);
                    entityTargetingCaster.setTarget(null);
                    entityTargetingCaster.setAggressive(false);
                    entityTargetingCaster.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
                });
    }

    public static void onDealDamage(DamageSource source) {
        if (source.getEntity() instanceof LivingEntity livingAttacker && livingAttacker.hasEffect(MobEffectRegistry.TRUE_INVISIBILITY)) {
            livingAttacker.removeEffect(MobEffectRegistry.TRUE_INVISIBILITY);
        }
    }
}

