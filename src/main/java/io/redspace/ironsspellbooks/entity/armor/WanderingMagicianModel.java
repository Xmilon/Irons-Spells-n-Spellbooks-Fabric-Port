package io.redspace.ironsspellbooks.entity.armor;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.item.armor.WanderingMagicianArmorItem;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;

public class WanderingMagicianModel extends GeoModel<WanderingMagicianArmorItem> {

    public WanderingMagicianModel() {
        super();

    }

    @Override
    public Identifier getModelResource(WanderingMagicianArmorItem object) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "geo/wandering_magician_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(WanderingMagicianArmorItem object) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/models/armor/wandering_magician.png");
    }

    @Override
    public Identifier getAnimationResource(WanderingMagicianArmorItem animatable) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "animations/wizard_armor_animation.json");
    }
//    public static String listOfBonesToString(List<IBone> list){
//        String s = "";
//        for (IBone o:list)
//            s += o.getName()+", ";
//        return s;
//    }
}
