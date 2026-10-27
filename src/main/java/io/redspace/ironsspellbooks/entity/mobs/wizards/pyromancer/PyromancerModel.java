package io.redspace.ironsspellbooks.entity.mobs.wizards.pyromancer;

import com.geckolib.renderer.base.GeoRenderState;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.resources.Identifier;

public class PyromancerModel extends AbstractSpellCastingMobModel {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/pyromancer.png");

    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        var object = mob(state);
        return TEXTURE;
    }

}
