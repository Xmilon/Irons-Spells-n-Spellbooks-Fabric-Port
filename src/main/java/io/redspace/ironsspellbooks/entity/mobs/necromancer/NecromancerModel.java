package io.redspace.ironsspellbooks.entity.mobs.necromancer;

import com.geckolib.renderer.base.GeoRenderState;


import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.resources.Identifier;

public class NecromancerModel extends AbstractSpellCastingMobModel {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/necromancer.png");
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "geo/skeleton_mob.geo.json");

    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        var object = mob(state);
        return TEXTURE;
    }

    @Override
    public Identifier getModelResource(GeoRenderState state) {
        var object = mob(state);
        return MODEL;
    }
}
