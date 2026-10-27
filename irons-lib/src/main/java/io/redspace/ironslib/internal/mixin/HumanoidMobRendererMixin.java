package io.redspace.ironslib.internal.mixin;

import io.redspace.ironslib.transmog.TransmogItemData;
import io.redspace.ironslib.transmog.client.CapeData;
import io.redspace.ironslib.transmog.client.ICapeDataProvider;
import io.redspace.ironslib.transmog.client.ISupportTransmog;
import io.redspace.ironslib.transmog.client.TransmogClientHandler;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HumanoidMobRenderer.class)
public class HumanoidMobRendererMixin {
   @Inject(method = "getEquipmentIfRenderable", at = @At("TAIL"), cancellable = true)
   private static void swapForTransmog(LivingEntity entity, EquipmentSlot slot, CallbackInfoReturnable<ItemStack> cir) {
      ItemStack stack = (ItemStack)cir.getReturnValue();
      if (!(entity instanceof ISupportTransmog) || entity instanceof Player player && !TransmogClientHandler.canUseTransmog(player, stack)) {
         TransmogItemData.remove(stack);
         cir.setReturnValue(stack);
      }
   }

   @Inject(method = "extractHumanoidRenderState", at = @At("TAIL"))
   private static void extractCapeData(LivingEntity entity, HumanoidRenderState state, float partialTicks, ItemModelResolver itemModelResolver, CallbackInfo ci) {
      ICapeDataProvider renderState = (ICapeDataProvider)entity;
      CapeData capeData = renderState.irons_lib$getCapeData();
      capeData.extractState(entity, partialTicks, state);
   }
}
