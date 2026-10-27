package io.redspace.ironslib.transmog;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.redspace.ironslib.patreon.data.PatreonPermissions;
import io.redspace.ironslib.util.Utils;
import java.util.Set;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public record TransmogHolder(Identifier id, PatreonPermissions requiredPermission, Set<EquipmentSlot> supportedSlots, TransmogHolder.DyeConfig dyeConfig) {
   public static final Set<EquipmentSlot> ALL_SLOTS = Set.of(EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD);
   public static final Codec<TransmogHolder> CODEC = Identifier.CODEC
      .xmap(TransmogManager::get, TransmogHolder::id)
      .validate(holder -> holder == null ? DataResult.error(() -> "Unknown transmog holder attempted to load") : DataResult.success(holder));

   public TransmogHolder(Identifier id, PatreonPermissions permissions) {
      this(id, permissions, ALL_SLOTS, TransmogHolder.DyeConfig.NONE);
   }

   public boolean supportsSlot(EquipmentSlot slot) {
      return this.supportedSlots.contains(slot);
   }

   public boolean supportsSlot(ItemStack itemStack) {
      return Utils.isEquippableInSlot(itemStack, this.supportedSlots);
   }

   @Override
   public boolean equals(Object obj) {
      return this == obj || obj instanceof TransmogHolder oth && this.id.equals(oth.id);
   }

   @Override
   public int hashCode() {
      return this.id.hashCode();
   }

   public String descriptionId() {
      return String.format("transmog.%s.%s", this.id.getNamespace(), this.id.getPath());
   }

   public record DyeConfig(boolean dyeable, int defaultColor) {
      public static final TransmogHolder.DyeConfig NONE = new TransmogHolder.DyeConfig(false, -1);
   }
}
