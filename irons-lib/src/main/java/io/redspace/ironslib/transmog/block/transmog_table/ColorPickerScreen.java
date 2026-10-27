package io.redspace.ironslib.transmog.block.transmog_table;

import io.redspace.ironslib.IronsLib;
import io.redspace.ironslib.util.GuiGraphicsHelper;
import java.awt.Color;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class ColorPickerScreen extends Screen {
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("irons_lib", "textures/gui/color_picker.png");
   private static final int PICKER_X = 8;
   private static final int PICKER_Y = 24;
   private static final int PICKER_WIDTH = 100;
   private static final int PICKER_HEIGHT = 64;
   private static final int VALUE_X = 114;
   private static final int VALUE_Y = 24;
   private static final int VALUE_WIDTH = 8;
   private static final int VALUE_HEIGHT = 64;
   float x;
   float y;
   float v;
   int leftPos;
   int topPos;
   public int imageWidth = 130;
   public int imageHeight = 96;
   int currentColor;
   int initialColor;
   final Runnable closeCallback;
   final Consumer<Integer> setColor;
   EditBox hexTextInput;
   boolean isMouseSelectingColor;
   boolean isMouseSelectingValue;

   protected ColorPickerScreen(Runnable closeCallback, Consumer<Integer> setColor, int initialColor) {
      super(Component.empty());
      this.setColor = setColor;
      this.closeCallback = closeCallback;
      this.currentColor = initialColor & 16777215;
      this.initialColor = this.currentColor;
      this.setPickerFromColor(this.initialColor);
      String hex = "#" + Integer.toHexString(this.currentColor);
      this.hexTextInput = new EditBox(Minecraft.getInstance().font, 7, 7, 52, 12, Component.literal(hex));
      this.hexTextInput.setMaxLength(7);
      this.hexTextInput.setValue(hex);
   }

   public void init() {
      this.leftPos = (this.width - this.imageWidth) / 2;
      this.topPos = (this.height - this.imageHeight) / 2;
      this.hexTextInput.setX(this.leftPos + 7);
      this.hexTextInput.setY(this.topPos + 7);
   }

   public void setPos(int x, int y) {
      this.leftPos = x;
      this.topPos = y;
      this.hexTextInput.setX(this.leftPos + 7);
      this.hexTextInput.setY(this.topPos + 7);
   }

   public void onClose() {
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void resetColor() {
      this.setColor(this.initialColor);
      this.setTextHex(this.initialColor);
      this.setPickerFromColor(this.initialColor);
   }

   public void setColor(int color) {
      this.currentColor = color;
      this.setColor.accept(color);
   }

   public void setTextHex(int color) {
      String hex = "#" + Integer.toHexString(color);
      this.hexTextInput.setValue(hex);
   }

   public void onTextInputChanged() {
      if (!this.hexTextInput.getValue().startsWith("#")) {
         this.hexTextInput.setValue("#" + this.hexTextInput.getValue());
      }

      String hexValue = this.hexTextInput.getValue().substring(1);
      boolean isValidHex = hexValue.matches("^[0-9a-fA-F]+$");
      if (isValidHex) {
         this.hexTextInput.setTextColor(-1);
         this.setColor(hexValue.isEmpty() ? 0 : Integer.parseInt(hexValue, 16));
         this.setPickerFromColor(this.currentColor);
      } else {
         this.hexTextInput.setTextColor(net.minecraft.network.chat.TextColor.RED.getValue());
      }
   }

   private void updateValueFromMouse(double mouseY) {
      this.v = (int)Math.clamp(mouseY - this.topPos - 24.0, 0.0, 64.0);
      this.setColor(this.colorFromPicker(this.x, this.y, this.v));
      this.setTextHex(this.currentColor);
   }

   private void updateColorFromMouse(double mouseX, double mouseY) {
      this.x = (int)Math.clamp(mouseX - this.leftPos - 8.0, 0.0, 99.0);
      this.y = (int)Math.clamp(mouseY - this.topPos - 24.0, 0.0, 63.0);
      this.setColor(this.colorFromPicker(this.x, this.y, this.v));
      this.setTextHex(this.currentColor);
   }

   public int colorFromPicker(float x, float y, float v) {
      float hue = x / 100.0F;
      float saturation = 1.0F - y / 64.0F;
      float brightness = 1.0F - v / 64.0F;
      return Color.getHSBColor(hue, saturation, brightness).getRGB() & 16777215;
   }

   public void setPickerFromColor(int color) {
      io.redspace.ironslib.util.Color colorUnpacked = new io.redspace.ironslib.util.Color(color);
      float[] hsb = Color.RGBtoHSB(colorUnpacked.red(), colorUnpacked.green(), colorUnpacked.blue(), null);
      float hue = hsb[0];
      float saturation = hsb[1];
      float brightness = hsb[2];
      this.x = hue * 100.0F;
      this.y = (1.0F - saturation) * 64.0F;
      this.v = (1.0F - brightness) * 64.0F;
   }

   public void extractPopupBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
      this.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
   }

   public void extractPopupForeground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
      guiGraphics.nextStratum();
      this.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
   }

   public void extractRenderState(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
      super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
      this.hexTextInput.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
      GuiGraphicsHelper.blitSprite(
         guiGraphics, IronsLib.id("color_picker/color_picker_target"), (int)(this.leftPos + 8 + this.x) - 5, (int)(this.topPos + 24 + this.y) - 5, 10, 10
      );
      GuiGraphicsHelper.blitSprite(guiGraphics, IronsLib.id("color_picker/value_picker"), this.leftPos + 114 - 2, (int)(this.topPos - 4 + 24 + this.v), 8, 8);
   }

   public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
      GuiGraphicsHelper.blit(guiGraphics, TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
      guiGraphics.nextStratum();
      this.drawPreviewBox(guiGraphics);
   }

   private void drawPreviewBox(GuiGraphicsExtractor guiGraphics) {
      int width = 20;
      int height = 10;
      int x = this.leftPos + 88;
      int y = this.topPos + 8;
      io.redspace.ironslib.util.Color color = new io.redspace.ironslib.util.Color(this.currentColor);
      GuiGraphicsHelper.blitSpriteColored(
         guiGraphics, IronsLib.id("color_picker/white"), x, y, width, height, color.red() / 255.0F, color.green() / 255.0F, color.blue() / 255.0F, 1.0F
      );
   }

   public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
      double mouseX = event.x();
      double mouseY = event.y();
      this.isMouseSelectingColor = false;
      this.isMouseSelectingValue = false;
      if (this.hexTextInput.mouseClicked(event, doubleClick)) {
         this.hexTextInput.setFocused(true);
         return true;
      } else {
         this.hexTextInput.setFocused(false);
         if (mouseX > this.leftPos + 64 && mouseY > this.topPos + 8 && mouseX < this.leftPos + 64 + 10 && mouseY < this.topPos + 8 + 10) {
            this.resetColor();
            return true;
         } else if (mouseX >= this.leftPos + 8 && mouseX <= this.leftPos + 8 + 100 && mouseY >= this.topPos + 24 && mouseY <= this.topPos + 24 + 64) {
            this.isMouseSelectingColor = true;
            this.updateColorFromMouse(mouseX, mouseY);
            return true;
         } else if (mouseX >= this.leftPos + 114 && mouseX <= this.leftPos + 114 + 8 && mouseY >= this.topPos + 24 && mouseY <= this.topPos + 24 + 64) {
            this.isMouseSelectingValue = true;
            this.updateValueFromMouse(mouseY);
            return true;
         } else {
            return super.mouseClicked(event, doubleClick);
         }
      }
   }

   public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
      double mouseX = event.x();
      double mouseY = event.y();
      if (this.isMouseSelectingColor) {
         this.updateColorFromMouse(mouseX, mouseY);
         return true;
      } else if (this.isMouseSelectingValue) {
         this.updateValueFromMouse(mouseY);
         return true;
      } else {
         return super.mouseDragged(event, dragX, dragY);
      }
   }

   public boolean keyPressed(KeyEvent event) {
      if (this.hexTextInput.keyPressed(event)) {
         this.onTextInputChanged();
         return true;
      } else {
         return this.hexTextInput.isFocused() && this.hexTextInput.isVisible() && event.key() != 256 ? true : super.keyPressed(event);
      }
   }

   public boolean charTyped(CharacterEvent event) {
      if (this.hexTextInput.isFocused() && this.hexTextInput.charTyped(event)) {
         this.onTextInputChanged();
         return true;
      } else {
         return super.charTyped(event);
      }
   }
}
