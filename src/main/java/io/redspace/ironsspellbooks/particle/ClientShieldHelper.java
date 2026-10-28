package io.redspace.ironsspellbooks.particle;

import io.redspace.ironsspellbooks.config.ClientConfigs;
import io.redspace.ironsspellbooks.entity.spells.AbstractShieldEntity;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public class ClientShieldHelper {
    private static final ArrayList<AbstractShieldEntity> trackedEntities = new ArrayList<>();

    public static void init() {
        ClientEntityEvents.ENTITY_LOAD.register((entity, level) -> trackShieldCreated(entity));
        ClientEntityEvents.ENTITY_UNLOAD.register((entity, level) -> trackShieldRemoved(entity));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> clear());
    }

    private static synchronized void trackShieldCreated(Entity entity) {
        if (entity instanceof AbstractShieldEntity ase) {
            trackedEntities.add(ase);
        }
    }

    private static synchronized void trackShieldRemoved(Entity entity) {
        if (entity instanceof AbstractShieldEntity ase) {
            trackedEntities.remove(ase);
        }
    }

    private static synchronized void clear() {
        trackedEntities.clear();
    }

    public static synchronized List<VoxelShape> getShieldsFor(AABB boundingBox) {
        if (trackedEntities.isEmpty() || !ClientConfigs.safeGet(ClientConfigs.SHIELD_PARTICLE_COLLISIONS)) {
            return List.of();
        } else {
            List<VoxelShape> shieldCollisions = new ArrayList<>();
            for (var s : trackedEntities) {
                if (boundingBox.intersects(s.getBoundingBox().inflate(1))) shieldCollisions.addAll(s.getVoxels());
            }
            return shieldCollisions;
        }
    }
}
