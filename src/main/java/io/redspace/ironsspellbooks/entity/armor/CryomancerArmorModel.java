package io.redspace.ironsspellbooks.entity.armor;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.item.armor.CryomancerArmorItem;
import net.minecraft.resources.Identifier;
import com.geckolib.model.DefaultedItemGeoModel;

public class CryomancerArmorModel extends DefaultedItemGeoModel<CryomancerArmorItem> {

    public CryomancerArmorModel() {
        super(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, ""));
    }

    @Override
    public Identifier getModelResource(CryomancerArmorItem object) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "geo/cryomancer_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(CryomancerArmorItem object) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/models/armor/cryomancer.png");
    }

    @Override
    public Identifier getAnimationResource(CryomancerArmorItem animatable) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "animations/wizard_armor_animation.json");
    }
}
