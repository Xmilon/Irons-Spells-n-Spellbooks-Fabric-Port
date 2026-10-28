package io.redspace.ironsspellbooks.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.particle.v1.ParticleGroupRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.ParticleGroupRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class FallingBlockParticle extends Particle {
    public static final ParticleRenderType GROUP = new ParticleRenderType("irons_spellbooks:falling_block", "ISSB_BLOCK");
    private final BlockState blockState;

    public static void registerGroup() {
        ParticleGroupRegistry.register(GROUP, Group::new);
        ParticleGroupRegistry.registerOrdering(ParticleRenderType.SINGLE_QUADS, GROUP);
    }

    FallingBlockParticle(ClientLevel level, double x, double y, double z, FallingBlockParticleOption options) {
        super(level, x, y, z, 0, 0, 0);
        this.xd = options.getMotion().x;
        this.yd = options.getMotion().y;
        this.zd = options.getMotion().z;
        this.lifetime = 200;
        this.blockState = options.getState();
        this.gravity = 0.08f;
    }

    @Override
    public void tick() {
        boolean wasOnGround = this.onGround;
        age++;
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        move(xd, yd, zd);
        yd -= gravity;
        if (this.blockState.isAir() || wasOnGround || age > lifetime) {
            this.remove();
        }
    }

    @Override
    public ParticleRenderType getGroup() {
        return GROUP;
    }

    public static class Provider implements ParticleProvider<FallingBlockParticleOption> {
        @Override
        public Particle createParticle(FallingBlockParticleOption options, ClientLevel level,
                                       double x, double y, double z, double xd, double yd, double zd, RandomSource random) {
            return new FallingBlockParticle(level, x, y, z, options);
        }
    }

    public record BlockInstance(Vec3 relativePosition, BlockModelRenderState model) {
    }

    public record RenderState(List<BlockInstance> blocks) implements ParticleGroupRenderState {
        public RenderState {
            blocks = List.copyOf(blocks);
        }

        @Override
        public void submit(SubmitNodeCollector collector, CameraRenderState camera) {
            var poses = new PoseStack();
            for (var block : blocks) {
                poses.pushPose();
                try {
                    Vec3 position = block.relativePosition();
                    poses.translate(position.x, position.y, position.z);
                    block.model().submit(poses, collector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
                } finally {
                    poses.popPose();
                }
            }
        }
    }

    private static class Group extends ParticleGroup<FallingBlockParticle> {
        private final BlockModelResolver modelResolver;
        private final BlockDisplayContext displayContext = BlockDisplayContext.create();

        private Group(ParticleEngine engine) {
            super(engine);
            modelResolver = new BlockModelResolver(Minecraft.getInstance().getModelManager());
        }

        @Override
        public ParticleGroupRenderState extractRenderState(Frustum frustum, Camera camera, float partialTick) {
            var blocks = new ArrayList<BlockInstance>();
            Vec3 cameraPosition = camera.position();
            for (var particle : particles) {
                if (particle.blockState.getRenderShape() != RenderShape.MODEL) continue;
                double x = Mth.lerp(partialTick, particle.xo, particle.x);
                double y = Mth.lerp(partialTick, particle.yo, particle.y);
                double z = Mth.lerp(partialTick, particle.zo, particle.z);
                if (!frustum.isVisible(new AABB(x, y, z, x + 1, y + 1, z + 1))) continue;
                var model = new BlockModelRenderState();
                modelResolver.update(model, particle.blockState, displayContext);
                blocks.add(new BlockInstance(new Vec3(x, y, z).subtract(cameraPosition), model));
            }
            return new RenderState(blocks);
        }
    }
}
