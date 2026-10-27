package io.redspace.ironslib;

import com.mojang.logging.LogUtils;
import io.redspace.ironslib.attribute.AttributeEventsHandler;
import io.redspace.ironslib.attribute.AttributeRemapRegistry;
import io.redspace.ironslib.attribute.data_handler.AttributeRemapReloadListener;
import io.redspace.ironslib.internal.compat.CompatHandler;
import io.redspace.ironslib.internal.network.PayloadHandler;
import io.redspace.ironslib.internal.network.packet.SyncPatreonDataPacket;
import io.redspace.ironslib.internal.registry.CommandHandler;
import io.redspace.ironslib.patreon.PatreonData;
import io.redspace.ironslib.patreon.PatreonHandler;
import io.redspace.ironslib.registry.IronsLibRegistries;
import io.redspace.ironslib.soulfire.SoulFireHooks;
import java.util.ArrayList;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.CreativeModeTabs;
import org.slf4j.Logger;

public final class IronsLib implements ModInitializer {
   public static final String MODID = "irons_lib";
   public static final String LEGACY_MODID = "irons_patreon_lib";
   public static final Logger LOGGER = LogUtils.getLogger();
   public static String version = "unknown";
   public static MinecraftServer server;

   @Override
   public void onInitialize() {
      version = FabricLoader.getInstance().getModContainer(MODID).orElseThrow().getMetadata().getVersion().getFriendlyString();
      IronsLibRegistries.register();
      PayloadHandler.register();
      CompatHandler.init();
      SoulFireHooks.initialize();
      CommandHandler.initialize();
      CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(output -> {
         output.accept(IronsLibRegistries.ItemRegistry.TRANSMOG_TABLE_BLOCK_ITEM);
         output.accept(IronsLibRegistries.ItemRegistry.PLAYER_STATUE_BLOCK_ITEM);
      });
      ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(id("attribute_remapper"), new AttributeRemapReloadListener());
      ServerLifecycleEvents.SERVER_STARTING.register(startingServer -> {
         server = startingServer;
         PatreonHandler.initialize(startingServer);
      });
      ServerLifecycleEvents.SERVER_STOPPED.register(stoppedServer -> {
         PatreonHandler.shutdown();
         server = null;
      });
      ServerPlayConnectionEvents.JOIN.register((handler, sender, currentServer) -> syncPlayer(handler.player));
      ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
         if (!joined) {
            ServerPlayNetworking.send(player, AttributeRemapRegistry.buildSyncPacket());
         }
      });
   }

   private static void syncPlayer(ServerPlayer player) {
      PatreonData data = PatreonData.getInstance();
      if (!data.isEmpty()) {
         ServerPlayNetworking.send(
            player, new SyncPatreonDataPacket(data.getChronicleEntries(), new ArrayList<>(data.getProfileValues()))
         );
      }
      ServerPlayNetworking.send(player, AttributeRemapRegistry.buildSyncPacket());
   }

   public static Identifier id(String path) {
      return Identifier.fromNamespaceAndPath(MODID, path);
   }
}
