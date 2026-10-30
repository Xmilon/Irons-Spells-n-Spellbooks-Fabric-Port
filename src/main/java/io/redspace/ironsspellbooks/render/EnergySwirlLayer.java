package io.redspace.ironsspellbooks.render;

import com.geckolib.renderer.base.GeoRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;

import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.layer.GeoRenderLayer;

import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class EnergySwirlLayer {
    public static final Identifier EVASION_TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/evasion.png");
    public static final Identifier CHARGE_TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/charged.png");
    private static final int COLOR = RenderHelper.colorf(.8f, .8f, .8f);

    public static class Vanilla extends RenderLayer<AvatarRenderState, PlayerModel> {
        public static ModelLayerLocation ENERGY_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "energy_layer"), "main");
        private final HumanoidModel<HumanoidRenderState> model;
        private final Identifier texture;
        private final Holder<MobEffect> shouldRenderFlag;

        public Vanilla(RenderLayerParent<AvatarRenderState, PlayerModel> renderer, Identifier texture, Holder<MobEffect> shouldRenderFlag) {
            super(renderer);
            this.model = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ENERGY_LAYER));
            this.texture = texture;
            this.shouldRenderFlag = shouldRenderFlag;
        }

        @Override
        public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, AvatarRenderState state, float yRot, float xRot) {
            var effects = LivingEffectsRenderState.get(state);
            if (effects == null || effects.hideAll() || !effects.effects().contains(shouldRenderFlag)) {
                return;
            }
            // submitModel poses the layer model from the state, replacing the old copyPropertiesTo
            collector.submitModel(model, state, poseStack, EnergySwirlLayer.getRenderType(texture, effects.age()), packedLight, OverlayTexture.NO_OVERLAY, COLOR, null, state.outlineColor);
        }
    }

    public static class Geo extends GeoRenderLayer<AbstractSpellCastingMob, Void, EntityRenderState> {
        private final Identifier TEXTURE;
        private final Predicate<LivingEntity> shouldRenderFlag;

        public Geo(GeoRenderer<AbstractSpellCastingMob, Void, EntityRenderState> renderer, Identifier texture, Holder<MobEffect> shouldRenderFlag) {
            this(renderer, texture, living -> living.hasEffect(shouldRenderFlag));
        }

        public Geo(GeoRenderer<AbstractSpellCastingMob, Void, EntityRenderState> renderer, Identifier texture, Predicate<LivingEntity> shouldRenderFlag) {
            super(renderer);
            this.TEXTURE = texture;
            this.shouldRenderFlag = shouldRenderFlag;
        }

        // shortcut: the swirl re-draws the whole model with the pass tint and without the old 1.1x body inflation.
        @Override
        public void submitRenderTask(RenderPassInfo<EntityRenderState> info, SubmitNodeCollector collector) {
            var mob = (AbstractSpellCastingMob) info.renderState().getGeckolibData(IronsGeoEntityRenderer.ENTITY);
            if (info.willRender() && EnergySwirlLayer.shouldRender(mob, shouldRenderFlag)) {
                float f = mob.tickCount + info.renderState().getGeckolibData(IronsGeoEntityRenderer.PARTIAL_TICK);
                getRenderer().submitRenderTasks(info, collector.order(1), EnergySwirlLayer.getRenderType(TEXTURE, f));
            }
        }
    }

    static RenderType getRenderType(Identifier texture, float f) {
        return RenderTypes.energySwirl(texture, f * 0.02F % 1.0F, f * 0.01F % 1.0F);
    }

    static boolean shouldRender(LivingEntity entity, Predicate<LivingEntity> shouldRenderFlag) {
        return shouldRenderFlag.test(entity);
    }
}
