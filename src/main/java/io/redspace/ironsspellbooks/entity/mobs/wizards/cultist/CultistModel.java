package io.redspace.ironsspellbooks.entity.mobs.wizards.cultist;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.resources.Identifier;

public class CultistModel extends AbstractSpellCastingMobModel {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/cultist.png");

    @Override
    public Identifier getTextureResource(AbstractSpellCastingMob object) {
        return TEXTURE;
    }

}
