package io.redspace.ironsspellbooks.entity.mobs.necromancer;


import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.resources.Identifier;

public class NecromancerModel extends AbstractSpellCastingMobModel {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/necromancer.png");
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "geo/skeleton_mob.geo.json");

    @Override
    public Identifier getTextureResource(AbstractSpellCastingMob object) {
        return TEXTURE;
    }

    @Override
    public Identifier getModelResource(AbstractSpellCastingMob object) {
        return MODEL;
    }
}
