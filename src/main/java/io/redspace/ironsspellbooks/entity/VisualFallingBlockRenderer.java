package io.redspace.ironsspellbooks.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.FallingBlockRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;

/** Visual debris deliberately renders even when its block matches the block beneath it. */
public class VisualFallingBlockRenderer extends EntityRenderer<VisualFallingBlockEntity, FallingBlockRenderState> {
    public VisualFallingBlockRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = .5f;
    }

    @Override
    public FallingBlockRenderState createRenderState() {
        return new FallingBlockRenderState();
    }

    @Override
    public void extractRenderState(VisualFallingBlockEntity entity, FallingBlockRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        var block = state.movingBlockRenderState;
        BlockPos position = BlockPos.containing(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
        block.blockState = entity.getBlockState();
        block.blockPos = position;
        block.randomSeedPos = entity.getStartPos();
        if (entity.level() instanceof ClientLevel level) {
            block.biome = level.getBiome(position);
            block.cardinalLighting = level.cardinalLighting();
            block.lightEngine = level.getLightEngine();
        }
    }

    @Override
    public void submit(FallingBlockRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.movingBlockRenderState.blockState.getRenderShape() != RenderShape.MODEL) return;
        poses.pushPose();
        poses.translate(-.5, 0, -.5);
        collector.submitMovingBlock(poses, state.movingBlockRenderState, state.outlineColor);
        poses.popPose();
        super.submit(state, poses, collector, camera);
    }
}
