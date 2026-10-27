package io.redspace.ironslib.patreon;

import io.redspace.ironslib.patreon.data.ChronicleEntry;
import io.redspace.ironslib.patreon.data.PatreonPermissions;
import io.redspace.ironslib.patreon.data.ProfileResult;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PatreonData {
   private static PatreonData instance;
   private final ConcurrentHashMap<String, ProfileResult> usernameToProfile = new ConcurrentHashMap<>();
   private final ConcurrentHashMap<UUID, ProfileResult> uuidToProfile = new ConcurrentHashMap<>();
   private final List<ChronicleEntry> chronicleEntries = new ArrayList<>();

   private PatreonData() {
   }

   public static PatreonData getInstance() {
      if (instance == null) {
         instance = new PatreonData();
      }

      return instance;
   }

   public static PatreonPermissions getPatreonPermissions(@NotNull Player player) {
      return getPatreonPermissions(player.getUUID());
   }

   public static PatreonPermissions getPatreonPermissions(UUID playerUUID) {
      return getInstance().uuidToProfile.getOrDefault(playerUUID, ProfileResult.NULL).permissions();
   }

   public static PatreonPermissions getPatreonPermissionsByUsername(String username) {
      return getInstance().usernameToProfile.getOrDefault(username.toLowerCase(Locale.ROOT), ProfileResult.NULL).permissions();
   }

   @Nullable
   public static UUID profileFromUsername(String username) {
      return getInstance().usernameToProfile.getOrDefault(username.toLowerCase(Locale.ROOT), ProfileResult.NULL).uuid();
   }

   public static ProfileResult profileFromUUID(UUID uuid) {
      return getInstance().uuidToProfile.getOrDefault(uuid, ProfileResult.NULL);
   }

   public void replaceWithChronicleAndProfiles(List<ChronicleEntry> chronicle, List<ProfileResult> profiles) {
      synchronized (this.chronicleEntries) {
         this.chronicleEntries.clear();
         this.chronicleEntries.addAll(chronicle);
         this.uuidToProfile.clear();
         this.usernameToProfile.clear();

         for (ProfileResult profile : profiles) {
            this.uuidToProfile.put(profile.uuid(), profile);
            String username = profile.username();
            if (username != null && !username.isEmpty()) {
               this.usernameToProfile.put(username.toLowerCase(Locale.ROOT), profile);
            }
         }
      }
   }

   public List<ChronicleEntry> getChronicleEntries() {
      synchronized (this.chronicleEntries) {
         return List.copyOf(this.chronicleEntries);
      }
   }

   public Collection<ProfileResult> getProfileValues() {
      return this.uuidToProfile.values();
   }

   public boolean isEmpty() {
      return this.uuidToProfile.isEmpty();
   }
}
