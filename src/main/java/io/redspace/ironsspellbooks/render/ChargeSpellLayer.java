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
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.layer.GeoRenderLayer;
import com.geckolib.util.RenderUtil;

public class ChargeSpellLayer {
    public static class Vanilla<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {

        public Vanilla(RenderLayerParent<T, M> pRenderer) {
            super(pRenderer);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferSource, int pPackedLight, T entity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            var syncedSpellData = ClientMagicData.getSyncedSpellData(entity);
            if (!syncedSpellData.isCasting()) {
                return;
            }
            var spellId = syncedSpellData.getCastingSpellId();
            poseStack.pushPose();
            this.getParentModel().translateToHand(HumanoidArm.RIGHT, poseStack);
            handleRender(poseStack, bufferSource, pPackedLight, entity, spellId, false);
            poseStack.popPose();
        }
    }

    private static <T extends LivingEntity> void handleRender(PoseStack poseStack, SubmitNodeCollector bufferSource, int pPackedLight, T entity, String spellId, boolean offhand) {
        if (spellId.equals(SpellRegistry.LIGHTNING_LANCE_SPELL.get().getSpellId())) {
            poseStack.translate((double) ((float) (offhand ? -1 : 1) / 32.0F) - .125, .5, 0);
            poseStack.rotate(Axis.YP.rotationDegrees(180.0F));
            LightningLanceRenderer.renderModel(poseStack, bufferSource, entity.tickCount);
        } else if (spellId.equals(SpellRegistry.MAGIC_ARROW_SPELL.get().getSpellId())) {
            poseStack.translate(((float) (offhand ? -1 : 1) / 32.0F), .5, 0);
            poseStack.rotate(Axis.YP.rotationDegrees(180.0F));
            poseStack.rotate(Axis.XP.rotationDegrees(90.0F));
            MagicArrowRenderer.renderModel(poseStack, bufferSource);
        } else if (spellId.equals(SpellRegistry.POISON_ARROW_SPELL.get().getSpellId())) {
            poseStack.translate(((float) (offhand ? -1 : 1) / 32.0F), 1, 0);
            poseStack.rotate(Axis.YP.rotationDegrees(180.0F));
            poseStack.rotate(Axis.XP.rotationDegrees(90.0F));
            PoisonArrowRenderer.renderModel(poseStack, bufferSource, pPackedLight);
        } else if (spellId.equals(SpellRegistry.FIRE_ARROW_SPELL.get().getSpellId())) {
            poseStack.translate(((float) (offhand ? -1 : 1) / 32.0F), .5, 0);
            poseStack.rotate(Axis.YP.rotationDegrees(180.0F));
            poseStack.rotate(Axis.XP.rotationDegrees(90.0F));
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
                handleRender(poseStack, collector, renderInfo.packedLight(), mob, spellId, false);
                poseStack.popPose();
            }));
        }
    }
}
