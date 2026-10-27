package io.redspace.ironsspellbooks.entity.spells.summoned_weapons;

import com.geckolib.renderer.base.GeoRenderState;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;

public class SummonedRapierModel extends GeoModel<SummonedWeaponEntity> {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/summoned_weapons/summoned_rapier.png");
    public static final Identifier MODEL = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "geo/summoned_rapier.geo.json");
    public static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "animations/summoned_weapon_animations.json");

    @Override
    public Identifier getModelResource(GeoRenderState state) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(SummonedWeaponEntity animatable) {
        return ANIMATIONS;
    }
}
