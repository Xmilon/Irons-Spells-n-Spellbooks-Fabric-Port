package io.redspace.ironsspellbooks.entity.spells.electrocute;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.render.RenderHelper;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ElectrocuteRenderer extends EntityRenderer<ElectrocuteProjectile, ElectrocuteRenderer.ElectrocuteRenderState> {
    private static final Identifier SOLID = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/electric_beams/solid.png");

    public ElectrocuteRenderer(Context context) {
        super(context);
    }

    @Override
    public ElectrocuteRenderState createRenderState() {
        return new ElectrocuteRenderState();
    }

    @Override
    public void extractRenderState(ElectrocuteProjectile entity, ElectrocuteRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        var owner = entity.getOwner();
        if (owner == null) {
            state.captureBeam(0, 0, 0, List.of());
        } else {
            state.captureBeam(owner.getYRot(), owner.getXRot(), entity.getEyeHeight() * .5f, entity.getBeamCache());
        }
    }

    @Override
    public void submit(ElectrocuteRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        super.submit(state, poseStack, collector, camera);
        if (state.segments.isEmpty()) return;
        poseStack.pushPose();
        poseStack.translate(0, state.eyeOffset, 0);
        poseStack.rotateDegrees(Axis.YP, -state.yaw);
        poseStack.rotateDegrees(Axis.XP, state.pitch);
        poseStack.translate(0, 0, 0.1);


        List<Vec3> segments = state.segments;

        float width = .3f;
        float height = width;
        for (int i = 0; i < segments.size() - 1; i += 2) {
            var from = segments.get(i);
            var to = segments.get(i + 1);
            collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucentEmissive(SOLID), (pose, consumer) -> {
                drawHull(from, to, width, height, pose, consumer, 0, 156, 255, 30);
                drawHull(from, to, width * .55f, height * .55f, pose, consumer, 63, 178, 255, 30);
            });
        }

        for (int i = 0; i < segments.size() - 1; i += 2) {
            var from = segments.get(i);
            var to = segments.get(i + 1);
            collector.submitCustomGeometry(poseStack, RenderHelper.CustomerRenderType.magicNoCull(SOLID), (pose, consumer) -> {
                drawHull(from, to, width * .2f, height * .2f, pose, consumer, 255, 255, 255, 255);
            });
        }


        poseStack.popPose();
    }

    public void drawHull(Vec3 from, Vec3 to, float width, float height, PoseStack.Pose pose, VertexConsumer consumer, int r, int g, int b, int a) {
        //Bottom
        drawQuad(from.subtract(0, height * .5f, 0), to.subtract(0, height * .5f, 0), width, 0, pose, consumer, r, g, b, a);
        //Top
        drawQuad(from.add(0, height * .5f, 0), to.add(0, height * .5f, 0), width, 0, pose, consumer, r, g, b, a);
        //Left
        drawQuad(from.subtract(width * .5f, 0, 0), to.subtract(width * .5f, 0, 0), 0, height, pose, consumer, r, g, b, a);
        //Right
        drawQuad(from.add(width * .5f, 0, 0), to.add(width * .5f, 0, 0), 0, height, pose, consumer, r, g, b, a);
    }

    public void drawQuad(Vec3 from, Vec3 to, float width, float height, PoseStack.Pose pose, VertexConsumer consumer, int r, int g, int b, int a) {
        float halfWidth = width * .5f;
        float halfHeight = height * .5f;
        consumer.addVertex(pose.pose(), (float) from.x - halfWidth, (float) from.y - halfHeight, (float) from.z).setColor(r, g, b, a).setUv(0f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(240).setNormal(0f, 1f, 0f);
        consumer.addVertex(pose.pose(), (float) from.x + halfWidth, (float) from.y + halfHeight, (float) from.z).setColor(r, g, b, a).setUv(1f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(240).setNormal(0f, 1f, 0f);
        consumer.addVertex(pose.pose(), (float) to.x + halfWidth, (float) to.y + halfHeight, (float) to.z).setColor(r, g, b, a).setUv(1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(240).setNormal(0f, 1f, 0f);
        consumer.addVertex(pose.pose(), (float) to.x - halfWidth, (float) to.y - halfHeight, (float) to.z).setColor(r, g, b, a).setUv(0f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(240).setNormal(0f, 1f, 0f);
    }

    public Identifier getTextureLocation(ElectrocuteProjectile p_115264_) {
        return SOLID;
    }

    public static class ElectrocuteRenderState extends EntityRenderState {
        public float yaw;
        public float pitch;
        public float eyeOffset;
        private List<Vec3> segments = List.of();

        public void captureBeam(float yaw, float pitch, float eyeOffset, List<Vec3> segments) {
            this.yaw = yaw;
            this.pitch = pitch;
            this.eyeOffset = eyeOffset;
            this.segments = List.copyOf(segments);
        }
    }
}
