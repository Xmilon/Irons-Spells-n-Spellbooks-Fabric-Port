package io.redspace.ironslib.internal.mixin;

import io.redspace.ironslib.transmog.client.CapeData;
import io.redspace.ironslib.transmog.client.ICapeDataProvider;
import io.redspace.ironslib.transmog.client.ITransmogPreview;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
public class ClientLivingEntityMixin implements ITransmogPreview, ICapeDataProvider {
   @Unique
   boolean irons_lib$transmogPreview;
   @Unique
   private final CapeData irons_lib$capeData = new CapeData();

   @Override
   public CapeData irons_lib$getCapeData() {
      return this.irons_lib$capeData;
   }

   @Override
   public void irons_lib$setCapeData(CapeData capeData) {
   }

   @Override
   public boolean irons_lib$isTransmogPreview() {
      return this.irons_lib$transmogPreview;
   }

   @Override
   public void irons_lib$setTransmogPreview(boolean preview) {
      this.irons_lib$transmogPreview = preview;
   }
}
