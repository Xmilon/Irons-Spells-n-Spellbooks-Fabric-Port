package io.redspace.ironsspellbooks.entity.spells.echoing_strikes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.entity.spells.magic_arrow.MagicArrowRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import io.redspace.ironsspellbooks.render.EntityBackedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class EchoingMagicArrowRenderer extends EntityBackedRenderer<EchoingArrowProjectile> {
    private static final float MODEL_SCALE = 0.065f;

    public EchoingMagicArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void submit(EchoingArrowProjectile entity, float partialTicks, int light, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (entity.tickCount < EchoingArrowProjectile.SPAWN_DELAY) {
            return;
        }

        poseStack.pushPose();
        Vec3 motion = entity.getDeltaMovement();
        float xRot = -((float) (Mth.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        float yRot = -((float) (Mth.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 90.0F);
        poseStack.rotateDegrees(Axis.YP, yRot);
        poseStack.rotateDegrees(Axis.XP, xRot);
        poseStack.scale(MODEL_SCALE / 0.065f, MODEL_SCALE / 0.065f, MODEL_SCALE / 0.065f);
        MagicArrowRenderer.renderModel(poseStack, collector);
        poseStack.popPose();
    }

    public Identifier getTextureLocation(EchoingArrowProjectile entity) {
        return MagicArrowRenderer.getTextureLocation();
    }
}
