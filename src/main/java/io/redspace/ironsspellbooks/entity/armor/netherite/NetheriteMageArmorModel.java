package io.redspace.ironsspellbooks.entity.armor.netherite;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.item.armor.NetheriteMageArmorItem;
import net.minecraft.resources.Identifier;
import com.geckolib.model.DefaultedItemGeoModel;

public class NetheriteMageArmorModel extends DefaultedItemGeoModel<NetheriteMageArmorItem> {

    public NetheriteMageArmorModel() {
        super(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, ""));
    }

    @Override
    public Identifier getModelResource(NetheriteMageArmorItem object) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "geo/netherite_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(NetheriteMageArmorItem object) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/models/armor/netherite.png");
    }

    @Override
    public Identifier getAnimationResource(NetheriteMageArmorItem animatable) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "animations/wizard_armor_animation.json");
    }
}
