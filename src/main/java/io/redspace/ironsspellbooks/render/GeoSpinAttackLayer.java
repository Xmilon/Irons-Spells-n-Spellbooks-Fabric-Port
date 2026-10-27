package io.redspace.ironsspellbooks.render;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import io.redspace.ironsspellbooks.entity.spells.SpinAttackModel;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.layer.GeoRenderLayer;

@OnlyIn(Dist.CLIENT)
public class GeoSpinAttackLayer extends GeoRenderLayer<AbstractSpellCastingMob, Void, EntityRenderState> {
    private final GeoModel<AbstractSpellCastingMob> modelProvider = new SpinAttackModel();

    public GeoSpinAttackLayer(GeoRenderer<AbstractSpellCastingMob, Void, EntityRenderState> renderer) {
        super(renderer);
    }

    // unverified: re-submits the spin model through the main renderer's pass.
    @Override
    public void submitRenderTask(RenderPassInfo<EntityRenderState> info, SubmitNodeCollector collector) {
        var mob = (AbstractSpellCastingMob) info.renderState().getGeckolibData(IronsGeoEntityRenderer.ENTITY);
        if (!info.willRender() || !mob.isAutoSpinAttack()) {
            return;
        }
        float partialTick = info.renderState().getGeckolibData(IronsGeoEntityRenderer.PARTIAL_TICK);
        var poseStack = info.poseStack();
        for (int i = 0; i < 3; ++i) {
            poseStack.pushPose();
            float f = (mob.tickCount + partialTick) + (float) (-(45 + i * 5));
            poseStack.rotateDegrees(Axis.YP, f);
            float f1 = 0.75F * (float) i;
            poseStack.scale(f1, f1, f1);
            poseStack.translate(0, -1 + (double) (-0.2F + 0.6F * (float) i), 0);
            var renderType = RenderTypes.entityCutout(modelProvider.getTextureResource(info.renderState()));
            getRenderer().submitRenderTasks(info, collector.order(1), renderType);
            poseStack.popPose();
        }
    }
}
