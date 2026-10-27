package io.redspace.ironslib.internal.mixin;

import io.redspace.ironslib.transmog.client.ISupportTransmog;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ArmorStand.class)
public class ClientArmorStandMixin implements ISupportTransmog {
}
