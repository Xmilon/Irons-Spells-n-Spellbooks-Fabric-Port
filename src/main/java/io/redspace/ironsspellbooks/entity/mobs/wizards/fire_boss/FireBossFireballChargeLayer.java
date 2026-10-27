package io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss;

import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.spells.fireball.FireballRenderer;
import io.redspace.ironsspellbooks.render.IronsGeoEntityRenderer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;

public class FireBossFireballChargeLayer extends GeoRenderLayer<AbstractSpellCastingMob, Void, EntityRenderState> {
    protected final ModelPart fireball;

    public FireBossFireballChargeLayer(GeoRenderer<AbstractSpellCastingMob, Void, EntityRenderState> renderer, EntityRendererProvider.Context context) {
        super(renderer);
        ModelPart modelpart = context.bakeLayer(FireballRenderer.MODEL_LAYER_LOCATION);
        this.fireball = modelpart.getChild("body");
    }

    @Override
    public void submitRenderTask(RenderPassInfo<EntityRenderState> info, SubmitNodeCollector collector) {
        if (!(IronsGeoEntityRenderer.entityOf(info.renderState()) instanceof FireBossEntity fireBoss) || !fireBoss.isHalfHealthAttacking()) {
            return;
        }
        var tick = FireBossEntity.HALF_HEALTH_ANIM_DURATION - fireBoss.halfHealthTimer;
        if (tick <= FireBossEntity.HALF_HEALTH_JUMP_TIMESTAMP || tick >= FireBossEntity.HALF_HEALTH_CAST_TIMESTAMP) {
            return;
        }
        var poseStack = info.poseStack();
        poseStack.pushPose();
        poseStack.rotateDegrees(Axis.YP, fireBoss.yHeadRot);
        poseStack.translate(0, fireBoss.getBoundingBox().getYsize() * 1.25, 0);
        float scale = Mth.lerp(tick / (float) FireBossEntity.HALF_HEALTH_ANIM_DURATION, 1f, 1.5f);
        poseStack.scale(scale, scale, scale);
        float f = fireBoss.tickCount + IronsGeoEntityRenderer.partialTickOf(info.renderState());
        poseStack.rotateDegrees(Axis.XP, Mth.cos(.08f * f) * 180);
        poseStack.rotateDegrees(Axis.YP, Mth.sin(.08f * f) * 180);
        poseStack.rotateDegrees(Axis.ZP, Mth.cos(.08f * f + 5464) * 180);
        collector.submitModelPart(fireball, poseStack, RenderTypes.entityCutout(FireballRenderer.BASE_TEXTURE), info.packedLight(), OverlayTexture.NO_OVERLAY, null);
        poseStack.popPose();
    }
}
