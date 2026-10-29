package io.redspace.ironsspellbooks.block.pedestal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

public class PedestalRenderer implements BlockEntityRenderer<PedestalTile, PedestalRenderer.State> {
    private final ItemModelResolver itemModels;

    public PedestalRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModels = context.itemModelResolver();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(PedestalTile tile, State state, float partialTick,
                                   Vec3 cameraPosition, CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(tile, state, partialTick, cameraPosition, breakProgress);
        var stack = tile.getHeldItem();
        itemModels.updateForTopItem(state.item, stack, ItemDisplayContext.FIXED, tile.getLevel(),
                null, (int) tile.getBlockPos().asLong());
        var player = Minecraft.getInstance().player;
        state.rotation = player == null ? 0 : player.tickCount * 2 + partialTick;
        state.tilt = stack.is(ItemTags.SWORDS) || stack.is(ItemTags.AXES) || stack.is(ItemTags.PICKAXES)
                || stack.is(ItemTags.SHOVELS) || stack.is(ItemTags.HOES);
    }

    @Override
    public void submit(State state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.item.isEmpty()) return;
        poses.pushPose();
        try {
            poses.translate(.5, 1.5, .5);
            poses.rotateDegrees(Axis.YP, state.rotation);
            if (state.tilt) poses.rotateDegrees(Axis.ZP, -45);
            poses.scale(.65f, .65f, .65f);
            state.item.submit(poses, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        } finally {
            poses.popPose();
        }
    }

    public static class State extends BlockEntityRenderState {
        public final ItemStackRenderState item = new ItemStackRenderState();
        public float rotation;
        public boolean tilt;
    }
}
