package io.redspace.ironslib.statue.client;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.services.ProfileResult;
import com.mojang.blaze3d.platform.NativeImage;
import io.redspace.ironslib.IronsLib;
import io.redspace.ironslib.patreon.PatreonData;
import io.redspace.ironslib.patreon.data.PatreonPermissions;
import io.redspace.ironslib.statue.PlayerStatueModelType;
import io.redspace.ironslib.statue.StatueData;
import io.redspace.ironslib.util.Color;
import io.redspace.ironslib.util.MeanShiftCluster;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.ClientAsset.DownloadedTexture;
import net.minecraft.core.ClientAsset.ResourceTexture;
import net.minecraft.core.ClientAsset.Texture;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StatueTextureManager {
   public static final UUID TEST_UUID = uuidFromUndashed("c3adad79e88a4f15bd61c58766d725e9");
   public static final UUID TEST_UUID2 = uuidFromUndashed("afb939b1f2684ebcb1f1261fad41bc33");
   public static final UUID TEST_UUID3 = uuidFromUndashed("93b459bece4f4700b457c1aa91b3b687");
   private static final ConcurrentHashMap<UUID, StatueTextureHolder> TEXTURES = new ConcurrentHashMap<>();
   private static final Identifier NULL_TEXTURE = IronsLib.id("steve");
   public static final StatueTextureHolder NULL = new StatueTextureHolder(PatreonPermissions.None, NULL_TEXTURE, PlayerStatueModelType.WIDE);

   public static void refresh() {
      synchronized (TEXTURES) {
         TEXTURES.keys().asIterator().forEachRemaining(StatueTextureManager::createTextureAsync);
      }
   }

   public static UUID uuidFromUndashed(String s) {
      if (s != null && s.length() == 32) {
         String dashed = s.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{12})", "$1-$2-$3-$4-$5");
         return UUID.fromString(dashed);
      } else {
         throw new IllegalArgumentException("Invalid UUID string");
      }
   }

   @Nullable
   public static File export(String name, NativeImage image) {
      try {
         if (!name.endsWith(".png")) {
            name = name + ".png";
         }

         Path path = Path.of("screenshots/irons_lib").resolve(name);
         if (Files.notExists(path)) {
            Files.createDirectories(path.getParent());
         }

         File file = path.toFile();
         image.writeToFile(file);
         return file;
      } catch (Exception e) {
         IronsLib.LOGGER.debug(e.getMessage());
         return null;
      }
   }

   public static Identifier IdentifierFromUuid(UUID uuid) {
      return IronsLib.id(uuid.toString());
   }

   public static StatueTextureHolder lookupUUID(@NotNull UUID playerUuid) {
      if (TEXTURES.containsKey(playerUuid)) {
         return TEXTURES.get(playerUuid);
      }

      TEXTURES.put(playerUuid, NULL);
      createTextureAsync(playerUuid);
      return NULL;
   }

   public static StatueTextureHolder resolve(@Nullable StatueData statueData) {
      if (statueData == null) {
         return NULL;
      } else {
         return !PatreonData.getPatreonPermissions(statueData.uuid()).supportsStatues() ? NULL : lookupUUID(statueData.uuid());
      }
   }

   private static void createTextureAsync(UUID playerUuid) {
      CompletableFuture.runAsync(() -> createTexture(playerUuid), Util.backgroundExecutor());
   }

   private static void createTexture(UUID playerUuid) {
      PatreonPermissions permissions = PatreonData.getPatreonPermissions(playerUuid);
      if (permissions.supportsStatues()) {
         Minecraft minecraft = Minecraft.getInstance();
         ProfileResult profileResult = minecraft.services().sessionService().fetchProfile(playerUuid, false);
         if (profileResult != null) {
            GameProfile profile = profileResult.profile();
            IronsLib.LOGGER.debug("processing texture for {} ({})", playerUuid, profile.name());
            minecraft.getSkinManager()
               .get(profile)
               .thenAcceptAsync(
                  optionalSkin -> optionalSkin.ifPresent(skin -> minecraft.execute(() -> createTextureFromProfileResponse(playerUuid, permissions, skin))),
                  Util.backgroundExecutor()
               );
         }
      }
   }

   private static void createTextureFromProfileResponse(UUID playerUuid, PatreonPermissions permissions, PlayerSkin skin) {
      NativeImage skinTexture = copySkinImage(skin);
      if (skinTexture != null) {
         skinTexture = transformTexture(skinTexture);
         Identifier textureId = IdentifierFromUuid(playerUuid);
         Minecraft.getInstance().getTextureManager().register(textureId, new DynamicTexture(() -> "irons_lib/statue/" + playerUuid, skinTexture));
         TEXTURES.put(playerUuid, new StatueTextureHolder(permissions, textureId, resolveModelType(skinTexture, skin.model())));
      }
   }

   private static PlayerStatueModelType resolveModelType(NativeImage skinTexture, PlayerModelType modelType) {
      if (skinTexture.getHeight() != skinTexture.getWidth()) {
         return PlayerStatueModelType.LEGACY;
      } else {
         return modelType == PlayerModelType.SLIM ? PlayerStatueModelType.SLIM : PlayerStatueModelType.WIDE;
      }
   }

   @Nullable
   private static NativeImage copySkinImage(PlayerSkin skin) {
      Texture body = skin.body();
      Minecraft minecraft = Minecraft.getInstance();

      try {
         if (body instanceof DownloadedTexture downloaded) {
            if (minecraft.getTextureManager().getTexture(downloaded.texturePath()) instanceof DynamicTexture dynamicTexture) {
               return dynamicTexture.getPixels().mappedCopy(i -> i);
            }
         } else if (body instanceof ResourceTexture resource) {
            return NativeImage.read(((Resource)minecraft.getResourceManager().getResource(resource.texturePath()).orElseThrow()).open());
         }
      } catch (IOException e) {
         IronsLib.LOGGER.error("Failed to load skin image for {}", body.id(), e);
      }

      return null;
   }

   public static NativeImage steve() {
      try {
         return NativeImage.read(
            ((Resource)Minecraft.getInstance()
                  .getResourceManager()
                  .getResource(Identifier.withDefaultNamespace("textures/entity/player/wide/steve.png"))
                  .orElseThrow())
               .open()
         );
      } catch (Exception ignored) {
         throw new RuntimeException();
      }
   }

   private static NativeImage normalizeValues(NativeImage texture, float fMin, float fMax) {
      int min = Integer.MAX_VALUE;
      int max = Integer.MIN_VALUE;
      NativeImage textureOut = texture.mappedCopy(i -> i);

      for (int color : textureOut.getPixels()) {
         if (color != 0) {
            Color colorStruct = new Color(color);
            int v = colorStruct.luminance();
            if (v < min) {
               min = v;
            }

            if (v > max) {
               max = v;
            }
         }
      }

      if (max <= min) {
         return textureOut;
      }

      for (int x = 0; x < textureOut.getWidth(); x++) {
         for (int y = 0; y < textureOut.getHeight(); y++) {
            Color color = new Color(textureOut.getPixel(x, y));
            if (color.empty()) {
               textureOut.setPixel(x, y, 0);
            } else {
               Color normalized = new Color(
                  (int)Math.clamp(Mth.lerp((float)(color.red() - min) / (max - min), fMin * 255.0F, fMax * 255.0F), 0.0F, 255.0F),
                  (int)Math.clamp(Mth.lerp((float)(color.green() - min) / (max - min), fMin * 255.0F, fMax * 255.0F), 0.0F, 255.0F),
                  (int)Math.clamp(Mth.lerp((float)(color.blue() - min) / (max - min), fMin * 255.0F, fMax * 255.0F), 0.0F, 255.0F)
               );
               textureOut.setPixel(x, y, normalized.packedARGB());
            }
         }
      }

      return textureOut;
   }

   private static Color stonePalette(Color color, int min, int max) {
      int v = color.luminance();
      float f = (float)(v - min) / (max - min);
      Color a = new Color(6842474).scale(0.95F);
      Color b = new Color(11250586).scale(1.1F);
      return Color.lerp(f, a, b);
   }

   public static NativeImage transformTexture(NativeImage skinTexture) {
      skinTexture = normalizeValues(skinTexture, 0.0F, 1.0F);
      int[] pixelData = skinTexture.getPixels();
      IntArraySet packedColors = new IntArraySet();

      for (int i : pixelData) {
         if (i != 0) {
            packedColors.add(i);
         }
      }

      if (packedColors.isEmpty()) {
         return skinTexture;
      }

      MeanShiftCluster.MeanShiftClusterResult meanShiftCluster = MeanShiftCluster.meanShiftCluster(new ArrayList<>(packedColors));
      NativeImage stoneOverlay = getStoneOverlay();
      stoneOverlay = normalizeValues(stoneOverlay, 0.75F, 1.0F);

      for (int x = 0; x < skinTexture.getWidth(); x++) {
         for (int y = 0; y < skinTexture.getHeight(); y++) {
            int argb = skinTexture.getPixel(x, y);
            if (argb != 0) {
               Color color = meanShiftCluster.lookupTable().computeIfAbsent(argb, Color::new);
               color = stonePalette(color, 0, 255);
               Color stoneSample = new Color(stoneOverlay.getPixel(x % stoneOverlay.getWidth(), y % stoneOverlay.getHeight()));
               color = color.multiply(stoneSample);
               skinTexture.setPixel(x, y, color.packedARGB());
            }
         }
      }

      return skinTexture;
   }

   private static NativeImage getStoneOverlay() {
      return Minecraft.getInstance()
         .getAtlasManager()
         .getAtlasOrThrow(AtlasIds.BLOCKS)
         .getSprite(Identifier.withDefaultNamespace("block/stone"))
         .contents()
         .originalImage;
   }

   static {
      CompletableFuture.supplyAsync(StatueTextureManager::steve, Util.backgroundExecutor())
         .thenApply(StatueTextureManager::transformTexture)
         .thenAccept(image -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft != null) {
               minecraft.execute(() -> minecraft.getTextureManager().register(NULL_TEXTURE, new DynamicTexture(() -> "irons_lib/steve", image)));
            }
         });
   }
}
