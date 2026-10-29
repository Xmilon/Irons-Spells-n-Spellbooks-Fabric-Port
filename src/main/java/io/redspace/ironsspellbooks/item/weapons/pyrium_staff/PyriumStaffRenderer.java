package io.redspace.ironsspellbooks.item.weapons.pyrium_staff;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public final class PyriumStaffRenderer implements SpecialModelRenderer<PyriumStaffRenderer.AnimationState> {
    private final PyriumStaffHeadModel head;
    private final PyriumStaffOrbModel orb;

    public PyriumStaffRenderer(EntityModelSet models) {
        this(new PyriumStaffHeadModel(models.bakeLayer(PyriumStaffHeadModel.LAYER_LOCATION)),
                new PyriumStaffOrbModel(models.bakeLayer(PyriumStaffOrbModel.LAYER_LOCATION)));
    }

    public PyriumStaffRenderer(PyriumStaffHeadModel head, PyriumStaffOrbModel orb) {
        this.head = head;
        this.orb = orb;
    }

    @Override
    public AnimationState extractArgument(ItemStack item) {
        var client = Minecraft.getInstance();
        float phase = client.player == null ? 0 : (client.player.tickCount
                + client.getDeltaTracker().getGameTimeDeltaPartialTick(true)) * .75f;
        return new AnimationState(phase);
    }

    @Override
    public void submit(AnimationState state, PoseStack poses, SubmitNodeCollector collector,
                       int light, int overlay, boolean foil, int outlineColor) {
        poses.pushPose();
        try {
            positionHead(poses);
            collector.submitModel(head, null, poses,
                    foil ? RenderTypes.itemCutoutGlint(PyriumStaffHeadModel.TEXTURE)
                            : head.renderType(PyriumStaffHeadModel.TEXTURE), light, overlay, -1, null, outlineColor);
            poses.translate(0, -9.5 / 32f, 0);
            float phase = state.phase;
            float scale = (Mth.sin(phase * .5f) + Mth.sin(3 * phase)) * .02f + 1;
            poses.translate(0, Mth.sin(phase * .3f) / 32f, 0);
            poses.scale(scale, scale, scale);
            collector.submitModel(orb, null, poses, orb.renderType(PyriumStaffOrbModel.TEXTURE),
                    LightCoordsUtil.FULL_BRIGHT, overlay, -1, null, outlineColor);
        } finally {
            poses.popPose();
        }
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        var poses = new PoseStack();
        positionHead(poses);
        head.root().getExtentsForGui(poses, output);
        poses.translate(0, -9.5 / 32f, 0);
        orb.root().getExtentsForGui(poses, output);
    }

    private static void positionHead(PoseStack poses) {
        poses.translate(.5, .5, .5);
        poses.rotateDegrees(Axis.ZP, 135);
        poses.rotateDegrees(Axis.YP, -90);
        poses.translate(0, -6.375 / 16f, 0);
        poses.scale(.5f, .5f, .5f);
    }

    public record AnimationState(float phase) { }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<AnimationState> {
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(new Unbaked());
        @Override public MapCodec<Unbaked> type() { return CODEC; }
        @Override public SpecialModelRenderer<AnimationState> bake(BakingContext context) {
            return new PyriumStaffRenderer(context.entityModelSet());
        }
    }
}
