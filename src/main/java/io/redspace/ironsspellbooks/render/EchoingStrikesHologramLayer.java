package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

@OnlyIn(Dist.CLIENT)
public class EchoingStrikesHologramLayer {
    public static class Vanilla extends RenderLayer<AvatarRenderState, PlayerModel> {

        public Vanilla(RenderLayerParent<AvatarRenderState, PlayerModel> renderer) {
            super(renderer);
        }

        @Override
        public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, AvatarRenderState state, float yRot, float xRot) {
            var effects = LivingEffectsRenderState.get(state);
            if (effects == null || effects.hideAll() || effects.echo() <= 0) {
                return;
            }
            float percent = effects.echo();
            float offset = Mth.sin(effects.age() * 2) * 0.75f * percent;
            poseStack.pushPose();
            poseStack.translate(offset, 0, 0);
            poseStack.scale(1.01f, 1.01f, 1.01f);
            int color = RenderHelper.colorf(.988f * percent, .313f * percent, .968f * percent, percent);
            collector.submitModel(getParentModel(), state, poseStack, RenderHelper.CustomerRenderType.magic(effects.texture()), LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, color, null, state.outlineColor);
            poseStack.popPose();
        }
    }

    public static class Geo extends GeoRenderLayer<AbstractSpellCastingMob, Void, EntityRenderState> {
        public Geo(GeoRenderer<AbstractSpellCastingMob, Void, EntityRenderState> renderer) {
            super(renderer);
        }

        // unverified: redraws the top level bones at the pose GeckoLib leaves in the pass, tinted and jittered by the echo.
        @Override
        public void submitRenderTask(RenderPassInfo<EntityRenderState> info, SubmitNodeCollector collector) {
            var effects = LivingEffectsRenderState.get(info.renderState());
            if (!info.willRender() || effects == null || effects.hideAll() || effects.echo() <= 0) {
                return;
            }
            float percent = effects.echo();
            float offset = Mth.sin(effects.age() * 2) * 0.75f * percent;
            int color = RenderHelper.colorf(.988f * percent, .313f * percent, .968f * percent, percent);
            var poseStack = info.poseStack();
            poseStack.pushPose();
            poseStack.translate(offset, 0, 0);
            poseStack.scale(1.01f, 1.01f, 1.01f);
            collector.submitCustomGeometry(poseStack, RenderHelper.CustomerRenderType.magic(effects.texture()), (pose, vertexConsumer) -> {
                var drawStack = new PoseStack();
                drawStack.last().set(pose);
                for (var bone : info.model().topLevelBones()) {
                    bone.render(info, drawStack, vertexConsumer, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, color);
                }
            });
            poseStack.popPose();
        }
    }
}
