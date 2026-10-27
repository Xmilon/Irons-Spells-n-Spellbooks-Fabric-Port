package io.redspace.ironsspellbooks.entity.mobs.keeper;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.resources.Identifier;

public class KeeperModel extends AbstractSpellCastingMobModel {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/keeper/keeper.png");
    public static final Identifier TEXTURE_RESTORED = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/keeper/keeper_restored.png");
    public static final Identifier modelResource = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "geo/citadel_keeper.geo.json");

    @Override
    public Identifier getTextureResource(AbstractSpellCastingMob object) {
        return object instanceof KeeperEntity keeper && keeper.isRestored() ? TEXTURE_RESTORED : TEXTURE;
    }

    @Override
    public Identifier getModelResource(AbstractSpellCastingMob object) {
        return modelResource;
    }
}
