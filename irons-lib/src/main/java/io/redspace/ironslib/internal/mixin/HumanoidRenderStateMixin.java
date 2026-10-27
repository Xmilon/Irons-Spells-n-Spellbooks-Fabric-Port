package io.redspace.ironslib.internal.mixin;

import io.redspace.ironslib.transmog.client.CapeData;
import io.redspace.ironslib.transmog.client.ICapeDataProvider;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(HumanoidRenderState.class)
public class HumanoidRenderStateMixin implements ICapeDataProvider {
   @Unique
   private CapeData irons_lib$capeData = new CapeData();

   @Override
   public CapeData irons_lib$getCapeData() {
      return this.irons_lib$capeData;
   }

   @Override
   public void irons_lib$setCapeData(CapeData capeData) {
      this.irons_lib$capeData = capeData.snapshot();
   }
}
