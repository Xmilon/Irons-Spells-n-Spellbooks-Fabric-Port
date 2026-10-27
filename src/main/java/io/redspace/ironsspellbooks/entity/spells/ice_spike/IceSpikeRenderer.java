package io.redspace.ironsspellbooks.entity.spells.ice_spike;

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

public class IceSpikeRenderer extends EntityBackedRenderer<IceSpikeEntity> {

    private final IceSpikeModel model;

    public IceSpikeRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new IceSpikeModel(pContext.bakeLayer(IceSpikeModel.LAYER_LOCATION));
    }

    protected void submit(IceSpikeEntity entity, float partialTicks, int light, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (entity.tickCount < entity.getWaitTime())
            return;
        float f = entity.tickCount + partialTicks;
        poseStack.pushPose();
        poseStack.rotateDegrees(Axis.YP, -entity.getYRot());
        poseStack.rotateDegrees(Axis.XP, entity.getXRot());
        float anim = entity.getPositionOffset(partialTicks);
        poseStack.scale(1, -1, 1);
        float scale = entity.getSpikeSize();
//        if (scale < 3 && scale > 2) {
//            scale -= 1;
//        } else if (scale >= 3) {
//            scale -= 2;
//        }
        scale = (scale - 1) * .25f + 1;
        poseStack.scale(scale, scale, scale);
        poseStack.translate(0, -anim * (22 + 22 + 24) / 16f, 0);

        this.model.submit(entity.getSpikeSize(), collector, poseStack, RenderTypes.entityCutout(getTextureLocation(entity)), light);
        poseStack.popPose();
    }

    public Identifier getTextureLocation(IceSpikeEntity pEntity) {
        return IronsSpellbooks.id("textures/entity/ice_spike.png");
    }

    public static class IceSpikeModel {
        public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "ice_spike"), "main");
        private final ModelPart bottom;
        private final ModelPart middle;
        private final ModelPart top;

        public IceSpikeModel(ModelPart root) {
            this.bottom = root.getChild("bottom");
            this.middle = root.getChild("middle");
            this.top = root.getChild("top");
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();

            PartDefinition bottom = partdefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -25.0F, -9.0F, 10.0F, 24.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 25.0F, 4.0F));

            PartDefinition cube_r1 = bottom.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(40, 3).addBox(-5.0F, -10.0F, -1.0F, 6.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, 0.0F, -8.0F, 0.3295F, -0.1172F, 0.3295F));

            PartDefinition cube_r2 = bottom.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(40, 3).addBox(-5.0F, -10.0F, -1.0F, 6.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.812F, 0.1172F, 2.812F));

            PartDefinition middle = partdefinition.addOrReplaceChild("middle", CubeListBuilder.create().texOffs(0, 34).addBox(-1.0F, -25.0F, -1.0F, 8.0F, 22.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 3.0F, -3.0F));

            PartDefinition cube_r3 = middle.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(40, 3).addBox(-5.0F, -10.0F, -1.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, 0.0F, 6.0F, -1.3526F, -1.3526F, 1.5708F));

            PartDefinition cube_r4 = middle.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(40, 3).addBox(-5.0F, -10.0F, -1.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.789F, 1.3526F, 1.5708F));

            PartDefinition top = partdefinition.addOrReplaceChild("top", CubeListBuilder.create().texOffs(39, 38).addBox(-1.0F, -25.0F, -3.0F, 4.0F, 22.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, -19.0F, 1.0F));

            PartDefinition cube_r5 = top.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(40, 3).addBox(-5.0F, -10.0F, -1.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 0.0F, -2.0F, 0.1719F, -0.0302F, 0.1719F));

            PartDefinition cube_r6 = top.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(40, 3).addBox(-5.0F, -10.0F, -1.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -2.9697F, 0.0302F, 2.9697F));

            return LayerDefinition.create(meshdefinition, 64, 64);
        }

        /**
         * Offsets are applied through the pose stack rather than by mutating the shared parts, because submitted parts are drawn later.
         */
        public void submit(float scale, SubmitNodeCollector collector, PoseStack poseStack, RenderType renderType, int light) {
            int ypos = 26;
            if (scale >= 3) {
                ypos -= 26;
                submitPart(bottom, 25, ypos, collector, poseStack, renderType, light);
            }
            ypos -= 22;
            submitPart(middle, 3, ypos, collector, poseStack, renderType, light);
            if (scale >= 2) {
                ypos -= 22;
                submitPart(top, -19, ypos, collector, poseStack, renderType, light);
            }
        }

        private static void submitPart(ModelPart part, int initialY, int y, SubmitNodeCollector collector, PoseStack poseStack, RenderType renderType, int light) {
            poseStack.pushPose();
            poseStack.translate(0, (y - initialY) / 16f, 0);
            collector.submitModelPart(part, poseStack, renderType, light, OverlayTexture.NO_OVERLAY, null);
            poseStack.popPose();
        }
    }
}
