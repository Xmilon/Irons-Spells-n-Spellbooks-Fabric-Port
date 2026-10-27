package io.redspace.ironsspellbooks.entity.spells.echoing_strikes;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.model.GeoModel;

public class EchoingSwordModel<T extends Entity & GeoEntity> extends GeoModel<T> {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/summoned_weapons/summoned_claymore.png");
    public static final Identifier MODEL = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "geo/summoned_claymore.geo.json");
    public static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "animations/summoned_weapon_animations.json");

    @Override
    public Identifier getModelResource(T animatable) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(T animatable) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(T animatable) {
        return ANIMATIONS;
    }
}

