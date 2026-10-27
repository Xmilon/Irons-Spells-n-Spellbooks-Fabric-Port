package io.redspace.ironsspellbooks.entity.spells.poison_arrow;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import io.redspace.ironsspellbooks.render.EntityBackedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class PoisonArrowRenderer extends EntityBackedRenderer<PoisonArrow> {
    //private static final Identifier TEXTURE = irons_spellbooks.id("textures/entity/icicle_projectile.png");
    private static final Identifier TEXTURE = IronsSpellbooks.id("textures/entity/arrow.png");

    public PoisonArrowRenderer(Context context) {
        super(context);
    }

    @Override
    protected void submit(PoisonArrow entity, float partialTicks, int light, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();

        Vec3 motion = entity.getDeltaMovement();
        float xRot = -((float) (Mth.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        float yRot = -((float) (Mth.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 90.0F);
        poseStack.rotateDegrees(Axis.YP, yRot);
        poseStack.rotateDegrees(Axis.XP, xRot);

        float f9 = entity.shakeTime - partialTicks;
        if (f9 > 0.0F) {
            float f10 = -Mth.sin(f9 * 3.0F) * f9;
            poseStack.rotateDegrees(Axis.XP, f10);
        }

        renderModel(poseStack, collector, light);
        poseStack.popPose();
    }

    public static void renderModel(PoseStack poseStack, SubmitNodeCollector collector, int light) {
        poseStack.scale(0.125f, 0.125f, 0.125f);

        //poseStack.rotate(camera.orientation);
        //poseStack.rotateDegrees(Axis.YP, 180f);

        poseStack.rotateDegrees(Axis.YP, 90.0F);
        poseStack.translate(-2, 0, 0);

        for (int j = 0; j < 4; ++j) {
            poseStack.rotateDegrees(Axis.XP, 90.0F);
            collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(getTextureLocation()), (pose, consumer) -> {
                vertex(pose.pose(), pose.normal(), consumer, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, light);
                vertex(pose.pose(), pose.normal(), consumer, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, light);
                vertex(pose.pose(), pose.normal(), consumer, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, light);
                vertex(pose.pose(), pose.normal(), consumer, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, light);
            });
        }
    }

    public static void vertex(Matrix4f pMatrix, Matrix3f pNormals, VertexConsumer pVertexBuilder, int pOffsetX, int pOffsetY, int pOffsetZ, float pTextureX, float pTextureY, int pNormalX, int p_113835_, int p_113836_, int pPackedLight) {
        pVertexBuilder.addVertex(pMatrix, (float) pOffsetX, (float) pOffsetY, (float) pOffsetZ).setColor(255, 255, 255, 255).setUv(pTextureX, pTextureY).setOverlay(OverlayTexture.NO_OVERLAY).setLight(pPackedLight).setNormal((float) pNormalX, (float) p_113836_, (float) p_113835_);
    }

    public Identifier getTextureLocation(PoisonArrow entity) {
        return getTextureLocation();
    }

    public static Identifier getTextureLocation() {
        return TEXTURE;
    }

}
