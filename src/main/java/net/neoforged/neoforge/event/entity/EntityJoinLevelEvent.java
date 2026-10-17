package net.neoforged.neoforge.event.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;

public class EntityJoinLevelEvent extends Event {
    private final Entity entity;
    private final Level level;
    private final boolean loadedFromDisk;

    public EntityJoinLevelEvent(Entity entity, Level level) {
        // Fabric reports no difference between spawned and loaded entities; a nonzero age means it was saved.
        this(entity, level, entity.tickCount > 0);
    }

    public EntityJoinLevelEvent(Entity entity, Level level, boolean loadedFromDisk) {
        this.entity = entity;
        this.level = level;
        this.loadedFromDisk = loadedFromDisk;
    }

    public Entity getEntity(){ return entity; }
    public Level getLevel(){ return level; }
    public boolean loadedFromDisk() { return loadedFromDisk; }
}
