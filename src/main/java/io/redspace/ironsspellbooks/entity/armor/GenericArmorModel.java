package io.redspace.ironsspellbooks.entity.armor;

import com.geckolib.renderer.base.GeoRenderState;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.item.armor.ExtendedArmorItem;
import net.minecraft.resources.Identifier;
import com.geckolib.model.DefaultedItemGeoModel;
import java.util.Map;

public class GenericArmorModel<T extends ExtendedArmorItem> extends DefaultedItemGeoModel<T> {
    private final Identifier model; //(IronsSpellbooks.MODID, "geo/shadowwalker_armor.geo.json");

    private final Identifier texture; //(IronsSpellbooks.MODID, "textures/models/armor/shadowwalker.png");

    private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "animations/wizard_armor_animation.json");

    public GenericArmorModel(String modid, String name) {
        super(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, ""));
        this.model = Identifier.fromNamespaceAndPath(modid, String.format("geo/%s_armor.geo.json", name));
        this.texture = Identifier.fromNamespaceAndPath(modid, String.format("textures/models/armor/%s.png", name));
    }

    public GenericArmorModel(String name) {
        this(IronsSpellbooks.MODID, name);
    }

    public GenericArmorModel<T> variants(Map<String, Identifier> modelVariants) {
        // Variant support is currently disabled on this loader path until per-stack renderer context is wired.
        return this;
    }

    @Override
    public Identifier getModelResource(GeoRenderState state) {
        return model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        return texture;
    }

    @Override
    public Identifier getAnimationResource(T animatable) {
        return ANIMATION;
    }
}
