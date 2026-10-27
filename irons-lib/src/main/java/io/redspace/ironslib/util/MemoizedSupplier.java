package io.redspace.ironslib.util;

import java.util.Objects;
import java.util.function.Supplier;

public class MemoizedSupplier<T> implements Supplier<T> {
   T value;
   final Supplier<T> supplier;

   public MemoizedSupplier(Supplier<T> supplier) {
      this.supplier = supplier;
   }

   @Override
   public T get() {
      if (this.value == null) {
         synchronized (this.supplier) {
            this.value = Objects.requireNonNull(this.supplier.get());
         }
      }

      return this.value;
   }
}
