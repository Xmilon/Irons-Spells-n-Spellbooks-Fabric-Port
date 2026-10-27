package io.redspace.ironslib.patreon.data;

import io.redspace.ironslib.transmog.TransmogHolder;
import net.fabricmc.loader.api.FabricLoader;

public enum PatreonPermissions {
   None("tooltip.irons_lib.patreon.tier.none"),
   Acolyte("tooltip.irons_lib.patreon.tier.acolyte"),
   Wizard("tooltip.irons_lib.patreon.tier.wizard"),
   AncientMagician("tooltip.irons_lib.patreon.tier.ancient_magician");

   final String descriptionId;

   PatreonPermissions(String descriptionId) {
      this.descriptionId = descriptionId;
   }

   public String getDescriptionId() {
      return this.descriptionId;
   }

   public boolean canUse(TransmogHolder holder) {
      return FabricLoader.getInstance().isDevelopmentEnvironment() || this.compareTo(holder.requiredPermission()) >= 0;
   }

   public boolean supportsStatues() {
      return this.compareTo(Wizard) >= 0;
   }
}
