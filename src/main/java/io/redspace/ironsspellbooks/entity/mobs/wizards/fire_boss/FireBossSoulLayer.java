package io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss;

import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.render.IronsGeoEntityRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import io.redspace.ironsspellbooks.render.RenderHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;

public class FireBossSoulLayer extends GeoRenderLayer<AbstractSpellCastingMob, Void, EntityRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/keeper/keeper_ghost.png");

    public FireBossSoulLayer(GeoRenderer<AbstractSpellCastingMob, Void, EntityRenderState> renderer) {
        super(renderer);
    }

    // shortcut: the original also hid the armor bones during the overlay; only its red tint fade and full-bright light are kept.
    @Override
    public void submitRenderTask(RenderPassInfo<EntityRenderState> info, SubmitNodeCollector collector) {
        AbstractSpellCastingMob mob = IronsGeoEntityRenderer.entityOf(info.renderState());
        if (!info.willRender() || mob.hurtTime + mob.deathTime <= 0) {
            return;
        }
        int i = mob.hurtTime + mob.deathTime;
        float alpha = mob.deathTime > 0 ? i / 120f : i / (float) mob.hurtDuration * 2;
        float f = mob.tickCount + IronsGeoEntityRenderer.partialTickOf(info.renderState());
        IronsGeoEntityRenderer.withTint(info.renderState(), RenderHelper.colorf(.15f * alpha, .02f * alpha, 0f), LightCoordsUtil.FULL_BRIGHT,
                () -> getRenderer().submitRenderTasks(info, collector.order(1), RenderTypes.energySwirl(TEXTURE, f * 0.02F % 1.0F, f * 0.01F % 1.0F)));
    }
}
