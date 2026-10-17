package net.neoforged.neoforge.event.entity;

import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.Event;

public class EntityMountEvent extends Event {
    private final Entity entityMounting;
    private final Entity entityBeingMounted;
    private final boolean mounting;

    public EntityMountEvent(Entity entityMounting, Entity entityBeingMounted, boolean mounting) {
        this.entityMounting = entityMounting;
        this.entityBeingMounted = entityBeingMounted;
        this.mounting = mounting;
    }

    public Entity getEntity() { return entityMounting; }
    public Entity getEntityMounting() { return entityMounting; }
    public Entity getEntityBeingMounted() { return entityBeingMounted; }
    public boolean isMounting() { return mounting; }
    public boolean isDismounting() { return !mounting; }
}
