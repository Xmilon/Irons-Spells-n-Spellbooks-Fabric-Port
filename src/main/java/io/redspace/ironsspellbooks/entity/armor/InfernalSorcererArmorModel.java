package io.redspace.ironsspellbooks.entity.armor;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.item.armor.InfernalSorcererArmorItem;
import net.minecraft.resources.Identifier;
import com.geckolib.model.DefaultedItemGeoModel;

public class InfernalSorcererArmorModel extends DefaultedItemGeoModel<InfernalSorcererArmorItem> {

    public InfernalSorcererArmorModel() {
        super(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, ""));
    }

    @Override
    public Identifier getModelResource(InfernalSorcererArmorItem object) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "geo/infernal_sorcerer.geo.json");
    }

    @Override
    public Identifier getTextureResource(InfernalSorcererArmorItem object) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/models/armor/infernal_sorcerer.png");
    }

    @Override
    public Identifier getAnimationResource(InfernalSorcererArmorItem animatable) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "animations/wizard_armor_animation.json");
    }
}
