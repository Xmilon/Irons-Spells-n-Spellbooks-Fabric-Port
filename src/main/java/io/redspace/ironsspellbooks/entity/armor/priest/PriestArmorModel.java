package io.redspace.ironsspellbooks.entity.armor.priest;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.item.armor.PriestArmorItem;
import net.minecraft.resources.Identifier;
import com.geckolib.model.DefaultedItemGeoModel;

public class PriestArmorModel extends DefaultedItemGeoModel<PriestArmorItem> {

    public PriestArmorModel() {
        super(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "armor/priest"));
    }

    @Override
    public Identifier getModelResource(PriestArmorItem object) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "geo/priest_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(PriestArmorItem object) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/models/armor/priest.png");
    }

    @Override
    public Identifier getAnimationResource(PriestArmorItem animatable) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "animations/wizard_armor_animation.json");
    }
}
