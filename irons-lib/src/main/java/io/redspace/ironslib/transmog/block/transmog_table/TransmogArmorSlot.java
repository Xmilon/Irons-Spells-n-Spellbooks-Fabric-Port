package io.redspace.ironslib.transmog.block.transmog_table;

import io.redspace.ironslib.IronsLib;
import java.util.Map;
import net.minecraft.resources.Identifier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class TransmogArmorSlot extends Slot {
   private final LivingEntity owner;
   private final EquipmentSlot slot;
   private final Identifier emptyIcon;
   private static final Map<EquipmentSlot, Identifier> TEXTURE_EMPTY_SLOTS = Map.of(
      EquipmentSlot.FEET,
      IronsLib.id("transmog_table/empty_armor_slot_boots"),
      EquipmentSlot.LEGS,
      IronsLib.id("transmog_table/empty_armor_slot_leggings"),
      EquipmentSlot.CHEST,
      IronsLib.id("transmog_table/empty_armor_slot_chestplate"),
      EquipmentSlot.HEAD,
      IronsLib.id("transmog_table/empty_armor_slot_helmet")
   );

   public TransmogArmorSlot(Container container, LivingEntity owner, EquipmentSlot slot, int slotIndex, int x, int y) {
      super(container, slotIndex, x, y);
      this.owner = owner;
      this.slot = slot;
      this.emptyIcon = TEXTURE_EMPTY_SLOTS.get(slot);
   }

   public void setByPlayer(ItemStack newStack, ItemStack oldStack) {
      this.owner.onEquipItem(this.slot, oldStack, newStack);
      super.setByPlayer(newStack, oldStack);
   }

   public int getMaxStackSize() {
      return 1;
   }

   public boolean mayPlace(ItemStack stack) {
      var equippable = stack.get(DataComponents.EQUIPPABLE);
      return equippable != null && equippable.slot() == this.slot
         && equippable.canBeEquippedBy(this.owner.getType().builtInRegistryHolder());
   }

   public boolean mayPickup(Player player) {
      ItemStack itemstack = this.getItem();
      return (itemstack.isEmpty() || player.isCreative() || !EnchantmentHelper.has(itemstack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE))
         && super.mayPickup(player);
   }

   public Identifier getEmptyIcon() {
      return this.emptyIcon;
   }
}
