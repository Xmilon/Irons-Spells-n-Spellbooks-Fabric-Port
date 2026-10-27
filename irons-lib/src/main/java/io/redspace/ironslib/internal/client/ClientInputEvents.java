package io.redspace.ironslib.internal.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.redspace.ironslib.statue.client.StatueTextureManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;

public final class ClientInputEvents {
   private static boolean showExpandedTooltip;

   public static void initialize() {
      KeyMapping refreshStatues = FabricLoader.getInstance().isDevelopmentEnvironment()
         ? KeyMappingHelper.registerKeyMapping(new KeyMapping("key.irons_lib.refresh_statue_textures", InputConstants.KEY_NUMPAD9, KeyMapping.Category.DEBUG))
         : null;
      ClientTickEvents.END_CLIENT_TICK.register(client -> {
         showExpandedTooltip = client.options.keyShift.isDown();
         if (refreshStatues != null && refreshStatues.consumeClick()) {
            StatueTextureManager.refresh();
         }
      });
   }

   public static boolean isShowExpandedTooltip() {
      return showExpandedTooltip;
   }

   public static void setShowExpandedTooltip(boolean showExpandedTooltip) {
      ClientInputEvents.showExpandedTooltip = showExpandedTooltip;
   }
}
