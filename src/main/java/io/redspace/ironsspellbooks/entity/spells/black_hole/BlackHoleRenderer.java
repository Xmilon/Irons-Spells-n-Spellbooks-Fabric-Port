package io.redspace.ironsspellbooks.entity.spells.black_hole;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.spells.icicle.IcicleRenderer;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import io.redspace.ironsspellbooks.render.EntityBackedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BlackHoleRenderer extends EntityBackedRenderer<BlackHole> {
    public BlackHoleRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    private static final Identifier CENTER_TEXTURE = IronsSpellbooks.id("textures/entity/black_hole/black_hole.png");
    private static final Identifier BEAM_TEXTURE = IronsSpellbooks.id("textures/entity/black_hole/beam.png");

    @Override
    protected void submit(BlackHole entity, float partialTicks, int pPackedLight, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0, entity.getBoundingBox().getYsize() / 2, 0);

        float entityScale = entity.getBbWidth() * .025f;
        Vec3 normalToCamera = camera.pos.subtract(entity.getBoundingBox().getCenter()).normalize().scale(2);
        poseStack.translate(normalToCamera.x, normalToCamera.y, normalToCamera.z);
        poseStack.scale(.5f * entityScale, .5f * entityScale, .5f * entityScale);
        poseStack.rotate(camera.orientation);
        poseStack.rotateDegrees(Axis.YP, 90f);
//        poseStack.translate(5, 0, 0);


        float centerScale = 3;
        collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(CENTER_TEXTURE), (pose, consumer) -> {
            consumer.addVertex(pose.pose(), 0, -centerScale, -centerScale).setColor(255, 255, 255, 255).setUv(0f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
            consumer.addVertex(pose.pose(), 0, centerScale, -centerScale).setColor(255, 255, 255, 255).setUv(0f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
            consumer.addVertex(pose.pose(), 0, centerScale, centerScale).setColor(255, 255, 255, 255).setUv(1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
            consumer.addVertex(pose.pose(), 0, -centerScale, centerScale).setColor(255, 255, 255, 255).setUv(1f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
        });
        poseStack.popPose();
        poseStack.pushPose();

        poseStack.translate(0, entity.getBoundingBox().getYsize() / 2, 0);
        float animationProgress = (entity.tickCount + partialTicks) / 200.0F;
        //float fadeProgress = Math.min(animationProgress > 0.8F ? (animationProgress - 0.8F) / 0.2F : 0.0F, 1.0F);
        float fadeProgress = .5f;
        RandomSource randomSource = RandomSource.create(432L);
//        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderTypes.lightning());
        //poseStack.translate(0.0D, -1.0D, -2.0D);

        float segments = Math.min(animationProgress, .8f);
        for (int i = 0; (float) i < (segments + segments * segments) / 2.0F * 40.0F; ++i) {
            poseStack.rotateDegrees(Axis.XP, randomSource.nextFloat() * 360.0F);
            poseStack.rotateDegrees(Axis.YP, randomSource.nextFloat() * 360.0F);
            poseStack.rotateDegrees(Axis.ZP, randomSource.nextFloat() * 360.0F);
            poseStack.rotateDegrees(Axis.XP, randomSource.nextFloat() * 360.0F);
            poseStack.rotateDegrees(Axis.YP, randomSource.nextFloat() * 360.0F);
            poseStack.rotateDegrees(Axis.ZP, randomSource.nextFloat() * 360.0F + animationProgress * 90.0F);
            float size1 = (randomSource.nextFloat() * 10.0F + 5.0F + fadeProgress * 5.0F) * entityScale * .4f;
            //float size2 = randomSource.nextFloat() * 2.0F + 1.0F + fadeProgress * 2.0F;
            collector.submitCustomGeometry(poseStack, RenderTypes.energySwirl(BEAM_TEXTURE, 0, 0), (pose, vertexConsumer) -> drawTriangle(vertexConsumer, pose, size1));
        }

        poseStack.popPose();
    }

    public Identifier getTextureLocation(BlackHole pEntity) {
        return IcicleRenderer.TEXTURE;
    }

    private static final float HALF_SQRT_3 = (float) (Math.sqrt(3.0D) / 2.0D);

    private static void drawTriangle(VertexConsumer consumer, PoseStack.Pose pose, float size) {
        consumer.addVertex(pose.pose(), 0, 0, 0).setColor(255, 0, 255, 255).setUv(0f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
        consumer.addVertex(pose.pose(), 0, 3 * size, -1 * size).setColor(0, 0, 0, 0).setUv(0f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
        consumer.addVertex(pose.pose(), 0, 3 * size, 1 * size).setColor(0, 0, 0, 0).setUv(1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
        consumer.addVertex(pose.pose(), 0, 0, 0).setColor(255, 0, 255, 255).setUv(1f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
    }

}
