package io.redspace.ironslib.util;

import net.minecraft.util.Mth;

public record Color(int packedARGB, int red, int green, int blue) {
   public Color(int color) {
      this(color, color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF);
   }

   public Color(int r, int g, int b) {
      this(0xFF000000 | r << 16 | g << 8 | b, r, g, b);
   }

   public int value() {
      return Math.max(this.red, Math.max(this.green, this.blue));
   }

   public int luminance() {
      return (int)(0.2126 * this.red + 0.7152 * this.green + 0.0722 * this.blue);
   }

   public int alpha() {
      return this.packedARGB >> 24;
   }

   public Color packedValue() {
      int v = this.value();
      return new Color(v, v, v);
   }

   public boolean empty() {
      return this.packedARGB == 0 || this.alpha() == 0;
   }

   public Color multiply(Color other) {
      return new Color(this.red * other.red / 255, this.green * other.green / 255, this.blue * other.blue / 255);
   }

   public static Color rgba(int rgba) {
      int alpha = rgba >> 24 & 0xFF;
      int r = rgba & 0xFF;
      int g = rgba >> 8 & 0xFF;
      int b = rgba >> 16 & 0xFF;
      return new Color(alpha << 24 | r << 16 | g << 8 | b);
   }

   public int toRgba() {
      return this.alpha() << 24 | this.blue << 16 | this.green << 8 | this.red;
   }

   public static Color lerp(float f, Color a, Color b) {
      return new Color(
         (int)Math.clamp(Mth.lerp(f, a.red, b.red), 0.0F, 255.0F),
         (int)Math.clamp(Mth.lerp(f, a.green, b.green), 0.0F, 255.0F),
         (int)Math.clamp(Mth.lerp(f, a.blue, b.blue), 0.0F, 255.0F)
      );
   }

   public Color scale(float scalar) {
      return new Color(
         (int)Math.clamp(this.red * scalar, 0.0F, 255.0F),
         (int)Math.clamp(this.green * scalar, 0.0F, 255.0F),
         (int)Math.clamp(this.blue * scalar, 0.0F, 255.0F)
      );
   }
}
