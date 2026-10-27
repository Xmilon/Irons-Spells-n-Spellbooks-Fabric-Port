package io.redspace.ironsspellbooks.entity.armor;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.item.armor.PlaguedArmorItem;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;

public class PlaguedArmorModel extends GeoModel<PlaguedArmorItem> {

    public PlaguedArmorModel() {
        super();

    }

    @Override
    public Identifier getModelResource(PlaguedArmorItem object) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "geo/plagued_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(PlaguedArmorItem object) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/models/armor/plagued.png");
    }

    @Override
    public Identifier getAnimationResource(PlaguedArmorItem animatable) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "animations/wizard_armor_animation.json");
    }
//    public static String listOfBonesToString(List<IBone> list){
//        String s = "";
//        for (IBone o:list)
//            s += o.getName()+", ";
//        return s;
//    }
}
