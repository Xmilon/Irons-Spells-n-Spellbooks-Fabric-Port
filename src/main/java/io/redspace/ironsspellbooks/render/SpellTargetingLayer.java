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
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
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

    /** Size and tint of the targeting reticle, captured while the entity is still reachable. */
    public record Target(float width, float height, Vector3f color) {
    }

    public static class Vanilla<S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends RenderLayer<S, M> {
        public Vanilla(RenderLayerParent<S, M> renderer) {
            super(renderer);
        }

        @Override
        public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, S state, float yRot, float xRot) {
            var effects = LivingEffectsRenderState.get(state);
            if (effects != null && !effects.hideAll() && effects.targeting() != null) {
                renderTargetLayer(poseStack, collector, effects.targeting());
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
            var target = capture(mob);
            if (info.willRender() && target != null) {
                var poseStack = info.poseStack();
                poseStack.pushPose();
                poseStack.rotateDegrees(Axis.XP, 180);
                poseStack.translate(0, -target.height() / 2, 0);
                renderTargetLayer(poseStack, collector, target);
                poseStack.popPose();
            }
        }
    }

    private static Vector3f getColor(String spellId) {
        return SpellRegistry.getSpell(spellId).getTargetingColor();
    }

    /** @return the reticle to draw on this entity, or null when it is not targeted */
    public static Target capture(LivingEntity entity) {
        if (!shouldRender(entity)) {
            return null;
        }
        AABB aabb = entity.getBoundingBox();
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
        return new Target((float) aabb.getXsize(), (float) aabb.getYsize(), new Vector3f(color).mul(.4f));
    }

    public static void renderTargetLayer(PoseStack poseStack, SubmitNodeCollector collector, Target target) {
        float height = target.height();
        float halfWidth = target.width() * .55f;
        float magicYOffset = 1.5f - height;
        final float fr = target.color().x(), fg = target.color().y(), fb = target.color().z();
        poseStack.pushPose();
        poseStack.translate(0, magicYOffset, 0);
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
