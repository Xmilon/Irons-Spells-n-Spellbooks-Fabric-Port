package io.redspace.ironsspellbooks.entity.mobs.keeper;

import com.geckolib.cache.model.GeoBone;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.render.RenderHelper;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.layer.GeoRenderLayer;

@OnlyIn(Dist.CLIENT)
public class GeoKeeperGhostLayer extends GeoRenderLayer<AbstractSpellCastingMob> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/keeper/keeper_ghost.png");

    public GeoKeeperGhostLayer(GeoEntityRenderer entityRendererIn) {
        super(entityRendererIn);
    }

//    @Override
//    public void renderForBone(PoseStack poseStack, AbstractSpellCastingMob animatable, GeoBone bone, RenderType renderType2, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
//        int hurtTime = animatable.hurtTime;
//        if (hurtTime > 0) {
//            float alpha = (float) hurtTime / animatable.hurtDuration;
//            float f = (float) animatable.tickCount + partialTick;
//            var renderType = RenderType.energySwirl(TEXTURE, f * 0.02F % 1.0F, f * 0.01F % 1.0F);
//
//            VertexConsumer vertexconsumer = bufferSource.getBuffer(renderType);
//            poseStack.pushPose();
//            //float scale = 1 / (1.3f);
//            //poseStack.scale(scale, scale, scale);
//
//            //IronsSpellbooks.LOGGER.debug("{}", bone.getName());
//            if (bone.getName().equals("head")) {
//                bone.updateScale(.75f, .75f, .75f);
//            } else {
//                bone.updateScale(.95f, .99f, .95f);
//            }
//            BakedGeoModel model = getGeoModel().getBakedModel(getGeoModel().getModelResource(animatable));
//            getRenderer().reRender(model, poseStack, bufferSource, animatable, renderType, vertexconsumer, partialTick, packedLight, packedOverlay, 1f, 1f, 1f, 1f);
//            //getRenderer().defaultRender(poseStack, animatable, bufferSource, renderType, vertexconsumer, 0, partialTick, packedLight);
////
////            this.getRenderer().renderb(poseStack, animatable, bakedModel, renderType, bufferSource, vertexconsumer, true, partialTick,
////                    LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, .15f * alpha, .02f * alpha, 0.0f * alpha, 1f);
//
//            bone.updateScale(1f, 1f, 1f);
//            poseStack.popPose();
//        }
//    }

    @Override
    public void render(PoseStack poseStack, AbstractSpellCastingMob animatable, BakedGeoModel bakedModel, RenderType renderType2, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        int hurtTime = animatable.hurtTime;
        if (hurtTime > 0) {
            float alpha = (float) hurtTime / animatable.hurtDuration;
            float f = (float) animatable.tickCount + partialTick;
            var renderType = RenderType.energySwirl(TEXTURE, f * 0.02F % 1.0F, f * 0.01F % 1.0F);

            VertexConsumer vertexconsumer = bufferSource.getBuffer(renderType);
            poseStack.pushPose();
            //float scale = 1 / (1.3f);
            //poseStack.scale(scale, scale, scale);

            bakedModel.getBone("body").ifPresent((rootBone) -> {
                rootBone.getChildBones().forEach(bone -> {
                    //IronsSpellbooks.LOGGER.debug("{}", bone.getName());
                    if (bone.getName().equals("head")) {
                        bone.updateScale(.75f, .75f, .75f);
                    } else
                        bone.updateScale(.95f, .99f, .95f);
                });
            });

            this.getRenderer().actuallyRender(poseStack, animatable, bakedModel, renderType, bufferSource, vertexconsumer, true, partialTick,
                    LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, RenderHelper.colorf( .15f * alpha, .02f * alpha, 0.0f * alpha));

            bakedModel.getBone("body").ifPresent((rootBone) -> {
                rootBone.getChildBones().forEach(bone -> {
                    bone.updateScale(1f, 1f, 1f);
                });
            });
            poseStack.popPose();
        }

    }
}
