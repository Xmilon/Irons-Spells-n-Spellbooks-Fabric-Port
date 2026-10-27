package io.redspace.ironslib.internal.client;

import io.redspace.ironslib.patreon.PatreonData;
import io.redspace.ironslib.transmog.TransmogHolder;
import io.redspace.ironslib.transmog.TransmogItemData;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;

public class ClientEvents {
   public static void initialize() {
      ItemTooltipCallback.EVENT.register(ClientEvents::handleItemTooltips);
   }

   public static void handleItemTooltips(ItemStack stack, Item.TooltipContext context, TooltipFlag flag, List<Component> tooltip) {
      if (TransmogItemData.has(stack)) {
         TransmogItemData itemData = TransmogItemData.get(stack);
         TransmogHolder transmog = itemData.transmog();
         List<Component> transmogTooltip = new ArrayList<>();
         transmogTooltip.add(
            Component.translatable(
                  "tooltip.irons_lib.transmog_title", new Object[]{Component.translatable(transmog.descriptionId()).withStyle(ChatFormatting.WHITE)}
               )
               .withStyle(ChatFormatting.LIGHT_PURPLE)
         );
         if (transmog.dyeConfig().dyeable() && (itemData.dyeColor() & 16777215) != transmog.dyeConfig().defaultColor()) {
            transmogTooltip.add(
               Component.literal(String.format("  #%s", Integer.toHexString(itemData.dyeColor() & 16777215)))
                  .withStyle(Style.EMPTY.withColor(itemData.dyeColor()))
            );
         }

         var player = Minecraft.getInstance().player;
         if (player != null && !PatreonData.getPatreonPermissions(player).canUse(transmog)) {
            transmogTooltip.add(Component.translatable("tooltip.irons_lib.transmog_failure").withStyle(ChatFormatting.RED));
         }

         tooltip.addAll(Math.min(1, tooltip.size()), transmogTooltip);
      }
   }
}
