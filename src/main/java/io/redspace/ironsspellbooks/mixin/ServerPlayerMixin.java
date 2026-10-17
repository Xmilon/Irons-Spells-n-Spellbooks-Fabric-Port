package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Fabric port has no attachment for MagicData; persist mana/cooldowns/recasts in the player save tag
@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    private static final String IRONS_MAGIC = "irons_spellbooks_magic";

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void irons_spellbooks$saveMagic(CompoundTag tag, CallbackInfo ci) {
        var player = (ServerPlayer) (Object) this;
        var data = new CompoundTag();
        MagicData.getPlayerMagicData(player).saveNBTData(data, player.registryAccess());
        tag.put(IRONS_MAGIC, data);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void irons_spellbooks$loadMagic(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains(IRONS_MAGIC)) {
            var player = (ServerPlayer) (Object) this;
            MagicData.getPlayerMagicData(player).loadNBTData(tag.getCompound(IRONS_MAGIC), player.registryAccess());
        }
    }
}
