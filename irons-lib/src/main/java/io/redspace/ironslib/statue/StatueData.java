package io.redspace.ironslib.statue;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.redspace.ironslib.registry.IronsLibRegistries;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record StatueData(UUID uuid, PlayerStatuePose pose, boolean flipped) {
   public static final Codec<StatueData> CODEC = RecordCodecBuilder.create(
      builder -> builder.group(
            UUIDUtil.CODEC.fieldOf("uuid").forGetter(StatueData::uuid),
            PlayerStatuePose.CODEC.fieldOf("pose").forGetter(StatueData::pose),
            Codec.BOOL.optionalFieldOf("flipped", false).forGetter(StatueData::flipped)
         )
         .apply(builder, StatueData::new)
   );

   public StatueData updateUUID(UUID uuid) {
      return new StatueData(uuid, this.pose, this.flipped);
   }

   public StatueData updatePose(PlayerStatuePose pose) {
      return new StatueData(this.uuid, pose, this.flipped);
   }

   public StatueData updateFlipped(boolean flipped) {
      return new StatueData(this.uuid, this.pose, flipped);
   }

   public static boolean has(ItemStack stack) {
      return stack.has(IronsLibRegistries.ComponentRegistry.STATUE_ITEM_DATA);
   }

   public static void set(ItemStack stack, StatueData holder) {
      stack.set(IronsLibRegistries.ComponentRegistry.STATUE_ITEM_DATA, holder);
   }

   @Nullable
   public static StatueData get(ItemStack stack) {
      return (StatueData)stack.get(IronsLibRegistries.ComponentRegistry.STATUE_ITEM_DATA);
   }

   public static void remove(ItemStack stack) {
      stack.remove(IronsLibRegistries.ComponentRegistry.STATUE_ITEM_DATA);
   }

   @Override
   public int hashCode() {
      return this.uuid.hashCode() * 31 + this.pose.getSerializedName().hashCode() + (this.flipped ? 0 : 1);
   }

   @Override
   public boolean equals(Object obj) {
      return obj == this || obj instanceof StatueData data && data.uuid.equals(this.uuid) && data.pose.equals(this.pose) && data.flipped == this.flipped;
   }
}
