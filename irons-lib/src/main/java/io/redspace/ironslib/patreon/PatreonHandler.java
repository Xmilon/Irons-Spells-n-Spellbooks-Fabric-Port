package io.redspace.ironslib.patreon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.SessionService;
import io.redspace.ironslib.IronsLib;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import io.redspace.ironslib.internal.network.packet.SyncPatreonDataPacket;
import io.redspace.ironslib.patreon.data.ChronicleEntry;
import io.redspace.ironslib.patreon.data.PatreonPermissions;
import io.redspace.ironslib.patreon.data.ProfileResult;
import io.redspace.ironslib.util.Utils;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.server.MinecraftServer;

public class PatreonHandler {
   private static final long DEFAULT_RETRY_SECONDS = 300L;
   private static final long MAX_CACHE_AGE_MS = 300000L;
   private static final Duration HTTP_TIMEOUT = Duration.ofSeconds(25L);
   private static PatreonHandler instance;
   private final MinecraftServer server;
   private final HttpClient httpClient;
   private final ScheduledExecutorService scheduler;
   private final SessionService sessionService;
   private volatile ScheduledFuture<?> scheduledTask;
   private long httpLastUpdated;
   private boolean logNextFetchError;
   private boolean logNextFetchSuccess;

   private PatreonHandler(MinecraftServer server) {
      this.server = server;
      this.sessionService = server.services().sessionService();
      this.httpClient = HttpClient.newHttpClient();
      this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
         Thread t = new Thread(r, "iron431-patreon-service");
         t.setDaemon(true);
         return t;
      });
      this.httpLastUpdated = 0L;
      this.logNextFetchError = true;
      this.logNextFetchSuccess = true;
   }

   public static void initialize(MinecraftServer server) {
      if (instance == null) {
         instance = new PatreonHandler(server);
         instance.firstTimeLoadData();
      }
   }

   public static void shutdown() {
      if (instance != null) {
         instance.interrupt();
         instance = null;
      }
   }

   private void interrupt() {
      if (this.scheduledTask != null) {
         this.scheduledTask.cancel(false);
      }

      this.scheduler.shutdownNow();
   }

   private void firstTimeLoadData() {
      IronsLibSaveData data = IronsLibSaveData.get(this.server);
      if (data != null && data.getPatreonCacheTimestamp() > 0L && System.currentTimeMillis() - data.getPatreonCacheTimestamp() < 300000L) {
         List<ChronicleEntry> chronicle = data.getCachedPatreonChronicle();
         List<ProfileResult> profiles = data.getCachedPatreonProfiles();
         if (!chronicle.isEmpty() || !profiles.isEmpty()) {
            IronsLib.LOGGER.info("Successfully loaded Cache");
            this.applyCacheResult(new PatreonHandler.CacheResult(true, chronicle, profiles, data.getPatreonCacheTimestamp()));
            this.scheduleNext(300L);
            return;
         }
      }

      this.scheduleNext(0L);
   }

   private void scheduleNext(long delaySeconds) {
      this.scheduledTask = this.scheduler.schedule(this::doDataFetch, delaySeconds, TimeUnit.SECONDS);
   }

   private void doDataFetch() {
      try {
         boolean isDedicated = this.server.isDedicatedServer();
         int playerCount = this.server.getPlayerCount();
         if (isDedicated && playerCount == 0 && this.httpLastUpdated != 0L) {
            this.scheduleNext(300L);
         } else {
            String serverId = "0";
            UUID uuid = IronsLibSaveData.getOrCreateServerId(this.server);
            if (uuid != null) {
               serverId = uuid.toString();
            }

            String url = String.format(
               "https://code.redspace.io/cache/game-data?lastUpdated=%s&id=%s&playerCount=%s&isDedicated=%s&version=%s",
               this.httpLastUpdated,
               serverId,
               playerCount,
               isDedicated,
               IronsLib.version
            );
            HttpRequest request = HttpRequest.newBuilder(new URI(url)).timeout(HTTP_TIMEOUT).GET().build();
            this.httpClient.sendAsync(request, BodyHandlers.ofString()).thenAccept(this::handleHttpDataResponse).exceptionally(ex -> {
               if (this.logNextFetchError) {
                  IronsLib.LOGGER.error("Failed to handle Data response: {}", ex.getMessage());
                  this.logNextFetchError = false;
                  this.logNextFetchSuccess = true;
               }

               this.scheduleNext(300L);
               this.attemptLoadCache();
               return null;
            });
         }
      } catch (URISyntaxException e) {
         throw new RuntimeException("URI Exception: " + e.getMessage());
      }
   }

   private void handleHttpDataResponse(HttpResponse<String> httpResponse) {
      this.scheduleNext(300L);
      int code = httpResponse.statusCode();
      if (code / 100 == 2) {
         String httpBody = httpResponse.body();

         JsonObject response;
         try {
            response = JsonParser.parseString(httpBody).getAsJsonObject();
         } catch (Exception e) {
            throw new RuntimeException(String.format("Body could not be parsed into json (%s)", e.getMessage()));
         }

         if (!response.has("data")) {
            throw new RuntimeException("Data not present in payload");
         }

         if (!response.has("lastUpdated")) {
            throw new RuntimeException("Data timestamp not present in payload");
         }

         JsonArray data = response.getAsJsonArray("data");
         this.populateDataFromJson(data);
         this.httpLastUpdated = response.get("lastUpdated").getAsLong();
         this.saveToCache();
         this.broadcastSync();
         this.logNextFetchError = true;
         if (this.logNextFetchSuccess) {
            IronsLib.LOGGER.info("Loaded {}", PatreonData.getInstance().getProfileValues().size());
            this.logNextFetchSuccess = false;
         }
      }
   }

   private void applyCacheResult(PatreonHandler.CacheResult result) {
      List<ProfileResult> resolved = new ArrayList<>(result.profiles().size());

      for (ProfileResult profile : result.profiles()) {
         String username = profile.username();
         if (username == null || username.isEmpty()) {
            username = this.fetchUsernameFromUuid(profile.uuid());
         }

         resolved.add(new ProfileResult(username, profile.uuid(), profile.permissions()));
      }

      PatreonData.getInstance().replaceWithChronicleAndProfiles(result.chronicleEntries(), resolved);
      this.broadcastSync();
   }

   private String fetchUsernameFromUuid(UUID uuid) {
      try {
         com.mojang.authlib.services.ProfileResult profileResult = this.sessionService.fetchProfile(uuid, true);
         if (profileResult == null) {
            return "";
         }

         GameProfile profile = profileResult.profile();
         return profile != null && profile.name() != null ? profile.name() : "";
      } catch (Exception e) {
         return "";
      }
   }

   private void attemptLoadCache() {
      IronsLibSaveData data = IronsLibSaveData.get(this.server);
      if (data != null && data.getPatreonCacheTimestamp() > 0L) {
         List<ChronicleEntry> chronicle = data.getCachedPatreonChronicle();
         List<ProfileResult> profiles = data.getCachedPatreonProfiles();
         if (!chronicle.isEmpty() || !profiles.isEmpty()) {
            this.applyCacheResult(new PatreonHandler.CacheResult(true, chronicle, profiles, data.getPatreonCacheTimestamp()));
         }
      }
   }

   private void saveToCache() {
      PatreonData data = PatreonData.getInstance();
      if (!data.isEmpty()) {
         IronsLibSaveData serverData = IronsLibSaveData.get(this.server);
         if (serverData != null) {
            long now = System.currentTimeMillis();
            serverData.setPatreonCache(now, data.getChronicleEntries(), new ArrayList<>(data.getProfileValues()));
         }
      }
   }

   private void broadcastSync() {
      this.server.execute(() -> {
         PatreonData data = PatreonData.getInstance();
         if (!data.isEmpty()) {
            var payload = new SyncPatreonDataPacket(data.getChronicleEntries(), new ArrayList<>(data.getProfileValues()));
            PlayerLookup.all(this.server).forEach(player -> ServerPlayNetworking.send(player, payload));
         }
      });
   }

   private synchronized void populateDataFromJson(JsonArray data) {
      List<ChronicleEntry> chronicle = new ArrayList<>();
      List<ProfileResult> profiles = new ArrayList<>();

      for (JsonElement elem : data) {
         if (elem.isJsonObject()) {
            JsonObject userEntry = elem.getAsJsonObject();

            try {
               int pledgeTier = userEntry.get("pledgeTier").getAsInt();
               int bookCategory = userEntry.get("bookCategory").getAsInt();
               String displayName = userEntry.get("displayName").getAsString();
               if (bookCategory >= 0) {
                  chronicle.add(new ChronicleEntry(displayName, pledgeTier, bookCategory));
               }

               if (userEntry.has("uuid")) {
                  UUID uuid = Utils.uuidFromUndashed(userEntry.get("uuid").getAsString());
                  String username = userEntry.has("mcUsername") ? userEntry.get("mcUsername").getAsString() : this.fetchUsernameFromUuid(uuid);
                  PatreonPermissions perm = PatreonPermissions.values()[pledgeTier];
                  profiles.add(new ProfileResult(username, uuid, perm));
               }
            } catch (Exception var13) {
            }
         }
      }

      PatreonData.getInstance().replaceWithChronicleAndProfiles(chronicle, mergeProfilesByUuid(profiles));
   }

   private static List<ProfileResult> mergeProfilesByUuid(List<ProfileResult> profiles) {
      Map<UUID, ProfileResult> byUuid = new HashMap<>();

      for (ProfileResult p : profiles) {
         ProfileResult existing = byUuid.get(p.uuid());
         if (existing == null) {
            byUuid.put(p.uuid(), p);
         } else {
            int maxOrd = Math.max(p.permissions().ordinal(), existing.permissions().ordinal());
            byUuid.put(p.uuid(), new ProfileResult(p.username(), p.uuid(), PatreonPermissions.values()[maxOrd]));
         }
      }

      return new ArrayList<>(byUuid.values());
   }

   private record CacheResult(boolean success, List<ChronicleEntry> chronicleEntries, List<ProfileResult> profiles, long cachedTimestamp) {
   }
}
