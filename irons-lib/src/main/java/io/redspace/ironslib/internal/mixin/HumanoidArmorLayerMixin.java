package io.redspace.ironslib.internal.mixin;

import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoArmorRenderer;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironslib.transmog.TransmogHolder;
import io.redspace.ironslib.transmog.TransmogItemData;
import io.redspace.ironslib.transmog.client.TransmogArmorRenderer;
import io.redspace.ironslib.transmog.client.TransmogClientHandler;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<S extends HumanoidRenderState, M extends HumanoidModel<S>, A extends HumanoidModel<S>>
   extends RenderLayer<S, M> {
   public HumanoidArmorLayerMixin(RenderLayerParent<S, M> renderer) {
      super(renderer);
   }

   @WrapWithCondition(
      method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;FF)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V"
      )
   )
   public boolean irons_lib$handleTransmog(
      HumanoidArmorLayer<S, M, A> instance,
      PoseStack poseStack,
      SubmitNodeCollector submitNodeCollector,
      ItemStack itemStack,
      EquipmentSlot equipmentSlot,
      int lightCoords,
      S state
   ) {
      TransmogItemData itemData = TransmogItemData.get(itemStack);
      if (itemData == null) {
         return true;
      }

      TransmogHolder transmogHolder = itemData.transmog();
      if (transmogHolder == null) {
         return true;
      }

      TransmogArmorRenderer<?, S> geoRenderer = (TransmogArmorRenderer<?, S>)TransmogClientHandler.renderer(transmogHolder);
      Map<DataTicket<?>, Object> geckolibData = state.getDataMap();
      Map<DataTicket<?>, Object> snapshot = new HashMap<>(geckolibData);

      try {
         state.addGeckolibData(GeoArmorRenderer.CURRENT_SLOT, equipmentSlot);
         state.addGeckolibData(GeoArmorRenderer.BASE_MODEL, (HumanoidModel)instance.getParentModel());
         state.addGeckolibData(DataTickets.HAS_GLINT, itemStack.hasFoil());
         geoRenderer.performRenderPass(
            state,
            poseStack,
            submitNodeCollector,
            ((LevelRendererAccessor)Minecraft.getInstance().levelRenderer).irons_lib$getLevelRenderState().cameraRenderState,
            null
         );
      } finally {
         geckolibData.clear();
         geckolibData.putAll(snapshot);
      }

      return false;
   }
}
