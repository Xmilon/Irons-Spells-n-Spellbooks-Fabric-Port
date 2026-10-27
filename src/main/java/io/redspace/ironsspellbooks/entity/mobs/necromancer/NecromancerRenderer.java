package io.redspace.ironsspellbooks.entity.mobs.necromancer;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public class NecromancerRenderer extends AbstractSpellCastingMobRenderer {

    public NecromancerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new NecromancerModel());
    }

    @Override
    public RenderType getRenderType(EntityRenderState state, Identifier texture) {
        return entity(state).isInvisible() ? RenderTypes.entityTranslucent(texture) : RenderTypes.entityCutout(texture);
    }
}
