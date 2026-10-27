package io.redspace.ironslib.transmog;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import io.redspace.ironslib.registry.IronsLibRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record TransmogItemData(TransmogHolder transmog, int dyeColor) {
   public static final Codec<TransmogItemData> CODEC = RecordCodecBuilder.create(
      builder -> builder.group(
            TransmogHolder.CODEC.fieldOf("transmog").forGetter(TransmogItemData::transmog),
            Codec.INT.optionalFieldOf("dye_color", -1).forGetter(TransmogItemData::dyeColor)
         )
         .apply(builder, TransmogItemData::new)
   );
   public static final StreamCodec<ByteBuf, TransmogItemData> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

   public TransmogItemData(TransmogHolder transmog) {
      this(transmog, transmog.dyeConfig().defaultColor());
   }

   public static boolean has(ItemStack stack) {
      return stack.has(IronsLibRegistries.ComponentRegistry.TRANSMOG);
   }

   public static void set(ItemStack stack, TransmogItemData holder) {
      stack.set(IronsLibRegistries.ComponentRegistry.TRANSMOG, holder);
   }

   @Nullable
   public static TransmogItemData get(ItemStack stack) {
      return (TransmogItemData)stack.get(IronsLibRegistries.ComponentRegistry.TRANSMOG);
   }

   public static void remove(ItemStack stack) {
      stack.remove(IronsLibRegistries.ComponentRegistry.TRANSMOG);
   }

   @Override
   public boolean equals(Object obj) {
      return obj == this || obj instanceof TransmogItemData data && this.transmog.equals(data.transmog) && this.dyeColor == data.dyeColor;
   }

   @Override
   public int hashCode() {
      return this.transmog.hashCode() + this.dyeColor * 31;
   }
}
