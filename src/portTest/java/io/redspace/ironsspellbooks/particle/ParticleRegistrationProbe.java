package io.redspace.ironsspellbooks.particle;

import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.HashSet;

/** Isolated Fabric test-mod entrypoint; not included in the Spellbooks release JAR. */
public final class ParticleRegistrationProbe implements ModInitializer {
    @Override
    public void onInitialize() {
        ParticleRegistry.register();
        var ids = new HashSet<String>();
        try {
            for (var field : ParticleRegistry.class.getFields()) {
                if (!ParticleType.class.isAssignableFrom(field.getType())) continue;
                var particle = (ParticleType<?>) field.get(null);
                var id = BuiltInRegistries.PARTICLE_TYPE.getKey(particle);
                if (id == null || !id.getNamespace().equals("irons_spellbooks") || !ids.add(id.toString())) {
                    throw new AssertionError("Missing or duplicate particle registration: " + field.getName());
                }
                if (particle.codec() == null || particle.streamCodec() == null) {
                    throw new AssertionError("Missing particle codecs: " + id);
                }
            }
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }
        if (ids.size() != 33) throw new AssertionError("Expected 33 particle types, got " + ids.size());
        org.slf4j.LoggerFactory.getLogger("ParticlePortProbe").info("All 33 native particle registrations and codecs initialized");
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            org.slf4j.LoggerFactory.getLogger("ParticlePortProbe").info("Particle registry dedicated-server startup passed");
            server.halt(false);
        });
    }
}
