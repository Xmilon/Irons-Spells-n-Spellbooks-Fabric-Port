package io.redspace.ironsspellbooks.entity.spells.electrocute;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.render.RenderHelper;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import io.redspace.ironsspellbooks.render.EntityBackedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ElectrocuteRenderer extends EntityBackedRenderer<ElectrocuteProjectile> {
    private static Identifier TEXTURES[] = {
            IronsSpellbooks.id("textures/entity/electric_beams/beam_1.png"),
            IronsSpellbooks.id("textures/entity/electric_beams/beam_2.png"),
            IronsSpellbooks.id("textures/entity/electric_beams/beam_3.png"),
            IronsSpellbooks.id("textures/entity/electric_beams/beam_4.png")
    };
    private static Identifier SOLID = IronsSpellbooks.id("textures/entity/electric_beams/solid.png");

    public ElectrocuteRenderer(Context context) {
        super(context);
    }

    @Override
    protected void submit(ElectrocuteProjectile entity, float partialTicks, int light, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (entity.getOwner() == null)
            return;
        poseStack.pushPose();

//        RenderSystem.disableDepthTest();
//        RenderSystem.enableBlend();
//        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE);

        //VertexConsumer consumer = bufferSource.getBuffer(RenderTypes.lightning());
        poseStack.translate(0, entity.getEyeHeight() * .5f, 0);
        poseStack.rotateDegrees(Axis.YP, -entity.getOwner().getYRot());
        poseStack.rotateDegrees(Axis.XP, entity.getOwner().getXRot());
        poseStack.translate(0, 0, 0.1);


        List<Vec3> segments = entity.getBeamCache();
        //irons_spellbooks.LOGGER.debug("ElectrocuteRenderer.segments.length: {}",segments.size());

        float width = .3f;
        float height = width;
        Vec3 start = Vec3.ZERO;//entity.getOwner().getEyePosition().add(entity.getForward().normalize().scale(.15f));
        for (int i = 0; i < segments.size() - 1; i += 2) {
            var from = segments.get(i).add(start);
            var to = segments.get(i + 1).add(start);
            collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucentEmissive(getTextureLocation(entity)), (pose, consumer) -> {
                drawHull(from, to, width, height, pose, consumer, 0, 156, 255, 30);
                drawHull(from, to, width * .55f, height * .55f, pose, consumer, 63, 178, 255, 30);
            });
        }

        for (int i = 0; i < segments.size() - 1; i += 2) {
            var from = segments.get(i).add(start);
            var to = segments.get(i + 1).add(start);
            collector.submitCustomGeometry(poseStack, RenderHelper.CustomerRenderType.magicNoCull(getTextureLocation(entity)), (pose, consumer) -> {
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
        //return TEXTURES[(int) (Math.random() * 4)];
        return SOLID;
    }
}
