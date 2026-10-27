package io.redspace.ironsspellbooks.entity.spells.snowball;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.entity.spells.acid_orb.AcidOrbRenderer;
import net.minecraft.client.model.geom.ModelPart;
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

public class SnowballRenderer extends EntityBackedRenderer<AbstractMagicProjectile> {
    private final ModelPart orb;

    public SnowballRenderer(Context context) {
        super(context);
        ModelPart modelpart = context.bakeLayer(AcidOrbRenderer.MODEL_LAYER_LOCATION);
        this.orb = modelpart.getChild("orb");
    }

    @Override
    protected void submit(AbstractMagicProjectile entity, float partialTicks, int light, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0, entity.getBoundingBox().getYsize() * .5f, 0);

        Vec3 motion = entity.getDeltaMovement();
        float xRot = -((float) (Mth.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        float yRot = -((float) (Mth.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 90.0F);
        poseStack.rotateDegrees(Axis.YP, yRot);
        poseStack.rotateDegrees(Axis.XP, xRot);

        float f = entity.tickCount + partialTicks;
        float swirlX = Mth.cos(.08f * f) * 130;
        float swirlY = Mth.sin(.08f * f) * 130;
        float swirlZ = Mth.cos(.08f * f + 5464) * 130;
        poseStack.rotateDegrees(Axis.XP, swirlX);
        poseStack.rotateDegrees(Axis.YP, swirlY);
        poseStack.rotateDegrees(Axis.ZP, swirlZ);
        collector.submitModelPart(this.orb, poseStack, RenderTypes.entityCutout(getTextureLocation(entity)), light, OverlayTexture.NO_OVERLAY, null);

        poseStack.popPose();
    }

    public Identifier getTextureLocation(AbstractMagicProjectile entity) {
        return Identifier.withDefaultNamespace("textures/block/snow.png");
    }

}
