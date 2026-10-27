package io.redspace.ironsspellbooks.entity.spells.small_magic_arrow;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.render.RenderHelper;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import io.redspace.ironsspellbooks.render.EntityBackedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class SmallMagicArrowRenderer extends EntityBackedRenderer<SmallMagicArrow> {
    private static final Identifier TEXTURE = IronsSpellbooks.id("textures/entity/small_magic_arrow.png");

    public SmallMagicArrowRenderer(Context context) {
        super(context);
    }

    @Override
    protected void submit(SmallMagicArrow entity, float partialTicks, int light, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();

        Vec3 motion = entity.deltaMovementOld.add(entity.getDeltaMovement().subtract(entity.deltaMovementOld).scale(partialTicks));
        float xRot = -((float) (Mth.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        float yRot = -((float) (Mth.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 90.0F);
        poseStack.rotateDegrees(Axis.YP, yRot);
        poseStack.rotateDegrees(Axis.XP, xRot);

        float f9 = entity.shakeTime - partialTicks;
        if (f9 > 0.0F) {
            float f10 = -Mth.sin(f9 * 3.0F) * f9;
            poseStack.rotateDegrees(Axis.XP, f10);
        }

        renderModel(poseStack, collector);
        poseStack.popPose();
    }

    public static void renderModel(PoseStack poseStack, SubmitNodeCollector collector) {
        poseStack.scale(0.05625F, 0.05625F, 0.05625F);

        poseStack.rotateDegrees(Axis.YP, 90.0F);
        poseStack.translate(-2, 0, 0);

        for (int j = 0; j < 4; ++j) {
            poseStack.rotateDegrees(Axis.XP, 90.0F);
            collector.submitCustomGeometry(poseStack, RenderHelper.CustomerRenderType.magic(getTextureLocation()), (pose, consumer) -> {
                vertex(pose.pose(), pose.normal(), consumer, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, LightCoordsUtil.FULL_BRIGHT);
                vertex(pose.pose(), pose.normal(), consumer, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, LightCoordsUtil.FULL_BRIGHT);
                vertex(pose.pose(), pose.normal(), consumer, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, LightCoordsUtil.FULL_BRIGHT);
                vertex(pose.pose(), pose.normal(), consumer, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, LightCoordsUtil.FULL_BRIGHT);
            });
        }
    }

    public static void vertex(Matrix4f pMatrix, Matrix3f pNormals, VertexConsumer pVertexBuilder, int pOffsetX, int pOffsetY, int pOffsetZ, float pTextureX, float pTextureY, int pNormalX, int p_113835_, int p_113836_, int pPackedLight) {
        pVertexBuilder.addVertex(pMatrix, (float) pOffsetX, (float) pOffsetY, (float) pOffsetZ).setColor(255, 255, 255, 255).setUv(pTextureX, pTextureY).setOverlay(OverlayTexture.NO_OVERLAY).setLight(pPackedLight).setNormal((float) pNormalX, (float) p_113836_, (float) p_113835_);
    }

    public Identifier getTextureLocation(SmallMagicArrow entity) {
        return getTextureLocation();
    }

    public static Identifier getTextureLocation() {
        return TEXTURE;
    }

}
