package io.redspace.ironslib.internal.client;

import io.redspace.ironslib.attribute.network.SyncAttributeRemapsPacket;
import io.redspace.ironslib.internal.network.packet.CritParticlesAreaPacket;
import io.redspace.ironslib.internal.network.packet.OpenStatuePoseScreenPacket;
import io.redspace.ironslib.internal.network.packet.SyncPatreonDataPacket;
import io.redspace.ironslib.soulfire.network.ClientboundSoulFirePacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

final class ClientNetworking {
    static void initialize() {
        ClientPlayNetworking.registerGlobalReceiver(OpenStatuePoseScreenPacket.TYPE, OpenStatuePoseScreenPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(CritParticlesAreaPacket.TYPE, CritParticlesAreaPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(SyncPatreonDataPacket.TYPE, SyncPatreonDataPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(SyncAttributeRemapsPacket.TYPE, SyncAttributeRemapsPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundSoulFirePacket.TYPE, ClientboundSoulFirePacket::handle);
    }
}
