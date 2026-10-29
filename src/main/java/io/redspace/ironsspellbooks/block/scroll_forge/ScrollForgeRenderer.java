package io.redspace.ironsspellbooks.block.scroll_forge;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.item.InkItem;
import io.redspace.ironsspellbooks.util.ModTags;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public class ScrollForgeRenderer implements BlockEntityRenderer<ScrollForgeTile, ScrollForgeRenderer.State> {
    private static final Identifier PAPER_TEXTURE = Identifier.fromNamespaceAndPath("irons_spellbooks", "textures/block/scroll_forge_paper.png");
    private final ItemModelResolver itemModels;

    public ScrollForgeRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModels = context.itemModelResolver();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(ScrollForgeTile tile, State state, float partialTick,
                                   Vec3 cameraPosition, CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(tile, state, partialTick, cameraPosition, breakProgress);
        var ink = tile.getStackInSlot(0);
        var paper = tile.getStackInSlot(1);
        var focus = tile.getStackInSlot(2);
        int seed = (int) tile.getBlockPos().asLong();
        itemModels.updateForTopItem(state.ink, ink.getItem() instanceof InkItem ? ink : ItemStack.EMPTY,
                ItemDisplayContext.FIXED, tile.getLevel(), null, seed);
        itemModels.updateForTopItem(state.focus, focus.is(ModTags.SCHOOL_FOCUS) ? focus : ItemStack.EMPTY,
                ItemDisplayContext.FIXED, tile.getLevel(), null, seed);
        state.paper = paper.is(Items.PAPER);
        state.facing = tile.getBlockState().getValueOrElse(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH);
    }

    @Override
    public void submit(State state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        poses.pushPose();
        try {
            poses.translate(.5, .5, .5);
            poses.rotateDegrees(Axis.YP, switch (state.facing) {
                case NORTH -> 180;
                case EAST -> 90;
                case WEST -> -90;
                default -> 0;
            });
            poses.translate(-.5, -.5, -.5);
            submitItem(state.ink, .175, .25, 15, state.lightCoords, poses, collector);
            submitItem(state.focus, .75, .4, 5, state.lightCoords, poses, collector);
            if (state.paper) {
                poses.translate(.5, .876, .7);
                poses.rotateDegrees(Axis.YP, 85);
                poses.rotateDegrees(Axis.XP, 180);
                int light = state.lightCoords;
                collector.submitCustomGeometry(poses, RenderTypes.entityCutout(PAPER_TEXTURE),
                        (pose, consumer) -> drawPaper(pose, consumer, light));
            }
        } finally {
            poses.popPose();
        }
    }

    private static void submitItem(ItemStackRenderState item, double x, double z, float rotation,
                                   int light, PoseStack poses, SubmitNodeCollector collector) {
        if (item.isEmpty()) return;
        poses.pushPose();
        try {
            poses.translate(x, .876, z);
            poses.rotateDegrees(Axis.XP, -90);
            poses.rotateDegrees(Axis.YP, 180);
            poses.rotateDegrees(Axis.ZP, -rotation);
            poses.scale(.45f, .45f, .45f);
            item.submit(poses, collector, light, OverlayTexture.NO_OVERLAY, 0);
        } finally {
            poses.popPose();
        }
    }

    private static void drawPaper(PoseStack.Pose pose, VertexConsumer consumer, int light) {
        float half = .45f / 2;
        consumer.addVertex(pose.pose(), -half, 0, -half).setColor(-1).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0, -1, 0);
        consumer.addVertex(pose.pose(), half, 0, -half).setColor(-1).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0, -1, 0);
        consumer.addVertex(pose.pose(), half, 0, half).setColor(-1).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0, -1, 0);
        consumer.addVertex(pose.pose(), -half, 0, half).setColor(-1).setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0, -1, 0);
    }

    public static class State extends BlockEntityRenderState {
        public final ItemStackRenderState ink = new ItemStackRenderState();
        public final ItemStackRenderState focus = new ItemStackRenderState();
        public boolean paper;
        public Direction facing = Direction.SOUTH;
    }
}
