package io.redspace.ironsspellbooks.render;

import com.geckolib.renderer.base.GeoRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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

    public static class Vanilla extends RenderLayer<Player, HumanoidModel<Player>> {
        public static ModelLayerLocation ENERGY_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "energy_layer"), "main");
        private final HumanoidModel<Player> model;
        private final Identifier TEXTURE;
        private final Predicate<LivingEntity> shouldRender;

        public Vanilla(RenderLayerParent pRenderer, Identifier texture, Predicate<LivingEntity> shouldRender) {
            super(pRenderer);
            this.model = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ENERGY_LAYER));
            this.TEXTURE = texture;
            this.shouldRender = shouldRender;
        }

        public Vanilla(RenderLayerParent pRenderer, Identifier texture, Holder<MobEffect> shouldRenderFlag) {
            this(pRenderer, texture, living -> living.hasEffect(shouldRenderFlag));
        }

        public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, Player pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            if (EnergySwirlLayer.shouldRender(pLivingEntity, shouldRender)) {
                float f = (float) pLivingEntity.tickCount + pPartialTicks;
                HumanoidModel<Player> entitymodel = this.model();
                VertexConsumer vertexconsumer = pBuffer.getBuffer(EnergySwirlLayer.getRenderType(TEXTURE, f));
                this.getParentModel().copyPropertiesTo(entitymodel);
                entitymodel.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, COLOR);
            }
        }

        protected HumanoidModel<Player> model() {
            return model;
        }

        protected boolean shouldRender(Player entity) {
            return true;
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
