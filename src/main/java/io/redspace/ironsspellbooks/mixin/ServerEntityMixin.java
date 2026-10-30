package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.network.ComplexSpawnDataPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ServerEntity.class)
public abstract class ServerEntityMixin {
    @Shadow
    @Final
    private Entity entity;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "sendPairingData", at = @At("TAIL"))
    private void irons_spellbooks$sendComplexSpawnData(ServerPlayer player, Consumer<Packet<ClientGamePacketListener>> consumer, CallbackInfo ci) {
        if (this.entity instanceof IEntityWithComplexSpawn) {
            var payload = ComplexSpawnDataPacket.create(this.entity, player.registryAccess());
            consumer.accept((Packet) new net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket(payload));
        }
    }
}
