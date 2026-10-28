package io.redspace.ironsspellbooks.particle;

import net.fabricmc.fabric.api.client.particle.v1.ParticleGroupRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.ParticleGroupRenderState;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ARGB;
import org.joml.Quaternionf;

/** Particle meshes are captured here; the renderer consumes only their extracted vertices. */
public abstract class MeshParticle extends SingleQuadParticle {
    private static final ParticleRenderType GROUP = new ParticleRenderType("irons_spellbooks:meshes", "ISB");

    protected MeshParticle(ClientLevel level, double x, double y, double z,
                           double dx, double dy, double dz, TextureAtlasSprite sprite) {
        super(level, x, y, z, dx, dy, dz, sprite);
    }

    public static void registerGroup() {
        ParticleGroupRegistry.register(GROUP, Group::new);
        ParticleGroupRegistry.registerOrdering(ParticleRenderType.SINGLE_QUADS, GROUP);
    }

    @Override
    public final ParticleRenderType getGroup() {
        return GROUP;
    }

    public void extractMesh(ParticleMeshRenderState state, Camera camera, float partialTick) {
        extract(state, camera, partialTick);
    }

    protected void extractGroundQuad(QuadParticleRenderState state, Quaternionf rotation,
                                     float x, float y, float z, float partialTick) {
        // The original ground meshes map U1 to negative X; vanilla's quad maps it to positive X.
        state.add(getLayer(), x, y, z, rotation.x, rotation.y, rotation.z, rotation.w,
                getQuadSize(partialTick), getU1(), getU0(), getV0(), getV1(),
                ARGB.colorFromFloat(alpha, rCol, gCol, bCol), getLightCoords(partialTick));
    }

    private static class Group extends ParticleGroup<MeshParticle> {
        private Group(ParticleEngine engine) {
            super(engine);
        }

        @Override
        public ParticleGroupRenderState extractRenderState(Frustum frustum, Camera camera, float partialTick) {
            var state = new ParticleMeshRenderState();
            // Beam and ground-effect geometry can be visible when its particle origin is offscreen.
            for (var particle : particles) particle.extractMesh(state, camera, partialTick);
            return state;
        }
    }
}
