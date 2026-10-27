package io.redspace.ironsspellbooks.entity.spells.icicle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
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

public class IcicleRenderer extends EntityBackedRenderer<IcicleProjectile> {
    public static final Identifier TEXTURE = IronsSpellbooks.id("textures/entity/icicle_projectile.png");
    public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "icicle_model"), "main");

    private final ModelPart body;
    //private final ModelPart tip;

    public IcicleRenderer(Context context) {
        super(context);
        ModelPart modelpart = context.bakeLayer(IcicleRenderer.MODEL_LAYER_LOCATION);
        //this.tip = modelpart.getChild("tip");
        this.body = modelpart.getChild("model");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bb_main = partdefinition.addOrReplaceChild("model", CubeListBuilder.create().texOffs(0, 8).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    protected void submit(IcicleProjectile entity, float partialTicks, int light, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();

        Vec3 motion = entity.getDeltaMovement();
        float xRot = -((float) (Mth.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        float yRot = -((float) (Mth.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 90.0F);
        poseStack.rotateDegrees(Axis.YP, yRot);
        poseStack.rotateDegrees(Axis.XP, xRot);
        poseStack.scale(1, -1, 1);
        collector.submitModelPart(this.body, poseStack, RenderTypes.entityCutout(TEXTURE), light, OverlayTexture.NO_OVERLAY, null);
        //this.tip.render(poseStack, consumer2, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    public Identifier getTextureLocation(IcicleProjectile entity) {
        return TEXTURE;
    }
}
