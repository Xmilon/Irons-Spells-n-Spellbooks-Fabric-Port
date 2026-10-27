package io.redspace.ironsspellbooks.entity.mobs.keeper;

import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.render.IronsGeoEntityRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class GeoKeeperGhostLayer extends GeoRenderLayer<AbstractSpellCastingMob, Void, EntityRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/keeper/keeper_ghost.png");

    public GeoKeeperGhostLayer(GeoRenderer<AbstractSpellCastingMob, Void, EntityRenderState> renderer) {
        super(renderer);
    }

    // shortcut: the hurt ghost re-draws the full model without the old red tint fade or per-bone shrink; a pass can only use the main render color.
    @Override
    public void submitRenderTask(RenderPassInfo<EntityRenderState> info, SubmitNodeCollector collector) {
        AbstractSpellCastingMob mob = IronsGeoEntityRenderer.entityOf(info.renderState());
        if (!info.willRender() || mob.hurtTime <= 0) {
            return;
        }
        float f = mob.tickCount + IronsGeoEntityRenderer.partialTickOf(info.renderState());
        getRenderer().submitRenderTasks(info, collector.order(1), RenderTypes.energySwirl(TEXTURE, f * 0.02F % 1.0F, f * 0.01F % 1.0F));
    }
}
