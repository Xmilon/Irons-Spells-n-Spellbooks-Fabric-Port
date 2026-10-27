package io.redspace.ironslib.internal.mixin;

import io.redspace.ironslib.attribute.AttributeEventsHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemStack.class)
public abstract class ItemStackAttributeMixin {
   @ModifyVariable(
      method = {
         "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlotGroup;Lorg/apache/commons/lang3/function/TriConsumer;)V",
         "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V"
      },
      at = @At("STORE"), ordinal = 0, require = 2
   )
   private ItemAttributeModifiers irons_lib$remapModifiers(ItemAttributeModifiers modifiers) {
      return AttributeEventsHandler.remapModifiers(modifiers);
   }
}
