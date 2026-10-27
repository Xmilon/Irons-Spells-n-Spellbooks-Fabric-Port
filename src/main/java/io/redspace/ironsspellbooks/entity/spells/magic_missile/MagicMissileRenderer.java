package io.redspace.ironsspellbooks.entity.spells.magic_missile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.spells.fireball.FireballRenderer;
import io.redspace.ironsspellbooks.render.RenderHelper;
import net.minecraft.client.model.geom.ModelPart;
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
import org.joml.Matrix4f;

public class MagicMissileRenderer extends EntityBackedRenderer<MagicMissileProjectile> {
    private static final Identifier TEXTURE = IronsSpellbooks.id("textures/entity/magic_missile/magic_missile.png");
    private static final Identifier FLARE = IronsSpellbooks.id("textures/entity/lens_flare.png");
    private final ModelPart body;

    public MagicMissileRenderer(Context context) {
        super(context);
        ModelPart modelpart = context.bakeLayer(FireballRenderer.MODEL_LAYER_LOCATION);
        this.body = modelpart.getChild("body");
    }

    @Override
    protected void submit(MagicMissileProjectile entity, float partialTicks, int light, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        Vec3 motion = entity.getDeltaMovement();
        float xRot = -((float) (Mth.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        float yRot = -((float) (Mth.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 90.0F);
        poseStack.rotateDegrees(Axis.YP, yRot);
        poseStack.rotateDegrees(Axis.XP, xRot);
        poseStack.scale(0.35f, 0.35f, 0.35f);

        collector.submitModelPart(this.body, poseStack, renderType(getTextureLocation(entity)), LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, null, RenderHelper.colorf(.8f, .8f, .8f));

        poseStack.popPose();

        poseStack.pushPose();
        float f = entity.tickCount + partialTicks;
        float scale = 0.5f + Mth.sin(f) * .125f;
        poseStack.scale(scale, scale, scale);
        poseStack.rotate(camera.orientation);
        poseStack.rotateDegrees(Axis.YP, 90f);
        poseStack.rotateDegrees(Axis.XP, (entity.tickCount + partialTicks) * 15);
        collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(FLARE), (pose, consumer) -> {
    //        int unpackedlight = Math.max(LightCoordsUtil.block(light), LightCoordsUtil.sky(light));
    //        int blowout = (int) Mth.lerp(unpackedlight / 15f, 0, 180);
            consumer.addVertex(pose.pose(), 0, -1, -1).setColor(255, 180, 255, 255).setUv(0f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
            consumer.addVertex(pose.pose(), 0, 1, -1).setColor(255, 180, 255, 255).setUv(0f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
            consumer.addVertex(pose.pose(), 0, 1, 1).setColor(255, 180, 255, 255).setUv(1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
            consumer.addVertex(pose.pose(), 0, -1, 1).setColor(255, 180, 255, 255).setUv(1f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
        });
        poseStack.popPose();
    }

    public RenderType renderType(Identifier TEXTURE) {
        return RenderTypes.energySwirl(TEXTURE, 0, 0);
    }

    public Identifier getTextureLocation(MagicMissileProjectile entity) {
        return TEXTURE;
    }

}
