package io.redspace.ironsspellbooks.entity.armor.pumpkin;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.item.armor.PumpkinArmorItem;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;

public class PumpkinArmorModel extends GeoModel<PumpkinArmorItem> {

    public PumpkinArmorModel() {
        super();

    }

    @Override
    public Identifier getModelResource(PumpkinArmorItem object) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "geo/pumpkin_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(PumpkinArmorItem object) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/models/armor/pumpkin.png");
    }

    @Override
    public Identifier getAnimationResource(PumpkinArmorItem animatable) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "animations/wizard_armor_animation.json");
    }
//    public static String listOfBonesToString(List<IBone> list){
//        String s = "";
//        for (IBone o:list)
//            s += o.getName()+", ";
//        return s;
//    }
}
