package io.redspace.ironsspellbooks.entity.spells;

import com.geckolib.renderer.base.GeoRenderState;


import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.player.SpinAttackType;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;

public class SpinAttackModel extends GeoModel<AbstractSpellCastingMob> {
    public static final Identifier FIRE_TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/fire_riptide.png");
    public static final Identifier LIGHTNING_TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/lightning_riptide.png");
    public static final Identifier DEFAULT_TEXTURE = Identifier.withDefaultNamespace("textures/entity/trident_riptide.png");

    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "spin_attack_model");

    public SpinAttackModel() {
    }

    @Override
    public Identifier getTextureResource(AbstractSpellCastingMob object) {
        SpinAttackType spinAttackType = ClientMagicData.getSyncedSpellData(object).getSpinAttackType();
        return spinAttackType.textureId();
    }

    @Override
    public Identifier getAnimationResource(AbstractSpellCastingMob animatable) {
        return null;
    }

    @Override
    public Identifier getModelResource(GeoRenderState state) {
        return MODEL;
    }

}
