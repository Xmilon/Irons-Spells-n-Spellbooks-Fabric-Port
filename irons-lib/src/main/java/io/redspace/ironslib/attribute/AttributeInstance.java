package io.redspace.ironslib.attribute;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;

public record AttributeInstance(Holder<Attribute> attribute, AttributeModifier modifier, EquipmentSlotGroup slot) {
   public static AttributeInstance from(Entry entry) {
      return new AttributeInstance(entry.attribute(), entry.modifier(), entry.slot());
   }

   public static AttributeInstance of(Holder<Attribute> attribute, AttributeModifier modifier) {
      return new AttributeInstance(attribute, modifier, EquipmentSlotGroup.ANY);
   }
}
