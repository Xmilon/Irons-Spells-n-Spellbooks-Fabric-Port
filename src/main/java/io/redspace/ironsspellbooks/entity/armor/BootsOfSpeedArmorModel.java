package io.redspace.ironsspellbooks.entity.armor;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.item.armor.BootsOfSpeedArmorItem;
import net.minecraft.resources.Identifier;
import com.geckolib.model.DefaultedItemGeoModel;

public class BootsOfSpeedArmorModel extends DefaultedItemGeoModel<BootsOfSpeedArmorItem> {

    public BootsOfSpeedArmorModel() {
        super(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, ""));
    }

    @Override
    public Identifier getModelResource(BootsOfSpeedArmorItem object) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "geo/boots_of_speed.geo.json");
    }

    @Override
    public Identifier getTextureResource(BootsOfSpeedArmorItem object) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/models/armor/boots_of_speed.png");
    }

    @Override
    public Identifier getAnimationResource(BootsOfSpeedArmorItem animatable) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "animations/wizard_armor_animation.json");
    }
}
