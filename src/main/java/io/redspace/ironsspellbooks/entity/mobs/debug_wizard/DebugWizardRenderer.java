package io.redspace.ironsspellbooks.entity.mobs.debug_wizard;

import com.geckolib.renderer.base.GeoRenderState;


import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import io.redspace.ironsspellbooks.render.DebugWizardSpellName;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

public class DebugWizardRenderer extends AbstractSpellCastingMobRenderer {

    //public static ModelLayerLocation PYROMANCER_MODEL_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(irons_spellbooks.MODID, "pyromancer"), "body");
    //public static ModelLayerLocation PYROMANCER_INNER_ARMOR = new ModelLayerLocation(Identifier.fromNamespaceAndPath(irons_spellbooks.MODID, "pyromancer"), "inner_armor");
    //public static ModelLayerLocation PYROMANCER_OUTER_ARMOR = new ModelLayerLocation(Identifier.fromNamespaceAndPath(irons_spellbooks.MODID, "pyromancer"), "outer_armor");

    public DebugWizardRenderer(EntityRendererProvider.Context context) {
        super(context, new AbstractSpellCastingMobModel() {
            @Override
            public Identifier getTextureResource(GeoRenderState state) {
                return AbstractSpellCastingMob.textureResource;
            }
        });
        withRenderLayer(new DebugWizardSpellName(this));
    }
}
