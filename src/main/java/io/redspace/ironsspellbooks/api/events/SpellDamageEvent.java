package io.redspace.ironsspellbooks.api.events;

import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;

/** Fired by the spell dispatcher before resistance and native server damage. */
public final class SpellDamageEvent {
    public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, listeners -> event -> {
        for (var listener : listeners) {
            listener.onDamage(event);
            if (event.isCanceled()) return;
        }
    });
    private final LivingEntity entity;
    private final SpellDamageSource source;
    private final float originalAmount;
    private float amount;
    private boolean canceled;

    public SpellDamageEvent(LivingEntity entity, float amount, SpellDamageSource source) {
        this.entity = entity;
        this.source = source;
        this.originalAmount = amount;
        this.amount = amount;
    }
    public LivingEntity getEntity() { return entity; }
    public float getOriginalAmount() { return originalAmount; }
    public float getAmount() { return amount; }
    public void setAmount(float amount) { this.amount = amount; }
    public SpellDamageSource getSpellDamageSource() { return source; }
    public boolean isCanceled() { return canceled; }
    public void setCanceled(boolean canceled) { this.canceled = canceled; }
    @FunctionalInterface public interface Callback { void onDamage(SpellDamageEvent event); }
}
