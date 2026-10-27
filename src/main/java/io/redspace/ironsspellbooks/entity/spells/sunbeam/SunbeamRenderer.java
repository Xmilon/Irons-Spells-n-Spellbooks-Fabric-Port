package io.redspace.ironsspellbooks.entity.spells.sunbeam;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.render.RenderHelper;
import io.redspace.ironsspellbooks.render.SpellRenderingHelper;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.culling.Frustum;
import io.redspace.ironsspellbooks.render.EntityBackedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class SunbeamRenderer extends EntityBackedRenderer<SunbeamEntity> {

    public SunbeamRenderer(Context context) {
        super(context);
    }


    @Override
    public boolean shouldRender(SunbeamEntity pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ, float partialTick) {
        return true;
    }

    @Override
    protected void submit(SunbeamEntity entity, float partialTicks, int light, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();

        //poseStack.rotateDegrees(Axis.XP, 90);
        float maxRadius = 2.5f;
        float minRadius = 0.005f;
        float deltaTicks = entity.tickCount + partialTicks;
        float deltaUV = -deltaTicks % 10;
        float max = Mth.frac(deltaUV * 0.2F - (float) Mth.floor(deltaUV * 0.1F));
        float min = -1.0F + max;
        float f = deltaTicks / SunbeamEntity.WARMUP_TIME;
        f *= f;
        float radius = Mth.clampedLerp(maxRadius, minRadius, f);
        float halfRadius = radius * .5f;
        float quarterRadius = halfRadius * .5f;
        float yMin = entity.onGround() ? 0 : Utils.findRelativeGroundLevel(entity.level(), entity.position(), 8) - (float) entity.getY();
        collector.submitCustomGeometry(poseStack, RenderHelper.CustomerRenderType.magic(SpellRenderingHelper.BEACON), (pose, inner) -> {
        Matrix4f matrix = new Matrix4f(pose.pose());
        for (int i = 0; i < 4; i++) {
            //orange glow
            RenderHelper.quadBuilder()
                    .vertex(-halfRadius, yMin, -halfRadius).uv(0, min).normal(0, 1, 0)
                    .vertex(-halfRadius, yMin, halfRadius).uv(1, min).normal(0, 1, 0)
                    .vertex(-halfRadius, 250, halfRadius).uv(1, max).normal(0, 1, 0)
                    .vertex(-halfRadius, 250, -halfRadius).uv(0, max).normal(0, 1, 0)
                    .color(Mth.clamp(.8f * f, 0, 1), Mth.clamp(.8f * f * f, 0, 1), Mth.clamp(.5f * f * f, 0, 1))
                    .light(LightCoordsUtil.FULL_BRIGHT)
                    .overlay(OverlayTexture.NO_OVERLAY)
                    .matrix(matrix)
                    .build(inner);
            //yellow core
            RenderHelper.quadBuilder()
                    .vertex(-quarterRadius, yMin, -quarterRadius).uv(0, min).normal(0, 1, 0)
                    .vertex(-quarterRadius, yMin, quarterRadius).uv(1, min).normal(0, 1, 0)
                    .vertex(-quarterRadius, 250, quarterRadius).uv(1, max).normal(0, 1, 0)
                    .vertex(-quarterRadius, 250, -quarterRadius).uv(0, max).normal(0, 1, 0)
                    .color(Mth.clamp(1f * f, 0, 1), Mth.clamp(.85f * f, 0, 1), Mth.clamp(.7f * f * f, 0, 1))
                    .light(LightCoordsUtil.FULL_BRIGHT)
                    .overlay(OverlayTexture.NO_OVERLAY)
                    .matrix(matrix)
                    .build(inner);
            matrix.rotateY(Mth.HALF_PI);
        }
        });

        poseStack.popPose();
    }

    public Identifier getTextureLocation(SunbeamEntity entity) {
        return SpellRenderingHelper.BEACON;
    }

}
