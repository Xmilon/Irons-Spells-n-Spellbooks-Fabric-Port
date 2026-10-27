package io.redspace.ironsspellbooks.entity.mobs.necromancer;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public class NecromancerRenderer extends AbstractSpellCastingMobRenderer {

    public NecromancerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new NecromancerModel());
    }

    @Override
    public RenderType getRenderType(AbstractSpellCastingMob animatable, Identifier texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return animatable.isInvisible() ? RenderType.entityTranslucent(texture) : RenderType.entityCutoutNoCull(texture);
    }
}
