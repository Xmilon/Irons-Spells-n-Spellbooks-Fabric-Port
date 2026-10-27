package io.redspace.ironslib.internal.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Avatar.class)
public interface PlayerAccessor {
   @Accessor("DATA_PLAYER_MODE_CUSTOMISATION")
   static EntityDataAccessor<Byte> getDataPlayerModeCustomisation() {
      throw new AssertionError();
   }
}
