package io.redspace.ironsspellbooks.item.armor;

import net.minecraft.world.item.equipment.ArmorMaterial;

import net.minecraft.world.item.equipment.ArmorType;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.registries.ArmorMaterialRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import com.geckolib.renderer.GeoArmorRenderer;

public class CryomancerArmorItem extends ImbuableChestplateArmorItem {
    public CryomancerArmorItem(ArmorType slot, Properties settings) {
        super(ArmorMaterialRegistry.SCHOOL, slot, settings, schoolAttributes(AttributeRegistry.ICE_SPELL_POWER));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public GeoArmorRenderer<?, ?> supplyRenderer() {
        return ArmorClientRenderers.create(this);
    }
}
