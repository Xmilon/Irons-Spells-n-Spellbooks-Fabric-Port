package io.redspace.ironsspellbooks.render;

import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.PerBoneRender;
import java.util.function.BiConsumer;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.spells.fire_arrow.FireArrowRenderer;
import io.redspace.ironsspellbooks.entity.spells.lightning_lance.LightningLanceRenderer;
import io.redspace.ironsspellbooks.entity.spells.magic_arrow.MagicArrowRenderer;
import io.redspace.ironsspellbooks.entity.spells.poison_arrow.PoisonArrowRenderer;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.util.DefaultBipedBoneIdents;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.layer.GeoRenderLayer;
import com.geckolib.util.RenderUtil;

public class ChargeSpellLayer {
    public static class Vanilla<S extends HumanoidRenderState, M extends HumanoidModel<S>> extends RenderLayer<S, M> {

        public Vanilla(RenderLayerParent<S, M> renderer) {
            super(renderer);
        }

        @Override
        public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, S state, float yRot, float xRot) {
            var effects = LivingEffectsRenderState.get(state);
            if (effects == null || effects.hideAll() || effects.castingSpell().isEmpty()) {
                return;
            }
            poseStack.pushPose();
            this.getParentModel().translateToHand(state, HumanoidArm.RIGHT, poseStack);
            handleRender(poseStack, collector, packedLight, (int) effects.age(), effects.castingSpell(), false);
            poseStack.popPose();
        }
    }

    private static void handleRender(PoseStack poseStack, SubmitNodeCollector bufferSource, int pPackedLight, int tickCount, String spellId, boolean offhand) {
        if (spellId.equals(SpellRegistry.LIGHTNING_LANCE_SPELL.get().getSpellId())) {
            poseStack.translate((double) ((float) (offhand ? -1 : 1) / 32.0F) - .125, .5, 0);
            poseStack.rotateDegrees(Axis.YP, 180.0F);
            LightningLanceRenderer.renderModel(poseStack, bufferSource, tickCount);
        } else if (spellId.equals(SpellRegistry.MAGIC_ARROW_SPELL.get().getSpellId())) {
            poseStack.translate(((float) (offhand ? -1 : 1) / 32.0F), .5, 0);
            poseStack.rotateDegrees(Axis.YP, 180.0F);
            poseStack.rotateDegrees(Axis.XP, 90.0F);
            MagicArrowRenderer.renderModel(poseStack, bufferSource);
        } else if (spellId.equals(SpellRegistry.POISON_ARROW_SPELL.get().getSpellId())) {
            poseStack.translate(((float) (offhand ? -1 : 1) / 32.0F), 1, 0);
            poseStack.rotateDegrees(Axis.YP, 180.0F);
            poseStack.rotateDegrees(Axis.XP, 90.0F);
            PoisonArrowRenderer.renderModel(poseStack, bufferSource, pPackedLight);
        } else if (spellId.equals(SpellRegistry.FIRE_ARROW_SPELL.get().getSpellId())) {
            poseStack.translate(((float) (offhand ? -1 : 1) / 32.0F), .5, 0);
            poseStack.rotateDegrees(Axis.YP, 180.0F);
            poseStack.rotateDegrees(Axis.XP, 90.0F);
            FireArrowRenderer.renderModel(poseStack, bufferSource);
        }
    }

    public static class Geo extends GeoRenderLayer<AbstractSpellCastingMob, Void, EntityRenderState> {
        public Geo(GeoRenderer<AbstractSpellCastingMob, Void, EntityRenderState> renderer) {
            super(renderer);
        }

        // unverified: assumes the pose stack handed to a per-bone task is positioned at that bone.
        @Override
        public void addPerBoneRender(RenderPassInfo<EntityRenderState> info, BiConsumer<GeoBone, PerBoneRender<EntityRenderState>> consumer) {
            var mob = (AbstractSpellCastingMob) info.renderState().getGeckolibData(IronsGeoEntityRenderer.ENTITY);
            info.model().getBone("right_arm").ifPresent(arm -> consumer.accept(arm, (renderInfo, bone, collector) -> {
                var poseStack = renderInfo.poseStack();
                poseStack.pushPose();
                var spellId = ClientMagicData.getSyncedSpellData(mob).getCastingSpellId();
                var scaleinv = 1 / mob.getScale();
                poseStack.translate(0, 0.5, 0);
                poseStack.scale(scaleinv, scaleinv, scaleinv);
                if (!spellId.equals("irons_spellbooks:lightning_lance")) {
                    poseStack.rotateDegrees(Axis.XP, 180.0F);
                }
                handleRender(poseStack, collector, renderInfo.packedLight(), mob.tickCount, spellId, false);
                poseStack.popPose();
            }));
        }
    }
}
