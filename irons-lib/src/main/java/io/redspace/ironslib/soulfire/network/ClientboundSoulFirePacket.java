package io.redspace.ironslib.soulfire.network;

import io.redspace.ironslib.soulfire.client.SoulFireClientHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public record ClientboundSoulFirePacket(int entityId, boolean soul) implements CustomPacketPayload {
   public static final Type<ClientboundSoulFirePacket> TYPE = new Type(Identifier.fromNamespaceAndPath("irons_lib", "soul_fire"));
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSoulFirePacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT, ClientboundSoulFirePacket::entityId, ByteBufCodecs.BOOL, ClientboundSoulFirePacket::soul, ClientboundSoulFirePacket::new
   );

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void handle(ClientboundSoulFirePacket payload, ClientPlayNetworking.Context context) {
      context.client().execute(() -> SoulFireClientHandler.onSoulFire(payload.entityId(), payload.soul()));
   }
}
