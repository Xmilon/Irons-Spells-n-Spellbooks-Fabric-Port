package io.redspace.ironslib.internal.mixin;

import io.redspace.ironslib.IronsLib;
import io.redspace.ironslib.transmog.TransmogItemData;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphicsExtractor.class)
public class GuiGraphicsMixin {
   @Inject(method = "itemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At("TAIL"))
   private void irons_lib$attachCustomDecorations(Font font, ItemStack stack, int x, int y, String countText, CallbackInfo ci) {
      if (!stack.isEmpty() && TransmogItemData.has(stack)) {
         GuiGraphicsExtractor guiGraphics = (GuiGraphicsExtractor)(Object)this;
         guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, IronsLib.id("transmog_indicator"), x + 16 - 6, y, 6, 6);
      }
   }
}
