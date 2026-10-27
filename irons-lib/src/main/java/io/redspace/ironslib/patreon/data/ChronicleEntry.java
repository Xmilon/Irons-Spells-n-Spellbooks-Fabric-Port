package io.redspace.ironslib.patreon.data;

public record ChronicleEntry(String displayName, short data) {
   public ChronicleEntry(String displayName, int pledge, int bookCategory) {
      this(displayName, (short)((bookCategory & 0xFF) << 8 | pledge & 0xFF));
   }

   public int pledge() {
      return this.data & 0xFF;
   }

   public int bookCategory() {
      return this.data >> 8 & 0xFF;
   }
}
