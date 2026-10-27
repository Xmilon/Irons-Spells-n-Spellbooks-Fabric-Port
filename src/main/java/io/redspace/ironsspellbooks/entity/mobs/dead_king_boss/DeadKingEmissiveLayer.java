package io.redspace.ironsspellbooks.entity.mobs.dead_king_boss;

import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.render.IronsGeoEntityRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class DeadKingEmissiveLayer extends GeoRenderLayer<AbstractSpellCastingMob, Void, EntityRenderState> {
    public static final Identifier TEXTURE_NORMAL = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/dead_king/dead_king_glowing.png");
    public static final Identifier TEXTURE_ENRAGED = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/dead_king/dead_king_enraged_glowing.png");

    public DeadKingEmissiveLayer(GeoRenderer<AbstractSpellCastingMob, Void, EntityRenderState> renderer) {
        super(renderer);
    }

    public static Identifier currentTexture(AbstractSpellCastingMob entity) {
        return entity instanceof DeadKingBoss boss && boss.isPhase(DeadKingBoss.Phases.FinalPhase) ? TEXTURE_ENRAGED : TEXTURE_NORMAL;
    }

    public static Identifier currentModel(AbstractSpellCastingMob deadKingBoss) {
        return DeadKingModel.MODEL;
    }

    public static RenderType renderType(Identifier resourceLocation) {
        return RenderTypes.energySwirl(resourceLocation, 0, 0);
    }

    @Override
    public void submitRenderTask(RenderPassInfo<EntityRenderState> info, SubmitNodeCollector collector) {
        AbstractSpellCastingMob mob = IronsGeoEntityRenderer.entityOf(info.renderState());
        if (!info.willRender() || mob instanceof DeadKingCorpseEntity || mob.isInvisible()) {
            return;
        }
        getRenderer().submitRenderTasks(info, collector.order(1), renderType(currentTexture(mob)));
    }
}
