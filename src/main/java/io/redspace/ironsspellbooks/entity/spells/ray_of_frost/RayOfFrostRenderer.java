package io.redspace.ironsspellbooks.entity.spells.ray_of_frost;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.culling.Frustum;
import io.redspace.ironsspellbooks.render.EntityBackedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class RayOfFrostRenderer extends EntityBackedRenderer<RayOfFrostVisualEntity> {

    public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "ray_of_frost_model"), "main");
    private static final Identifier TEXTURE_CORE = IronsSpellbooks.id("textures/entity/ray_of_frost/core.png");
    private static final Identifier TEXTURE_OVERLAY = IronsSpellbooks.id("textures/entity/ray_of_frost/overlay.png");

    private final ModelPart body;

    public RayOfFrostRenderer(Context context) {
        super(context);
        ModelPart modelpart = context.bakeLayer(MODEL_LAYER_LOCATION);
        this.body = modelpart.getChild("body");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8, -16, -8, 16, 32, 16), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public boolean shouldRender(RayOfFrostVisualEntity pLivingEntity, Frustum pCamera, double pCamX, double pCamY, double pCamZ, float partialTick) {
        return true;
    }

    @Override
    protected void submit(RayOfFrostVisualEntity entity, float partialTicks, int light, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        float lifetime = RayOfFrostVisualEntity.lifetime;
        float scalar = .25f;
        float length = 32 * scalar * scalar;
        float f = entity.tickCount + partialTicks;
        poseStack.translate(0, entity.getBoundingBox().getYsize() * .5f, 0);
        poseStack.rotateDegrees(Axis.YP, -entity.getYRot() - 180.0F);
        poseStack.rotateDegrees(Axis.XP, -entity.getXRot() - 90);
        poseStack.scale(scalar, scalar, scalar);

        //float scale = Mth.lerp(Mth.clamp(f / 6f, 0, 1), 1, 2.3f);

        float alpha = Mth.clamp(1f - f / lifetime, 0, 1);

        for (float i = 0; i < entity.distance * 4; i += length) {
            poseStack.translate(0, length, 0);
            //Render overlay
            //VertexConsumer consumer = bufferSource.getBuffer(RenderTypes.entityTranslucent(TEXTURE_OVERLAY));
            {
                poseStack.pushPose();
                float expansion = Mth.clampedLerp(1.2f, 0, f / (lifetime));
                poseStack.rotateDegrees(Axis.YP, f * 5);
                poseStack.scale(expansion, 1, expansion);
                poseStack.rotateDegrees(Axis.YP, 45);
                collector.submitModelPart(this.body, poseStack, RenderTypes.energySwirl(TEXTURE_OVERLAY, 0, 0), LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, null, ARGB.color((int) (alpha * 255),255,255,255));
                poseStack.popPose();
            }
            //Render core
            {
                poseStack.pushPose();
                float expansion = Mth.clampedLerp(1, 0, f / (lifetime - 8));
                poseStack.scale(expansion, 1, expansion);
                poseStack.rotateDegrees(Axis.YP, f * -10);
                collector.submitModelPart(this.body, poseStack, RenderTypes.energySwirl(TEXTURE_CORE, 0, 0), LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, null, -1);
                poseStack.popPose();
            }
        }


        poseStack.popPose();
    }

    public Identifier getTextureLocation(RayOfFrostVisualEntity entity) {
        return TEXTURE_CORE;
    }

}
