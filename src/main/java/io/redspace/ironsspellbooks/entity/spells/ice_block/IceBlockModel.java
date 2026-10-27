package io.redspace.ironsspellbooks.entity.spells.ice_block;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;

public class IceBlockModel extends GeoModel<IceBlockProjectile> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/ice_block.png");
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "geo/ice_block_projectile.geo.json");
    public static final Identifier ANIMS = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "animations/ice_block_animations.json");


    public IceBlockModel() {
    }

    @Override
    public Identifier getTextureResource(IceBlockProjectile object) {
        return TEXTURE;
    }

    @Override
    public Identifier getModelResource(IceBlockProjectile object) {
        return MODEL;
    }

    @Override
    public Identifier getAnimationResource(IceBlockProjectile animatable) {
        return ANIMS;
    }
}
