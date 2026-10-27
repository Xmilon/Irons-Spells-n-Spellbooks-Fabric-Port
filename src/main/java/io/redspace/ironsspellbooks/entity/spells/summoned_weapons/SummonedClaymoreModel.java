package io.redspace.ironsspellbooks.entity.spells.summoned_weapons;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;

public class SummonedClaymoreModel extends GeoModel<SummonedWeaponEntity> {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/summoned_weapons/summoned_claymore.png");
    public static final Identifier MODEL = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "geo/summoned_claymore.geo.json");
    public static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "animations/summoned_weapon_animations.json");

    @Override
    public Identifier getModelResource(SummonedWeaponEntity animatable) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(SummonedWeaponEntity animatable) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(SummonedWeaponEntity animatable) {
        return ANIMATIONS;
    }
}
