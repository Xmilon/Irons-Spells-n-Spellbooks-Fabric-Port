package io.redspace.ironsspellbooks.item.armor;

import net.minecraft.world.item.equipment.ArmorMaterial;

import net.minecraft.world.item.equipment.ArmorType;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.registries.ArmorMaterialRegistry;
import net.minecraft.resources.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.geckolib.renderer.GeoArmorRenderer;

public class ElectromancerArmorItem extends ImbuableChestplateArmorItem implements IArmorCapeProvider {
    public ElectromancerArmorItem(ArmorType slot, Properties settings) {
        super(ArmorMaterialRegistry.SCHOOL, slot, settings, schoolAttributes(AttributeRegistry.LIGHTNING_SPELL_POWER));
    }

    @Override
    public Identifier getCapeIdentifier() {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/models/armor/electromancer_cape.png");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public GeoArmorRenderer<?, ?> supplyRenderer() {
        return ArmorClientRenderers.create(this);
    }


}
