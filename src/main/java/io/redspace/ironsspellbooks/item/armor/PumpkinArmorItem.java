package io.redspace.ironsspellbooks.item.armor;

import net.minecraft.world.item.equipment.ArmorMaterial;

import net.minecraft.world.item.equipment.ArmorType;

import io.redspace.ironsspellbooks.registries.ArmorMaterialRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.geckolib.renderer.GeoArmorRenderer;

public class PumpkinArmorItem extends ExtendedArmorItem {
    public PumpkinArmorItem(ArmorType slot, Properties settings) {
        super(ArmorMaterialRegistry.PUMPKIN, slot, settings, withManaAttribute(75));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public GeoArmorRenderer<?, ?> supplyRenderer() {
        return ArmorClientRenderers.create(this);
    }
}
