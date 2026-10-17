package net.neoforged.neoforge.event.entity.player;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;

public class CriticalHitEvent extends Event {
    private final Player player;
    private final Entity target;
    private float damageMultiplier;
    private boolean criticalHit;

    public CriticalHitEvent(Player player, Entity target, float damageMultiplier, boolean criticalHit) {
        this.player = player;
        this.target = target;
        this.damageMultiplier = damageMultiplier;
        this.criticalHit = criticalHit;
    }

    public Player getEntity() { return player; }
    public Entity getTarget() { return target; }
    public float getDamageMultiplier() { return damageMultiplier; }
    public void setDamageMultiplier(float damageMultiplier) { this.damageMultiplier = damageMultiplier; }
    public boolean isCriticalHit() { return criticalHit; }
    public void setCriticalHit(boolean criticalHit) { this.criticalHit = criticalHit; }
}
