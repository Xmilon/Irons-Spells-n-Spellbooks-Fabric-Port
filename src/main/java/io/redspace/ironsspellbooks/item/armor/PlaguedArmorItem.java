package io.redspace.ironsspellbooks.item.armor;

import net.minecraft.world.item.equipment.ArmorMaterial;

import net.minecraft.world.item.equipment.ArmorType;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.registries.ArmorMaterialRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.geckolib.renderer.GeoArmorRenderer;

public class PlaguedArmorItem extends ImbuableChestplateArmorItem {
    public PlaguedArmorItem(ArmorType slot, Properties settings) {
        super(ArmorMaterialRegistry.SCHOOL, slot, settings, schoolAttributes(AttributeRegistry.NATURE_SPELL_POWER));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public GeoArmorRenderer<?, ?> supplyRenderer() {
        return ArmorClientRenderers.create(this);
    }
}
