package io.redspace.ironsspellbooks.entity.spells.lightning_lance;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.util.LightCoordsUtil;
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

public class LightningLanceRenderer extends EntityBackedRenderer<LightningLanceProjectile> {
    private static final Identifier TEXTURE = IronsSpellbooks.id("textures/entity/lightning_lance/lightning_lance.png");

    public LightningLanceRenderer(Context context) {
        super(context);
    }

    @Override
    protected void submit(LightningLanceProjectile entity, float partialTicks, int light, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        Vec3 motion = entity.getDeltaMovement();
        float xRot = -((float) (Mth.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        float yRot = -((float) (Mth.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 90.0F);
        poseStack.rotateDegrees(Axis.YP, yRot);
        poseStack.rotateDegrees(Axis.XP, xRot);
        renderModel(poseStack, collector, entity.getAge());
        poseStack.popPose();
    }

    public static void renderModel(PoseStack poseStack, SubmitNodeCollector collector, int animOffset) {
        //poseStack.scale(0.0625f, 0.0625f, 0.0625f);

        //poseStack.rotate(camera.orientation);
        //poseStack.rotateDegrees(Axis.YP, 180f);

        int framecout = 7;
        int anim = animOffset % framecout;
        float uvMin = anim / (float) framecout;
        float uvMax = (anim + 1) / (float) framecout;

        float halfWidth = 2;
        float halfHeight = 1;
        float angleCorrection = 50;
        float texturefix = -.2f;
        //Vertical plane
        poseStack.rotateDegrees(Axis.XP, angleCorrection);
        collector.submitCustomGeometry(poseStack, RenderTypes.energySwirl(TEXTURE, 0, 0), (pose, consumer) -> {
            consumer.addVertex(pose.pose(), 0, -halfWidth, -halfHeight + texturefix).setColor(255, 255, 255, 255).setUv(uvMin, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
            consumer.addVertex(pose.pose(), 0, halfWidth, -halfHeight + texturefix).setColor(255, 255, 255, 255).setUv(uvMin, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
            consumer.addVertex(pose.pose(), 0, halfWidth, halfHeight + texturefix).setColor(255, 255, 255, 255).setUv(uvMax, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
            consumer.addVertex(pose.pose(), 0, -halfWidth, halfHeight + texturefix).setColor(255, 255, 255, 255).setUv(uvMax, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
        });
        poseStack.rotateDegrees(Axis.XP, -angleCorrection);

        //Horizontal plane
        poseStack.rotateDegrees(Axis.YP, -angleCorrection);
        collector.submitCustomGeometry(poseStack, RenderTypes.energySwirl(TEXTURE, 0, 0), (pose, consumer) -> {
            consumer.addVertex(pose.pose(), -halfWidth - texturefix, 0, -halfHeight).setColor(255, 255, 255, 255).setUv(uvMin, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
            consumer.addVertex(pose.pose(), halfWidth - texturefix, 0, -halfHeight).setColor(255, 255, 255, 255).setUv(uvMin, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
            consumer.addVertex(pose.pose(), halfWidth - texturefix, 0, halfHeight).setColor(255, 255, 255, 255).setUv(uvMax, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
            consumer.addVertex(pose.pose(), -halfWidth - texturefix, 0, halfHeight).setColor(255, 255, 255, 255).setUv(uvMax, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
        });
        poseStack.rotateDegrees(Axis.YP, angleCorrection);
    }

    public Identifier getTextureLocation(LightningLanceProjectile entity) {
        return TEXTURE;
    }
}
