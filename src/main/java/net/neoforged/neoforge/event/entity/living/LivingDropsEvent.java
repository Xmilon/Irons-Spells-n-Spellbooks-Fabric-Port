package net.neoforged.neoforge.event.entity.living;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.bus.api.Event;

import java.util.ArrayList;
import java.util.Collection;

public class LivingDropsEvent extends Event {
    private final LivingEntity entity;
    private final DamageSource source;
    // Fabric exposes no drop list; handlers can edit this but it is not applied to the real drops.
    private final Collection<ItemEntity> drops = new ArrayList<>();

    public LivingDropsEvent(LivingEntity entity, DamageSource source) {
        this.entity = entity;
        this.source = source;
    }

    public LivingEntity getEntity() { return entity; }
    public DamageSource getSource() { return source; }
    public Collection<ItemEntity> getDrops() { return drops; }
}
