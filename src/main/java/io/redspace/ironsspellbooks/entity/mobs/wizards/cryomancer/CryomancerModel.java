package io.redspace.ironsspellbooks.entity.mobs.wizards.cryomancer;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.resources.Identifier;

public class CryomancerModel extends AbstractSpellCastingMobModel {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/cryomancer.png");

    @Override
    public Identifier getTextureResource(AbstractSpellCastingMob object) {
        return TEXTURE;
    }

}
