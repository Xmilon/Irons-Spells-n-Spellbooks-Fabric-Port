package io.redspace.ironsspellbooks.entity.spells.gust;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import io.redspace.ironsspellbooks.render.EntityBackedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public class GustRenderer extends EntityBackedRenderer<GustCollider> {

    public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "gust_model"), "main");
    private static Identifier TEXTURE = IronsSpellbooks.id("textures/entity/trident_riptide.png");

    private final ModelPart body;

    public GustRenderer(Context context) {
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
    protected void submit(GustCollider entity, float partialTicks, int light, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0, entity.getBoundingBox().getYsize() * .5f, 0);
        poseStack.rotateDegrees(Axis.YP, -entity.getYRot() - 180.0F);
        poseStack.rotateDegrees(Axis.XP, -entity.getXRot() - 90);
        poseStack.scale(.25f, .25f, .25f);

        float f = entity.tickCount + partialTicks;
        float scale = Mth.lerp(Mth.clamp(f / 6f, 0, 1), 1, 2.3f);
        float alpha = 1f - f / 10f;

        for (int i = 0; i < 3; i++) {
            poseStack.rotateDegrees(Axis.YP, f * 10);
            poseStack.scale(scale, scale, scale);
            poseStack.translate(0, scale - 1, 0);
            collector.submitModelPart(this.body, poseStack, RenderTypes.entityTranslucent(getTextureLocation(entity)), light, OverlayTexture.NO_OVERLAY, null, ARGB.color((int) (alpha * 255),255,255,255));
        }


        poseStack.popPose();
    }

    public Identifier getTextureLocation(GustCollider entity) {
        return TEXTURE;
    }

}
