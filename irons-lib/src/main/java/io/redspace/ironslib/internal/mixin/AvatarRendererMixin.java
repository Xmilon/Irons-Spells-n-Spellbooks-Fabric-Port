package io.redspace.ironslib.internal.mixin;

import io.redspace.ironslib.transmog.client.TransmogClientHandler;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin<AvatarlikeEntity extends Avatar & ClientAvatarEntity> {
   @Inject(
      method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
      at = @At("TAIL")
   )
   private void adjustPlayerRenderState(AvatarlikeEntity entity, AvatarRenderState state, float partialTicks, CallbackInfo ci) {
      ItemStack headStack = state.headEquipment;
      ItemStack chestStack = state.chestEquipment;
      ItemStack leggingsStack = state.legsEquipment;
      ItemStack bootsStack = state.feetEquipment;
      if (state.showHat && TransmogClientHandler.shouldDisableOuterLayer(headStack, EquipmentSlot.HEAD)) {
         state.showHat = false;
      }

      if ((state.showJacket || state.showLeftSleeve || state.showRightSleeve) && TransmogClientHandler.shouldDisableOuterLayer(chestStack, EquipmentSlot.CHEST)
         )
       {
         state.showJacket = false;
         state.showLeftSleeve = false;
         state.showRightSleeve = false;
      }

      if ((state.showLeftPants || state.showRightPants)
         && (
            TransmogClientHandler.shouldDisableOuterLayer(leggingsStack, EquipmentSlot.LEGS)
               || TransmogClientHandler.shouldDisableOuterLayer(bootsStack, EquipmentSlot.FEET)
         )) {
         state.showLeftPants = false;
         state.showRightPants = false;
      }
   }
}
