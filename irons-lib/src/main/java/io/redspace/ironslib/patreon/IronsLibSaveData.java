package io.redspace.ironslib.patreon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.redspace.ironslib.patreon.data.ChronicleEntry;
import io.redspace.ironslib.patreon.data.PatreonPermissions;
import io.redspace.ironslib.patreon.data.ProfileResult;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.Nullable;

public class IronsLibSaveData extends SavedData {
   private static final Codec<UUID> UUID_CODEC = Codec.STRING.xmap(UUID::fromString, UUID::toString);
   private static final Codec<String> OBFUSCATED_STRING_CODEC = ExtraCodecs.BASE64_STRING.xmap(IronsLibSaveData::unxorName, IronsLibSaveData::xorName);
   private static final Codec<ChronicleEntry> CHRONICLE_CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            OBFUSCATED_STRING_CODEC.fieldOf("profile").forGetter(ChronicleEntry::displayName),
            Codec.INT.fieldOf("pledge").forGetter(ChronicleEntry::pledge),
            Codec.INT.fieldOf("bookCategory").forGetter(ChronicleEntry::bookCategory)
         )
         .apply(instance, ChronicleEntry::new)
   );
   private static final Codec<PatreonPermissions> PERMISSIONS_CODEC = Codec.INT.xmap(ord -> {
      PatreonPermissions[] values = PatreonPermissions.values();
      return ord >= 0 && ord < values.length ? values[ord] : PatreonPermissions.None;
   }, Enum::ordinal);
   private static final Codec<ProfileResult> PROFILE_CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            OBFUSCATED_STRING_CODEC.fieldOf("profile").forGetter(ProfileResult::username),
            UUID_CODEC.fieldOf("uuid").forGetter(ProfileResult::uuid),
            PERMISSIONS_CODEC.fieldOf("data").forGetter(ProfileResult::permissions)
         )
         .apply(instance, ProfileResult::new)
   );
   private static final Codec<IronsLibSaveData> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            UUID_CODEC.fieldOf("ServerId").forGetter(IronsLibSaveData::getServerId),
            Codec.LONG.fieldOf("PatreonCacheTime").forGetter(IronsLibSaveData::getPatreonCacheTimestamp),
            CHRONICLE_CODEC.listOf().fieldOf("PatreonChronicle").forGetter(IronsLibSaveData::getCachedPatreonChronicle),
            PROFILE_CODEC.listOf().fieldOf("PatreonProfiles").forGetter(IronsLibSaveData::getCachedPatreonProfiles)
         )
         .apply(instance, IronsLibSaveData::new)
   );
   public static final SavedDataType<IronsLibSaveData> TYPE = new SavedDataType(
      Identifier.fromNamespaceAndPath("irons_lib", "data"), IronsLibSaveData::new, CODEC, null
   );
   private final UUID serverId;
   private long patreonCacheTimestamp;
   private List<ChronicleEntry> patreonChronicle;
   private List<ProfileResult> patreonProfiles;

   private IronsLibSaveData() {
      this(UUID.randomUUID(), 0L, new ArrayList<>(), new ArrayList<>());
   }

   private IronsLibSaveData(UUID serverId, long patreonCacheTimestamp, List<ChronicleEntry> patreonChronicle, List<ProfileResult> patreonProfiles) {
      this.serverId = serverId;
      this.patreonCacheTimestamp = patreonCacheTimestamp;
      this.patreonChronicle = new ArrayList<>(patreonChronicle);
      this.patreonProfiles = new ArrayList<>();

      for (ProfileResult profile : patreonProfiles) {
         if (profile.uuid() != null) {
            this.patreonProfiles.add(profile);
         }
      }
   }

   public UUID getServerId() {
      return this.serverId;
   }

   public long getPatreonCacheTimestamp() {
      return this.patreonCacheTimestamp;
   }

   public List<ChronicleEntry> getCachedPatreonChronicle() {
      return this.patreonChronicle;
   }

   public List<ProfileResult> getCachedPatreonProfiles() {
      return this.patreonProfiles;
   }

   public void setPatreonCache(long timestamp, List<ChronicleEntry> chronicle, List<ProfileResult> profiles) {
      this.patreonCacheTimestamp = timestamp;
      this.patreonChronicle = chronicle != null ? new ArrayList<>(chronicle) : new ArrayList<>();
      this.patreonProfiles = profiles != null ? new ArrayList<>(profiles) : new ArrayList<>();
      this.setDirty();
   }

   @Nullable
   public static IronsLibSaveData get(MinecraftServer server) {
      return (IronsLibSaveData)server.getDataStorage().computeIfAbsent(TYPE);
   }

   @Nullable
   public static UUID getOrCreateServerId(MinecraftServer server) {
      IronsLibSaveData data = get(server);
      return data != null ? data.getServerId() : null;
   }

   private static byte[] xorName(String s) {
      if (s == null) {
         return new byte[0];
      }

      byte[] bytes = s.getBytes(StandardCharsets.UTF_8);

      for (int i = 0; i < bytes.length; i++) {
         bytes[i] = (byte)(~bytes[i]);
      }

      return bytes;
   }

   private static String unxorName(byte[] bytes) {
      if (bytes != null && bytes.length != 0) {
         byte[] copy = (byte[])bytes.clone();

         for (int i = 0; i < copy.length; i++) {
            copy[i] = (byte)(~copy[i]);
         }

         return new String(copy, StandardCharsets.UTF_8);
      } else {
         return "";
      }
   }
}
