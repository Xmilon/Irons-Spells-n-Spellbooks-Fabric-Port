package io.redspace.ironslib.util;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;

public class Utils {
   public static UUID uuidFromUndashed(String s) {
      if (s != null && s.length() == 32) {
         String dashed = s.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{12})", "$1-$2-$3-$4-$5");
         return UUID.fromString(dashed);
      } else {
         throw new IllegalArgumentException("Invalid UUID string");
      }
   }

   public static void spawnParticles(
      Level level, ParticleOptions particle, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed, boolean force
   ) {
      ((ServerLevel)level).sendParticles(particle, force, force, x, y, z, count, deltaX, deltaY, deltaZ, speed);
   }

   public static boolean isEquippable(ItemStack stack) {
      return stack.has(DataComponents.EQUIPPABLE);
   }

   public static boolean isEquippable(ItemStack stack, Predicate<EquipmentSlot> slot) {
      Equippable equippable = (Equippable)stack.get(DataComponents.EQUIPPABLE);
      return equippable != null && slot.test(equippable.slot());
   }

   public static boolean isEquippableInSlot(ItemStack stack, EquipmentSlot slot) {
      Equippable equippable = (Equippable)stack.get(DataComponents.EQUIPPABLE);
      return equippable != null && equippable.slot().equals(slot);
   }

   public static boolean isEquippableInSlot(ItemStack stack, Collection<EquipmentSlot> slot) {
      Equippable equippable = (Equippable)stack.get(DataComponents.EQUIPPABLE);
      return equippable != null && slot.contains(equippable.slot());
   }
}
