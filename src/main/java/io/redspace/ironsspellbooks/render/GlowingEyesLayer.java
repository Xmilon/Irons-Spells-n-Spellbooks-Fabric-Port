package io.redspace.ironsspellbooks.render;

import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.PerBoneRender;
import java.util.function.BiConsumer;
import com.geckolib.cache.model.GeoBone;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Vector3f;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.layer.GeoRenderLayer;

public class GlowingEyesLayer {
    public static final Identifier EYE_TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/purple_eyes.png");
    public static final RenderType EYES = RenderTypes.eyes(EYE_TEXTURE);

    public static class Vanilla<T extends LivingEntity, M extends HumanoidModel<T>> extends EyesLayer<T, M> {

        public Vanilla(RenderLayerParent pRenderer) {
            super(pRenderer);
        }

        @Override
        public RenderType renderType() {
            return EYES;
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int pPackedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            var eye = getEyeType(livingEntity);
            if (eye != EyeType.None) {
                VertexConsumer vertexconsumer = multiBufferSource.getBuffer(this.renderType());

                //pMatrixStack.translate(0, -eye.yOffset, -eye.forwardOffset);
                float scale = getEyeScale(livingEntity);
                poseStack.scale(scale, scale, scale);
                this.getParentModel().renderToBuffer(poseStack, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, Utils.packRGB(new Vector3f(eye.r, eye.g, eye.b)));
            }
        }
    }

    public static class Geo extends GeoRenderLayer<AbstractSpellCastingMob, Void, EntityRenderState> {
        public Geo(GeoRenderer<AbstractSpellCastingMob, Void, EntityRenderState> renderer) {
            super(renderer);
        }

        // unverified: relies on GeoBone#render drawing the bone and its children at the pose GeckoLib leaves in the pass.
        @Override
        public void addPerBoneRender(RenderPassInfo<EntityRenderState> info, BiConsumer<GeoBone, PerBoneRender<EntityRenderState>> consumer) {
            var mob = (AbstractSpellCastingMob) info.renderState().getGeckolibData(IronsGeoEntityRenderer.ENTITY);
            var eye = getEyeType(mob);
            if (eye == EyeType.None) {
                return;
            }
            int color = Utils.packRGB(new Vector3f(eye.r, eye.g, eye.b)) | 0xFF000000;
            float scale = getEyeScale(mob);
            info.model().getBone("head").ifPresent(head -> consumer.accept(head, (renderInfo, bone, collector) -> {
                var poseStack = renderInfo.poseStack();
                poseStack.pushPose();
                poseStack.scale(scale, scale, scale);
                collector.submitCustomGeometry(poseStack, EYES, (pose, vertexConsumer) -> {
                    var drawStack = new PoseStack();
                    drawStack.last().set(pose);
                    bone.render(renderInfo, drawStack, vertexConsumer, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, color);
                });
                poseStack.popPose();
            }));
        }
    }

    public static EyeType getEyeType(LivingEntity entity) {
        //Sorted by most prioritized color
        if (entity.hasEffect(MobEffectRegistry.ABYSSAL_SHROUD))
            return EyeType.Abyssal;
        if (entity.hasEffect(MobEffectRegistry.PLANAR_SIGHT))
            return EyeType.Planar_Sight;
//        else if (entity.getItemBySlot(EquipmentSlot.HEAD).is(ItemRegistry.SHADOWWALKER_HELMET.get()))
//            return EyeType.Ender_Armor;
        else return EyeType.None;
    }

    public static float getEyeScale(LivingEntity entity) {
        //Sorted by most prioritized scale (highest to lowest)
        if (entity.getItemBySlot(EquipmentSlot.HEAD).is(ItemRegistry.SHADOWWALKER_HELMET.get()))
            return EyeType.Ender_Armor.scale;
        if (entity.hasEffect(MobEffectRegistry.ABYSSAL_SHROUD))
            return EyeType.Abyssal.scale;
        if (entity.hasEffect(MobEffectRegistry.PLANAR_SIGHT))
            return EyeType.Planar_Sight.scale;
        else return EyeType.None.scale;
    }

    public enum EyeType {
        None(0, 0, 0, 0),
        Abyssal(1f, 1f, 1f, 1f),
        Planar_Sight(.42f, .258f, .96f, 1f),
        Ender_Armor(.816f, 0f, 1f, 1.15f);

        public final float r, g, b, scale;

        EyeType(float r, float g, float b, float scale) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.scale = scale;
        }
    }
}

