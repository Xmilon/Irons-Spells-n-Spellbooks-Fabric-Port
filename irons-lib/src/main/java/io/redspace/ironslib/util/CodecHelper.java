package io.redspace.ironslib.util;

import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

public class CodecHelper {
   public static <T> void replaceLegacy(ItemStack stack, Supplier<DataComponentType<T>> legacy, Supplier<DataComponentType<T>> dataComponentType) {
      if (stack.has(legacy.get())) {
         T data = stack.get(legacy.get());
         stack.remove(legacy.get());
         stack.set(dataComponentType.get(), data);
      }
   }
}
