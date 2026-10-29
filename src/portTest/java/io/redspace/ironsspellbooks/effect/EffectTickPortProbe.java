package io.redspace.ironsspellbooks.effect;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.phys.Vec3;

/** Exercises production effect ticks and lifecycle hooks through the native effect engine. */
public final class EffectTickPortProbe implements ModInitializer {
    private static final class EndingEffect extends MagicMobEffect implements ISyncedMobEffect {
        int removals;
        int lastAmplifier;

        EndingEffect() { super(MobEffectCategory.BENEFICIAL, 0xffffff); }

        @Override
        public void onEffectRemoved(net.minecraft.world.entity.LivingEntity entity, int amplifier) {
            removals++;
            lastAmplifier = amplifier;
        }
    }

    private static final class Fixture extends Zombie {
        Fixture(ServerLevel level) { super(level); setNoAi(true); }
        void tickStatusEffects() { tickEffects(); }
    }

    @Override
    public void onInitialize() {
        Holder<MobEffect> airborne = register("airborne", new AirborneEffect(MobEffectCategory.HARMFUL, 0));
        Holder<MobEffect> falling = register("falling", new FallDamageImmunityEffect(MobEffectCategory.BENEFICIAL, 0));
        EndingEffect first = new EndingEffect();
        EndingEffect second = new EndingEffect();
        Holder<MobEffect> firstHolder = register("first", first);
        Holder<MobEffect> secondHolder = register("second", second);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            Fixture target = new Fixture(server.overworld());
            try {
                target.fallDistance = 42;
                target.addEffect(new MobEffectInstance(falling, 2));
                target.tickStatusEffects();
                require(target.fallDistance == 0 && target.hasEffect(falling), "Native tick did not reset falling distance");
                target.tickStatusEffects();
                require(!target.hasEffect(falling), "Fall immunity did not expire");

                target.setHealth(20);
                target.damageCooldownTime = 0;
                target.horizontalCollision = false;
                target.setDeltaMovement(Vec3.ZERO);
                target.addEffect(new MobEffectInstance(airborne, 10, 1));
                target.tickStatusEffects();
                require(target.hasEffect(airborne) && target.getHealth() == 20, "Airborne damaged without collision");
                target.horizontalCollision = true;
                target.setDeltaMovement(new Vec3(.05, 0, 0));
                target.tickStatusEffects();
                require(target.hasEffect(airborne), "Slow collision prematurely ended airborne");
                target.setDeltaMovement(new Vec3(.5, 0, 0));
                target.tickStatusEffects();
                require(!target.hasEffect(airborne), "Airborne false result did not remove the effect");
                require(Math.abs(target.getHealth() - 15) < .001f, "Airborne native server damage changed");

                target.addEffect(new MobEffectInstance(firstHolder, 1, 2));
                target.tickStatusEffects();
                require(first.removals == 1 && first.lastAmplifier == 2, "Expiry callback missing or duplicated");
                require(!target.removeEffect(firstHolder) && first.removals == 1, "Missing effect dispatched cleanup");

                target.addEffect(new MobEffectInstance(firstHolder, 20));
                target.addEffect(new MobEffectInstance(firstHolder, 40, 3));
                require(first.removals == 1, "Effect update dispatched removal");
                require(target.removeEffect(firstHolder), "Explicit effect removal rejected");
                require(first.removals == 2 && first.lastAmplifier == 3, "Explicit removal lost updated amplifier");

                target.addEffect(new MobEffectInstance(firstHolder, 20, 1));
                target.addEffect(new MobEffectInstance(secondHolder, 20, 4));
                require(target.removeAllEffects(), "Bulk effect removal rejected");
                require(first.removals == 3 && second.removals == 1 && second.lastAmplifier == 4,
                        "Batch removal did not clean each effect exactly once");
                require(target.getActiveEffects().isEmpty(), "Bulk removal left effects behind");
                System.out.println("Native effect checks passed: server tick dispatch, fall immunity, airborne damage/end, expiry, updates, explicit and batch cleanup");
            } finally {
                target.discard();
                server.halt(false);
            }
        });
    }

    private static Holder<MobEffect> register(String name, MobEffect effect) {
        Registry.register(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath("spellbooks_effect_probe", name), effect);
        return BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect);
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
