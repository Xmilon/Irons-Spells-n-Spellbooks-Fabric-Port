package io.redspace.ironsspellbooks.item.armor;

import net.minecraft.world.item.equipment.ArmorMaterial;

import net.minecraft.world.item.equipment.ArmorType;

import io.redspace.ironsspellbooks.registries.ArmorMaterialRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.geckolib.renderer.GeoArmorRenderer;

public class WanderingMagicianArmorItem extends ExtendedArmorItem {
    public WanderingMagicianArmorItem(ArmorType slot, Properties settings) {
        super(ArmorMaterialRegistry.WANDERING_MAGICIAN, slot, settings, withManaAttribute(25));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public GeoArmorRenderer<?, ?> supplyRenderer() {
        return ArmorClientRenderers.create(this);
    }
}
