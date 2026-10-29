package io.redspace.ironsspellbooks.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/** Native extension points at the incoming, health-loss and accepted-hit stages. */
public final class LivingDamageEvents {
    public static final Event<AllowDamage> ALLOW_DAMAGE = EventFactory.createArrayBacked(AllowDamage.class, listeners -> (level, target, source, amount) -> {
        for (var listener : listeners) if (!listener.allow(level, target, source, amount)) return false;
        return true;
    });
    public static final Event<DamageModifier> MODIFY_DAMAGE = modifierEvent();
    public static final Event<DamageModifier> MODIFY_HEALTH_DAMAGE = modifierEvent();
    /** Positive unblocked hits after health calculation; health loss can be zero after absorption/deferral. */
    public static final Event<AfterDamage> AFTER_DAMAGE = EventFactory.createArrayBacked(AfterDamage.class, listeners -> (level, target, source, amount) -> {
        for (var listener : listeners) listener.afterDamage(level, target, source, amount);
    });
    /** Accepted server hits; amount follows native hit processing and is not health loss. */
    public static final Event<AfterDamage> AFTER_HIT = EventFactory.createArrayBacked(AfterDamage.class, listeners -> (level, target, source, amount) -> {
        for (var listener : listeners) listener.afterDamage(level, target, source, amount);
    });
    public static final Event<AllowKnockback> ALLOW_KNOCKBACK = EventFactory.createArrayBacked(AllowKnockback.class, listeners -> (target, source) -> {
        for (var listener : listeners) if (!listener.allow(target, source)) return false;
        return true;
    });
    public static final Event<HealingModifier> MODIFY_HEALING = EventFactory.createArrayBacked(HealingModifier.class, listeners -> (target, amount) -> {
        for (var listener : listeners) amount = listener.modify(target, amount);
        return amount;
    });
    public static final Event<FinishUsingItem> FINISH_USING_ITEM = EventFactory.createArrayBacked(FinishUsingItem.class, listeners -> (target, item) -> {
        for (var listener : listeners) listener.finish(target, item);
    });

    private static Event<DamageModifier> modifierEvent() {
        return EventFactory.createArrayBacked(DamageModifier.class, listeners -> (level, target, source, amount) -> {
            for (var listener : listeners) amount = listener.modify(level, target, source, amount);
            return amount;
        });
    }

    @FunctionalInterface public interface AllowDamage { boolean allow(ServerLevel level, LivingEntity target, DamageSource source, float amount); }
    @FunctionalInterface public interface DamageModifier { float modify(ServerLevel level, LivingEntity target, DamageSource source, float amount); }
    @FunctionalInterface public interface AfterDamage { void afterDamage(ServerLevel level, LivingEntity target, DamageSource source, float amount); }
    @FunctionalInterface public interface AllowKnockback { boolean allow(LivingEntity target, DamageSource source); }
    @FunctionalInterface public interface HealingModifier { float modify(LivingEntity target, float amount); }
    @FunctionalInterface public interface FinishUsingItem { void finish(LivingEntity target, ItemStack item); }

    private LivingDamageEvents() { }
}
