package io.redspace.ironsspellbooks.entity.mobs.keeper;

import com.geckolib.renderer.base.RenderPassInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import io.redspace.ironsspellbooks.render.RenderHelper;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class KeeperRenderer extends AbstractSpellCastingMobRenderer {
    public KeeperRenderer(EntityRendererProvider.Context context) {
        super(context, new KeeperModel());
        withRenderLayer(new GeoKeeperGhostLayer(this));
        this.shadowRadius = 0.65f;
    }

    @Override
    public void submit(EntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        // let there be extra light during bossfight for visual clarity
        if (entity(state) instanceof KeeperEntity keeper && keeper.isSummoned()) {
            state.lightCoords = Math.clamp(state.lightCoords + 100, 0, 240);
        }
        super.submit(state, poseStack, collector, camera);
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<EntityRenderState> info) {
        info.poseStack().scale(1.3f, 1.3f, 1.3f);
        super.adjustRenderPose(info);
    }

    @Override
    public RenderType getRenderType(EntityRenderState state, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }

    @Override
    public int getPackedOverlay(AbstractSpellCastingMob animatable, Void relatedObject, float u, float partialTick) {
        return OverlayTexture.pack(OverlayTexture.u(u), OverlayTexture.v(animatable.deathTime > 0));
    }

    @Override
    public int getRenderColor(AbstractSpellCastingMob animatable, Void relatedObject, float partialTick) {
        int color = super.getRenderColor(animatable, relatedObject, partialTick);
        if (animatable instanceof KeeperEntity keeper && keeper.isRising()) {
            color = RenderHelper.colorf(1f, 1f, 1f, Mth.clamp((KeeperEntity.RISE_ANIM_TIME - keeper.riseAnimTick + 1) / (float) KeeperEntity.RISE_ANIM_TIME, 0, 1f));
        }
        return color;
    }
}
