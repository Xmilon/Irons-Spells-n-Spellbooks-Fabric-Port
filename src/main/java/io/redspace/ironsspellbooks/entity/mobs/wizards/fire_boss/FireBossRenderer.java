package io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss;

import com.geckolib.cache.model.GeoBone;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.PerBoneRender;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.render.IronsGeoEntityRenderer;
import io.redspace.ironsspellbooks.render.RenderHelper;
import io.redspace.ironsspellbooks.util.DefaultBipedBoneIdents;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;

import java.util.function.BiConsumer;

public class FireBossRenderer extends AbstractSpellCastingMobRenderer {
    static final int deathFadeTime = 120;

    public FireBossRenderer(EntityRendererProvider.Context context) {
        super(context, new FireBossModel());
        this.shadowRadius = 0.65f;
        withRenderLayer(new FireBossSoulLayer(this));
        withRenderLayer(new FireBossFlameLayer(this));
        withRenderLayer(new FireBossFireballChargeLayer(this, context));
        withRenderLayer(new SpectralDaggerLayer(this, context.getItemModelResolver()));
    }

    @Override
    public void submit(EntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        var entity = entity(state);
        float partialTick = state.getGeckolibData(PARTIAL_TICK);
        if (entity instanceof FireBossEntity fireBossEntity && fireBossEntity.isSpawning()) {
            float f = fireBossEntity.getSpawnWalkPercent(partialTick);
            if (f == 0) {
                // not visible, dont render
                return;
            }
            shadowRadius = Mth.lerp(f, 2, .65f);
            shadowStrength = Mth.lerp(f, 0, 1);
        } else {
            shadowStrength = 1;
            shadowRadius = .65f;
        }
        // extra light for visual clarity during the fight
        state.lightCoords = Math.clamp(state.lightCoords + 100, 0, 240);
        super.submit(state, poseStack, collector, camera);
    }

    @Override
    public int getPackedOverlay(AbstractSpellCastingMob animatable, Void relatedObject, float u, float partialTick) {
        //disable hurt/death red flashing
        return OverlayTexture.NO_OVERLAY;
    }

    @Override
    public int getRenderColor(AbstractSpellCastingMob animatable, Void relatedObject, float partialTick) {
        int color = super.getRenderColor(animatable, relatedObject, partialTick);
        float f = 1f;
        if (animatable.deathTime > 160 - deathFadeTime) {
            f = Mth.clamp((160 - animatable.deathTime) / (float) deathFadeTime, 0, 1f);
        } else if (animatable instanceof FireBossEntity fireBoss && fireBoss.isSpawning()) {
            f = fireBoss.getSpawnWalkPercent(partialTick);
        }
        if (!animatable.isInvisible() && f != 1) {
            color = RenderHelper.colorf(1f, 1f, 1f, f);
        }
        return color;
    }

    @Override
    public RenderType getRenderType(EntityRenderState state, Identifier texture) {
        var animatable = entity(state);
        if (animatable.isDeadOrDying() || animatable instanceof FireBossEntity fireBoss && (fireBoss.isSpawning() || fireBoss.isDespawning())) {
            return RenderTypes.entityTranslucent(texture);
        }
        return super.getRenderType(state, texture);
    }

    @Override
    protected float getDeathMaxRotation(GeoRenderState state) {
        return 0;
    }

    /**
     * shortcut: draws the hellrazor's default item model; the dedicated item/fiery_dagger model variant is not selected yet.
     */
    private static class SpectralDaggerLayer extends GeoRenderLayer<AbstractSpellCastingMob, Void, EntityRenderState> {
        private final ItemModelResolver itemModelResolver;

        SpectralDaggerLayer(GeoRenderer<AbstractSpellCastingMob, Void, EntityRenderState> renderer, ItemModelResolver itemModelResolver) {
            super(renderer);
            this.itemModelResolver = itemModelResolver;
        }

        @Override
        public void addPerBoneRender(RenderPassInfo<EntityRenderState> info, BiConsumer<GeoBone, PerBoneRender<EntityRenderState>> consumer) {
            if (!(IronsGeoEntityRenderer.entityOf(info.renderState()) instanceof FireBossEntity boss) || !boss.spectralDaggerActive()) {
                return;
            }
            info.model().getBone(DefaultBipedBoneIdents.LEFT_HAND_BONE_IDENT).ifPresent(hand -> consumer.accept(hand, (renderInfo, bone, collector) -> {
                var poseStack = renderInfo.poseStack();
                poseStack.pushPose();
                poseStack.rotateDegrees(Axis.XP, 90);
                poseStack.translate(-0.375, .1, -1);
                var itemState = new ItemStackRenderState();
                itemModelResolver.updateForLiving(itemState, ItemRegistry.HELLRAZOR.get().getDefaultInstance(), ItemDisplayContext.THIRD_PERSON_LEFT_HAND, boss);
                itemState.submit(poseStack, collector, renderInfo.packedLight(), renderInfo.packedOverlay(), 0);
                poseStack.popPose();
            }));
        }
    }
}
