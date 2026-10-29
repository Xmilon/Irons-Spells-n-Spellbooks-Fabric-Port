package io.redspace.ironsspellbooks.entity.armor;

import com.geckolib.renderer.base.GeoRenderState;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.item.armor.GoldCrownArmorItem;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;

public class GoldCrownModel extends GeoModel<GoldCrownArmorItem> {

    public GoldCrownModel() {
        super();

    }

    @Override
    public Identifier getModelResource(GeoRenderState state) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "tarnished_armor");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/models/armor/gold_crown.png");
    }

    @Override
    public Identifier getAnimationResource(GoldCrownArmorItem animatable) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "wizard_armor_animation");
    }
}
