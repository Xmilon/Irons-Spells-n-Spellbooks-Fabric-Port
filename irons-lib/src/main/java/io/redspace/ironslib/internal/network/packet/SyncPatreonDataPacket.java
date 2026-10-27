package io.redspace.ironslib.internal.network.packet;

import io.redspace.ironslib.internal.client.ClientHelper;
import io.redspace.ironslib.patreon.PatreonData;
import io.redspace.ironslib.patreon.data.ChronicleEntry;
import io.redspace.ironslib.patreon.data.PatreonPermissions;
import io.redspace.ironslib.patreon.data.ProfileResult;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class SyncPatreonDataPacket implements CustomPacketPayload {
   public static final Type<SyncPatreonDataPacket> TYPE = new Type(Identifier.fromNamespaceAndPath("irons_lib", "sync_patreon_data"));
   public static final StreamCodec<RegistryFriendlyByteBuf, SyncPatreonDataPacket> STREAM_CODEC = CustomPacketPayload.codec(
      SyncPatreonDataPacket::write, SyncPatreonDataPacket::new
   );
   private final List<ChronicleEntry> chronicleEntries;
   private final List<ProfileResult> profiles;

   public SyncPatreonDataPacket(List<ChronicleEntry> chronicleEntries, List<ProfileResult> profiles) {
      this.chronicleEntries = List.copyOf(chronicleEntries);
      this.profiles = List.copyOf(profiles);
   }

   public SyncPatreonDataPacket(FriendlyByteBuf buf) {
      int chronicleCount = buf.readVarInt();
      this.chronicleEntries = new ArrayList<>(chronicleCount);

      for (int i = 0; i < chronicleCount; i++) {
         String displayName = buf.readUtf();
         int pledge = buf.readByte() & 255;
         int bookCategory = buf.readByte() & 255;
         this.chronicleEntries.add(new ChronicleEntry(displayName, pledge, bookCategory));
      }

      int profileCount = buf.readVarInt();
      this.profiles = new ArrayList<>(profileCount);

      for (int i = 0; i < profileCount; i++) {
         String username = buf.readUtf();
         UUID uuid = buf.readUUID();
         int ordinal = buf.readByte() & 255;
         PatreonPermissions perm = ordinal >= 0 && ordinal < PatreonPermissions.values().length
            ? PatreonPermissions.values()[ordinal]
            : PatreonPermissions.None;
         this.profiles.add(new ProfileResult(username, uuid, perm));
      }
   }

   public void write(FriendlyByteBuf buf) {
      buf.writeVarInt(this.chronicleEntries.size());

      for (ChronicleEntry e : this.chronicleEntries) {
         buf.writeUtf(e.displayName());
         buf.writeByte(e.pledge() & 0xFF);
         buf.writeByte(e.bookCategory() & 0xFF);
      }

      buf.writeVarInt(this.profiles.size());

      for (ProfileResult p : this.profiles) {
         buf.writeUtf(p.username());
         buf.writeUUID(p.uuid());
         buf.writeByte(p.permissions().ordinal() & 0xFF);
      }
   }

   public static void handle(SyncPatreonDataPacket packet, ClientPlayNetworking.Context context) {
      context.client().execute(() -> {
         PatreonData.getInstance().replaceWithChronicleAndProfiles(packet.chronicleEntries, packet.profiles);
         ClientHelper.onPayloadRefresh();
      });
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
