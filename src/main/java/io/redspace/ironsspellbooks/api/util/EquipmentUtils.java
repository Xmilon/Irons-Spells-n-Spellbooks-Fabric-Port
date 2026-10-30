package io.redspace.ironsspellbooks.api.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public final class EquipmentUtils {
    private EquipmentUtils() {}

    public static boolean isArmor(ItemStack stack) {
        var equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable == null || !equippable.slot().isArmor()) return false;
        return stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY).modifiers().stream()
                .anyMatch(entry -> entry.attribute().equals(Attributes.ARMOR) && entry.slot().test(equippable.slot()));
    }
}
