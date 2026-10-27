package io.redspace.ironslib.internal.mixin;

import io.redspace.ironslib.transmog.client.ISupportTransmog;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public class ClientPlayerMixin implements ISupportTransmog {
}
