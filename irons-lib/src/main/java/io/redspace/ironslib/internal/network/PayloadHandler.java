package io.redspace.ironslib.internal.network;

import io.redspace.ironslib.attribute.network.SyncAttributeRemapsPacket;
import io.redspace.ironslib.internal.network.packet.CritParticlesAreaPacket;
import io.redspace.ironslib.internal.network.packet.OpenStatuePoseScreenPacket;
import io.redspace.ironslib.internal.network.packet.SelectStatuePosePacket;
import io.redspace.ironslib.internal.network.packet.SyncPatreonDataPacket;
import io.redspace.ironslib.soulfire.network.ClientboundSoulFirePacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class PayloadHandler {
   public static void register() {
      PayloadTypeRegistry.serverboundPlay().register(SelectStatuePosePacket.TYPE, SelectStatuePosePacket.STREAM_CODEC);
      PayloadTypeRegistry.clientboundPlay().register(OpenStatuePoseScreenPacket.TYPE, OpenStatuePoseScreenPacket.STREAM_CODEC);
      PayloadTypeRegistry.clientboundPlay().register(CritParticlesAreaPacket.TYPE, CritParticlesAreaPacket.STREAM_CODEC);
      PayloadTypeRegistry.clientboundPlay().register(SyncPatreonDataPacket.TYPE, SyncPatreonDataPacket.STREAM_CODEC);
      PayloadTypeRegistry.clientboundPlay().register(SyncAttributeRemapsPacket.TYPE, SyncAttributeRemapsPacket.STREAM_CODEC);
      PayloadTypeRegistry.clientboundPlay().register(ClientboundSoulFirePacket.TYPE, ClientboundSoulFirePacket.STREAM_CODEC);
      ServerPlayNetworking.registerGlobalReceiver(SelectStatuePosePacket.TYPE, SelectStatuePosePacket::handle);
   }
}
