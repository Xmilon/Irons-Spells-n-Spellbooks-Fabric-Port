package io.redspace.ironsspellbooks.entity.armor;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.item.armor.ShadowwalkerArmorItem;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;

public class ShadowwalkerArmorModel extends GeoModel<ShadowwalkerArmorItem> {

    public ShadowwalkerArmorModel() {
        super();

    }

    @Override
    public Identifier getModelResource(ShadowwalkerArmorItem object) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "geo/shadowwalker_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(ShadowwalkerArmorItem object) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/models/armor/shadowwalker.png");
    }

    @Override
    public Identifier getAnimationResource(ShadowwalkerArmorItem animatable) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "animations/wizard_armor_animation.json");
    }
}
