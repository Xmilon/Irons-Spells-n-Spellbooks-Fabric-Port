package io.redspace.ironsspellbooks.entity.spells.ice_tomb;

import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class IceTombRenderer extends EntityBackedRenderer<IceTombEntity> {

    public static final Identifier NOCULL = IronsSpellbooks.id("textures/entity/ice_tomb/ice_tomb.png");
    public static final Identifier CULL = IronsSpellbooks.id("textures/entity/ice_tomb/ice_tomb_cull.png");
    private final IceTombModel model;

    public IceTombRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new IceTombModel(pContext.bakeLayer(IceTombModel.LAYER_LOCATION));
    }

    protected void submit(IceTombEntity entity, float partialTicks, int light, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.rotateDegrees(Axis.YP, -entity.getYRot());
        float xScaleFactor = entity.getBbWidth() / entity.getType().getDimensions().width();
        float yScaleFactor = entity.getBbHeight() / entity.getType().getDimensions().height();
        poseStack.scale(xScaleFactor, -yScaleFactor, -xScaleFactor);
        poseStack.translate(0, -1.501, 0);
        collector.submitModelPart(this.model.model, poseStack, RenderTypes.entityTranslucent(NOCULL), light, OverlayTexture.NO_OVERLAY, null, -1);
        collector.submitModelPart(this.model.model, poseStack, RenderTypes.entityTranslucentCull(CULL), light, OverlayTexture.NO_OVERLAY, null, -1);
        poseStack.popPose();
    }

    public Identifier getTextureLocation(IceTombEntity pEntity) {
        return NOCULL;
    }

    public static class IceTombModel {
        public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "ice_tomb"), "main");
        private final ModelPart model;

        public IceTombModel(ModelPart root) {
            this.model = root.getChild("model");
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();

            PartDefinition bb_main = partdefinition.addOrReplaceChild("model", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -36.0F, -8.0F, 16.0F, 36.0F, 16.0F, new CubeDeformation(0.0F))
                    .texOffs(40, 67).addBox(4.0F, -9.0F, 4.0F, 6.0F, 9.0F, 6.0F, new CubeDeformation(0.0F))
                    .texOffs(0, 52).addBox(1.0F, -24.0F, -11.0F, 10.0F, 24.0F, 10.0F, new CubeDeformation(0.0F))
                    .texOffs(64, 0).addBox(-8.0F, -36.0F, -8.0F, 16.0F, 36.0F, 16.0F, new CubeDeformation(-0.01F))
                    .texOffs(40, 52).addBox(-10.0F, -9.0F, -10.0F, 6.0F, 9.0F, 6.0F, new CubeDeformation(0.0F))
                    .texOffs(0, 86).addBox(-11.0F, -24.0F, 1.0F, 10.0F, 24.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

            return LayerDefinition.create(meshdefinition, 128, 128);
        }
    }
}
