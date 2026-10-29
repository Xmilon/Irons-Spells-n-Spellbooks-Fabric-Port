package io.redspace.ironsspellbooks.item.armor;

import net.minecraft.world.item.equipment.ArmorMaterial;

import net.minecraft.world.item.equipment.ArmorType;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.registries.ArmorMaterialRegistry;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;

import net.minecraft.world.item.ItemStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;
import com.geckolib.renderer.GeoArmorRenderer;

import java.util.Map;

public class WizardArmorItem extends ImbuableChestplateArmorItem implements IDisableJacket {
    private static final String descIdHat = "item.irons_spellbooks.wizard_helmet.hat";
    private static final String descIdHood = "item.irons_spellbooks.wizard_helmet.hood";

    public WizardArmorItem(ArmorType type, Properties settings) {
        super(ArmorMaterialRegistry.SCHOOL, type, settings, withManaAndSpellPowerAttribute(125, 0.05));
    }

    @Override
    public @NotNull net.minecraft.network.chat.Component getName(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof ExtendedArmorItem armorItem) || armorItem.getArmorType() != ArmorType.HELMET) {
            return super.getName(stack);
        } else {
            return net.minecraft.network.chat.Component.translatable(stack.getOrDefault(ComponentRegistry.CLOTHING_VARIANT, "").equals("hat") ? descIdHat : descIdHood);
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public GeoArmorRenderer<?, ?> supplyRenderer() {
        return ArmorClientRenderers.create(this);
    }
}
