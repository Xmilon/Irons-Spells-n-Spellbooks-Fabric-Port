package io.redspace.ironsspellbooks.item.armor;

import net.minecraft.world.item.equipment.ArmorMaterial;

import net.minecraft.world.item.equipment.ArmorType;

import io.redspace.ironsspellbooks.registries.ArmorMaterialRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import com.geckolib.renderer.GeoArmorRenderer;

public class NetheriteMageArmorItem extends ImbuableChestplateArmorItem {
    public NetheriteMageArmorItem(ArmorType type, Properties settings) {
        super(ArmorMaterialRegistry.NETHERITE_BATTLEMAGE, type, settings, withManaAndSpellPowerAttribute(125, 0.05));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public GeoArmorRenderer<?, ?> supplyRenderer() {
        return ArmorClientRenderers.create(this);
    }

}
