package io.redspace.ironsspellbooks.entity.armor;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.item.armor.ArchevokerArmorItem;
import net.minecraft.resources.Identifier;
import com.geckolib.model.DefaultedItemGeoModel;

public class ArchevokerArmorModel extends DefaultedItemGeoModel<ArchevokerArmorItem> {

    public ArchevokerArmorModel() {
        super(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, ""));
    }

    @Override
    public Identifier getModelResource(ArchevokerArmorItem object) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "geo/archevoker_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(ArchevokerArmorItem object) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/models/armor/archevoker.png");
    }

    @Override
    public Identifier getAnimationResource(ArchevokerArmorItem animatable) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "animations/wizard_armor_animation.json");
    }
//    public static String listOfBonesToString(List<IBone> list){
//        String s = "";
//        for (IBone o:list)
//            s += o.getName()+", ";
//        return s;
//    }
}
