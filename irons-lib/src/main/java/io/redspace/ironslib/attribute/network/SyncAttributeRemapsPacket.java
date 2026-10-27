package io.redspace.ironslib.attribute.network;

import io.redspace.ironslib.attribute.AttributeRemapRegistry;
import io.redspace.ironslib.attribute.data_handler.AttributeRemapEntry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class SyncAttributeRemapsPacket implements CustomPacketPayload {
   public static final Type<SyncAttributeRemapsPacket> TYPE = new Type(Identifier.fromNamespaceAndPath("irons_lib", "sync_attribute_remaps"));
   public static final StreamCodec<RegistryFriendlyByteBuf, SyncAttributeRemapsPacket> STREAM_CODEC = CustomPacketPayload.codec(
      SyncAttributeRemapsPacket::write, SyncAttributeRemapsPacket::new
   );
   public final List<AttributeRemapEntry> remaps;

   public SyncAttributeRemapsPacket(List<AttributeRemapEntry> remaps) {
      this.remaps = List.copyOf(remaps);
   }

   public SyncAttributeRemapsPacket(FriendlyByteBuf buf) {
      int count = buf.readVarInt();
      this.remaps = new ArrayList<>(count);

      for (int i = 0; i < count; i++) {
         this.remaps.add(new AttributeRemapEntry(buf.readIdentifier(), buf.readIdentifier()));
      }
   }

   public void write(FriendlyByteBuf buf) {
      buf.writeVarInt(this.remaps.size());

      for (AttributeRemapEntry entry : this.remaps) {
         buf.writeIdentifier(entry.from());
         buf.writeIdentifier(entry.to());
      }
   }

   public static void handle(SyncAttributeRemapsPacket packet, ClientPlayNetworking.Context context) {
      context.client().execute(() -> AttributeRemapRegistry.handleDatapackSync(packet.remaps));
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
