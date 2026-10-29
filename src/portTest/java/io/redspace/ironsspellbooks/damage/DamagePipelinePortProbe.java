package io.redspace.ironsspellbooks.damage;

import io.redspace.ironsspellbooks.api.events.LivingDamageEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

/** Loads the production mixin, native event API and spell hit effects on an actual Fabric server. */
public final class DamagePipelinePortProbe implements ModInitializer {
    private enum Mode { PLAIN, CANCEL, DOUBLE, EFFECTS, HEARTSTOP, KNOCKBACK, HALF_HEAL, INVALID, IFRAMES }
    private Mode mode = Mode.PLAIN;
    private LivingEntity attacker;
    private int incoming, modifications, healthStage, after, accepted, knocks, finishedItems;
    private float lastHealthAmount, lastLost, deferred;

    @Override
    public void onInitialize() {
        LivingDamageEvents.ALLOW_DAMAGE.register((level, target, source, amount) -> { incoming++; return mode != Mode.CANCEL; });
        LivingDamageEvents.MODIFY_DAMAGE.register((level, target, source, amount) -> {
            modifications++;
            return mode == Mode.DOUBLE ? amount * 2 : mode == Mode.INVALID ? Float.NaN : amount;
        });
        LivingDamageEvents.MODIFY_HEALTH_DAMAGE.register((level, target, source, amount) -> {
            healthStage++;
            lastHealthAmount = amount;
            if (mode == Mode.HEARTSTOP) { deferred += amount * .5f; return 0; }
            return amount;
        });
        LivingDamageEvents.AFTER_DAMAGE.register((level, target, source, lost) -> {
            after++;
            lastLost = lost;
            if (mode == Mode.EFFECTS) SpellHitEffects.apply(target, attacker, lost, .5f, 7, 30);
        });
        LivingDamageEvents.AFTER_HIT.register((level, target, source, amount) -> {
            accepted++;
            if (mode == Mode.IFRAMES) target.damageCooldownTime = 0;
        });
        LivingDamageEvents.ALLOW_KNOCKBACK.register((target, source) -> { knocks++; return mode != Mode.KNOCKBACK; });
        LivingDamageEvents.MODIFY_HEALING.register((target, amount) -> mode == Mode.HALF_HEAL ? amount * .5f : amount);
        LivingDamageEvents.FINISH_USING_ITEM.register((target, item) -> {
            require(item.is(Items.APPLE) && item.getCount() == 1, "Consumed item snapshot was lost");
            finishedItems++;
        });
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            var level = server.overworld();
            var target = new FixtureZombie(level);
            var dealer = new FixtureZombie(level);
            attacker = dealer;
            target.getAttribute(Attributes.ARMOR).setBaseValue(0);
            target.getAttribute(Attributes.MAX_ABSORPTION).setBaseValue(20);
            target.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0);
            var source = target.damageSources().generic();
            try {
                reset(target, Mode.PLAIN, 20);
                require(target.hurtServer(level, source, 4), "Plain server damage rejected");
                require(close(target.getHealth(), 16) && close(lastLost, 4), "Wrong plain health damage");
                require(incoming == 1 && modifications == 1 && healthStage == 1 && after == 1 && accepted == 1, "Damage callbacks ran more than once");
                require(target.immunityChecks == 2, "Hook added an immunity/enchantment check beyond vanilla's two stages");
                require(!target.hurtServer(level, source, 1) && after == 1 && accepted == 1, "Normal damage cooldown was bypassed or triggered post effects");

                reset(target, Mode.CANCEL, 20);
                require(!target.hurtServer(level, source, 4) && close(target.getHealth(), 20), "Cancellation did not prevent damage");
                require(modifications == 0 && after == 0 && accepted == 0, "Canceled hit reached later stages");

                reset(target, Mode.DOUBLE, 20);
                require(target.hurtServer(level, source, 3) && close(target.getHealth(), 14), "Incoming multiplier did not apply once: health=" + target.getHealth() + ", health stage=" + lastHealthAmount + ", lost=" + lastLost + ", modifiers=" + modifications);
                require(modifications == 1 && after == 1, "Modified damage recursively redispatched");

                reset(target, Mode.EFFECTS, 20);
                dealer.setHealth(5);
                target.setAbsorptionAmount(3);
                require(target.hurtServer(level, source, 5), "Absorbed hit rejected");
                require(close(lastHealthAmount, 2) && close(lastLost, 2), "Health stage ran before absorption");
                require(close(dealer.getHealth(), 6), "Lifesteal used incoming damage instead of health loss");
                require(target.getTicksFrozen() == 0 && target.getRemainingFireTicks() == 30, "Native ignition did not thaw freezing");
                target.clearFire();
                SpellHitEffects.apply(target, dealer, 1, 0, 7, 0);
                require(target.getTicksFrozen() == 14, "Freeze-only hit lost its duration");
                float healed = dealer.getHealth();
                SpellHitEffects.apply(target, dealer, -1, 10, 1, 30);
                require(dealer.getHealth() == healed && target.getTicksFrozen() == 14 && target.getRemainingFireTicks() <= 0, "Invalid health loss applied spell effects");
                reset(target, Mode.EFFECTS, 20);
                dealer.setHealth(5);
                target.setAbsorptionAmount(5);
                require(target.hurtServer(level, source, 3), "Fully absorbed hit was rejected");
                require(after == 1 && lastLost == 0 && close(target.getHealth(), 20) && close(dealer.getHealth(), 5), "Absorbed hit granted lifesteal or skipped health stage");
                require(target.getRemainingFireTicks() == 30, "Absorption incorrectly prevented the hit's fire effect");

                reset(target, Mode.EFFECTS, 20);
                dealer.setHealth(5);
                target.setPermanentlyInvulnerable(true);
                require(!target.hurtServer(level, source, 5), "Invulnerable hit accepted");
                require(after == 0 && accepted == 0 && close(dealer.getHealth(), 5), "Rejected hit applied post effects");

                reset(target, Mode.HEARTSTOP, 20);
                target.setAbsorptionAmount(3);
                require(target.hurtServer(level, source, 5), "Deferred hit should remain accepted");
                require(close(target.getHealth(), 20) && close(deferred, 1) && after == 1 && lastLost == 0, "Heartstop used pre-absorption damage or reported health loss");

                reset(target, Mode.KNOCKBACK, 20);
                target.setDeltaMovement(Vec3.ZERO);
                target.knockback(1, 1, 0, source, 1);
                require(target.getDeltaMovement().equals(Vec3.ZERO) && knocks == 1, "Knockback cancellation/overload dispatch failed");
                mode = Mode.PLAIN;
                target.knockback(1, 1, 0, source, 1);
                require(target.getDeltaMovement().lengthSqr() > 0 && knocks == 2, "Native knockback did not resume");

                reset(target, Mode.HALF_HEAL, 10);
                target.heal(4);
                require(close(target.getHealth(), 12), "Healing modifier did not apply");
                reset(target, Mode.INVALID, 20);
                require(!target.hurtServer(level, source, 2) && close(target.getHealth(), 20), "Non-finite modifier reached health");
                reset(target, Mode.PLAIN, 20);
                require(!target.hurtServer(level, source, Float.NaN) && incoming == 0, "Non-finite incoming damage reached callbacks");

                reset(target, Mode.IFRAMES, 20);
                require(target.hurtServer(level, source, 2) && target.hurtServer(level, source, 2), "Accepted-hit i-frame override failed");
                require(close(target.getHealth(), 16) && accepted == 2, "Repeated hits lost their native results");

                reset(target, Mode.PLAIN, 20);
                target.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.APPLE));
                target.startUsingItem(InteractionHand.MAIN_HAND);
                target.finishForProbe();
                require(finishedItems == 1, "Native finish-use callback did not fire once");

                reset(target, Mode.EFFECTS, 3);
                dealer.setHealth(5);
                require(target.hurtServer(level, source, 10), "Lethal hit rejected");
                require(after == 1 && close(lastLost, 3) && close(dealer.getHealth(), 6.5f), "Lethal damage skipped effects or granted overkill lifesteal");
                System.out.println("Native damage mixin checks passed: single dispatch, cancellation, modifiers, absorption/heartstop, real spell hit effects, lethal hits, knockback, healing, item snapshots and i-frames");
            } finally {
                target.discard(); dealer.discard();
            }
            server.halt(false);
        });
    }

    private void reset(FixtureZombie target, Mode next, float health) {
        mode = next;
        incoming = modifications = healthStage = after = accepted = knocks = 0;
        lastHealthAmount = lastLost = deferred = 0;
        target.setPermanentlyInvulnerable(false);
        target.immunityChecks = 0;
        target.setInvulnerableTime(0);
        target.damageCooldownTime = 0;
        target.setAbsorptionAmount(0);
        target.setHealth(health);
        target.setTicksFrozen(0);
        target.clearFire();
    }
    private static boolean close(float a, float b) { return Math.abs(a - b) < .001f; }
    private static void require(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
    private static final class FixtureZombie extends Zombie {
        int immunityChecks;
        FixtureZombie(ServerLevel level) { super(level); setNoAi(true); }
        @Override public boolean isInvulnerableTo(ServerLevel level, DamageSource source) {
            immunityChecks++;
            return super.isInvulnerableTo(level, source);
        }
        void finishForProbe() { completeUsingItem(); }
        @Override protected void dropAllDeathLoot(ServerLevel level, DamageSource source) { }
    }
}
