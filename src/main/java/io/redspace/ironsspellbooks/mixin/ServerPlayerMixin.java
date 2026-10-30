package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
    private void irons_spellbooks$saveMagic(ValueOutput output, CallbackInfo ci) {
        var player = (ServerPlayer) (Object) this;
        var data = new CompoundTag();
        MagicData.getPlayerMagicData(player).saveNBTData(data, player.registryAccess());
        output.store(IRONS_MAGIC, CompoundTag.CODEC, data);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void irons_spellbooks$loadMagic(ValueInput input, CallbackInfo ci) {
        var player = (ServerPlayer) (Object) this;
        input.read(IRONS_MAGIC, CompoundTag.CODEC).ifPresent(data -> MagicData.getPlayerMagicData(player).loadNBTData(data, player.registryAccess()));
    }
}
