package io.redspace.ironsspellbooks.render;

import com.geckolib.renderer.base.GeoRenderer;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.capabilities.magic.MultiTargetEntityCastData;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.layer.GeoRenderLayer;

public class SpellTargetingLayer {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/target/heal.png");

    public static class Vanilla<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
        public Vanilla(RenderLayerParent<T, M> pRenderer) {
            super(pRenderer);
        }


        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferSource, int pPackedLight, T entity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            if (shouldRender(entity)) {
                renderTargetLayer(poseStack, bufferSource, entity);
            }
        }

    }

    public static class Geo extends GeoRenderLayer<AbstractSpellCastingMob, Void, EntityRenderState> {
        public Geo(GeoRenderer<AbstractSpellCastingMob, Void, EntityRenderState> renderer) {
            super(renderer);
        }

        @Override
        public void submitRenderTask(RenderPassInfo<EntityRenderState> info, SubmitNodeCollector collector) {
            var mob = (AbstractSpellCastingMob) info.renderState().getGeckolibData(IronsGeoEntityRenderer.ENTITY);
            if (info.willRender() && shouldRender(mob)) {
                var poseStack = info.poseStack();
                poseStack.pushPose();
                poseStack.rotateDegrees(Axis.XP, 180);
                poseStack.translate(0, -mob.getBoundingBox().getYsize() / 2, 0);
                renderTargetLayer(poseStack, collector, mob);
                poseStack.popPose();
            }
        }
    }

    private static Vector3f getColor(String spellId) {
        return SpellRegistry.getSpell(spellId).getTargetingColor();
    }

    public static void renderTargetLayer(PoseStack poseStack, SubmitNodeCollector collector, LivingEntity entity) {
        //EntityRenderDispatcher#169(renderHitbox)
//        AABB aabb = entity.getBoundingBox().move(-entity.getX(), -entity.getY(), -entity.getZ());
//        double magicYOffset = 1.5 - aabb.getYsize();
//        poseStack.translate(0, magicYOffset, 0);
//        poseStack.pushPose();
//        LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), aabb, 1.0F, 1.0F, 1.0F, 1.0F);
//        poseStack.popPose();

        AABB aabb = entity.getBoundingBox().move(-entity.getX(), -entity.getY(), -entity.getZ());

        float width = (float) aabb.getXsize();
        float height = (float) aabb.getYsize();
        float halfWidth = width * .55f;
        float magicYOffset = (float) (1.5 - height);
        Vector3f color = null;
        if (ClientMagicData.getRecasts().hasRecastsActive()) {
            for (RecastInstance recastInstance : ClientMagicData.getRecasts().getActiveRecasts()) {
                if (recastInstance.getCastData() instanceof MultiTargetEntityCastData targetEntityCastData) {
                    if (targetEntityCastData.isTargeted(entity)) {
                        color = getColor(recastInstance.getSpellId());
                        break;
                    }
                }
            }
        }
        if (color == null) {
            color = getColor(ClientMagicData.getTargetingData().spellId);
        }
        color.mul(.4f);
        poseStack.pushPose();
        poseStack.translate(0, magicYOffset, 0);


        //LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), aabb, 1.0F, 1.0F, 1.0F, 1.0F);
        final float fr = color.x(), fg = color.y(), fb = color.z();
        for (int i = 0; i < 4; i++) {
            collector.submitCustomGeometry(poseStack, RenderTypes.energySwirl(TEXTURE, 0, 0), (pose, consumer) -> {
            consumer.addVertex(pose.pose(), halfWidth, height, halfWidth).setColor(fr, fg, fb, 1).setUv(0f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
            consumer.addVertex(pose.pose(), halfWidth, 0, halfWidth).setColor(fr, fg, fb, 1).setUv(0f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
            consumer.addVertex(pose.pose(), -halfWidth, 0, halfWidth).setColor(fr, fg, fb, 1).setUv(1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
            consumer.addVertex(pose.pose(), -halfWidth, height, halfWidth).setColor(fr, fg, fb, 1).setUv(1f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
            });
            poseStack.rotateDegrees(Axis.YP, 90.0F);
        }
        poseStack.popPose();
    }

    public static boolean shouldRender(LivingEntity entity) {
        if (ClientMagicData.getRecasts().hasRecastsActive()) {
            for (RecastInstance recastInstance : ClientMagicData.getRecasts().getActiveRecasts()) {
                if (recastInstance.getCastData() instanceof MultiTargetEntityCastData targetEntityCastData) {
                    if (targetEntityCastData.isTargeted(entity)) {
                        return true;
                    }
                }
            }
        }
        return ClientMagicData.getTargetingData().isTargeted(entity);
    }
}
