package io.redspace.ironsspellbooks.entity.mobs.wizards.archevoker;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.resources.Identifier;

public class ArchevokerModel extends AbstractSpellCastingMobModel {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/archevoker.png");
    public static final Identifier MODEL = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "geo/archevoker.geo.json");

    @Override
    public Identifier getModelResource(AbstractSpellCastingMob object) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(AbstractSpellCastingMob object) {
        return TEXTURE;
    }

}
