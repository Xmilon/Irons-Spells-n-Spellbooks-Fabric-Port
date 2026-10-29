package io.redspace.ironsspellbooks.entity.armor.priest;

import com.geckolib.renderer.base.GeoRenderState;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.item.armor.PriestArmorItem;
import net.minecraft.resources.Identifier;
import com.geckolib.model.DefaultedItemGeoModel;

public class PriestArmorModel extends DefaultedItemGeoModel<PriestArmorItem> {

    public PriestArmorModel() {
        super(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "armor/priest"));
    }

    @Override
    public Identifier getModelResource(GeoRenderState state) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "priest_armor");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/models/armor/priest.png");
    }

    @Override
    public Identifier getAnimationResource(PriestArmorItem animatable) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "wizard_armor_animation");
    }
}
