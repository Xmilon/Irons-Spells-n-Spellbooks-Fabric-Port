package io.redspace.ironsspellbooks.entity.spells.wisp;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;

public class WispModel extends GeoModel<WispEntity> {
    public static final Identifier modelResource = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "geo/wisp.geo.json");
    public static final Identifier textureResource = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/wisp/wisp.png");
    public static final Identifier animationResource = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "animations/wisp.animation.json");


    @Override
    public Identifier getModelResource(WispEntity object) {
        return modelResource;
    }

    @Override
    public Identifier getTextureResource(WispEntity object) {
        return textureResource;
    }

    @Override
    public Identifier getAnimationResource(WispEntity animatable) {
        return animationResource;
    }
}
