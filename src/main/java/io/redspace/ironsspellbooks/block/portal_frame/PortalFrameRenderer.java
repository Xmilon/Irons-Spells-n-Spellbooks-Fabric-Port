package io.redspace.ironsspellbooks.block.portal_frame;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.entity.spells.portal.PortalRenderer;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;

public class PortalFrameRenderer implements BlockEntityRenderer<PortalFrameBlockEntity, PortalFrameRenderer.PortalFrameRenderState> {
    public PortalFrameRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public PortalFrameRenderState createRenderState() {
        return new PortalFrameRenderState();
    }

    @Override
    public void extractRenderState(PortalFrameBlockEntity blockEntity, PortalFrameRenderState state,
                                   float partialTick, Vec3 cameraPosition, CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPosition, breakProgress);
        var blockState = blockEntity.getBlockState();
        state.visible = blockEntity.clientIsConnected
                && blockState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER;
        state.direction = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        state.portalType = blockState.is(BlockRegistry.POCKET_PORTAL_FRAME.get())
                ? PortalRenderer.POCKET_DIMENSION : PortalRenderer.NORMAL;
        state.animationTick = blockEntity.getLevel() == null ? 0 : (int) blockEntity.getLevel().getGameTime();
        state.partialTick = partialTick;
        state.color = blockEntity.getColor();
    }

    @Override
    public void submit(PortalFrameRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!state.visible) return;
        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        var normal = state.direction.getUnitVec3i();
        poseStack.translate(normal.getX() * -(6.5 / 16f), 0, normal.getZ() * -(6.5 / 16f));
        if (state.direction == Direction.EAST || state.direction == Direction.WEST) {
            poseStack.rotateDegrees(Axis.YP, 90);
        }
        PortalRenderer.renderPortal(state.portalType, poseStack, collector, state.animationTick, state.partialTick, true, state.color);
        poseStack.popPose();
    }

    public static class PortalFrameRenderState extends BlockEntityRenderState {
        public boolean visible;
        public Direction direction;
        public PortalRenderer.PortalType portalType;
        public int animationTick;
        public float partialTick;
        public int color;
    }
}
