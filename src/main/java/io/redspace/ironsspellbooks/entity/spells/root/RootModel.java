package io.redspace.ironsspellbooks.entity.spells.root;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;

public class RootModel extends GeoModel<RootEntity> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/root.png");
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "geo/root.geo.json");
    public static final Identifier ANIMS = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "animations/root_animations.json");

    public RootModel() {
    }

    @Override
    public Identifier getTextureResource(RootEntity object) {
        return TEXTURE;
    }

    @Override
    public Identifier getModelResource(RootEntity object) {
        return MODEL;
    }

    @Override
    public Identifier getAnimationResource(RootEntity animatable) {
        return ANIMS;
    }
}
