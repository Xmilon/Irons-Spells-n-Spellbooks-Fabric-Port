package io.redspace.ironsspellbooks.setup;

import io.redspace.ironsspellbooks.capabilities.magic.PocketDimensionManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.api.config.SpellConfigManager;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.compat.CompatHandler;
import io.redspace.ironsspellbooks.content.ContentPackManager;
import io.redspace.ironsspellbooks.content.ContentPackEvents;
import io.redspace.ironsspellbooks.content.SpellSchoolMasteryPack;
import io.redspace.ironsspellbooks.data.IronsDataStorage;
import io.redspace.ironsspellbooks.effect.guiding_bolt.GuidingBoltManager;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import io.redspace.ironsspellbooks.worldgen.IceSpiderPatrolSpawner;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gamerules.GameRules;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;


public class ModSetup {
    private static final IceSpiderPatrolSpawner ICE_SPIDER_PATROL_SPAWNER = new IceSpiderPatrolSpawner();

    public static void setup() {
        Messages.register();
        CompatHandler.init();
        NeoForge.EVENT_BUS.register(ContentPackEvents.class);
        FabricEventBridge.init();

        // Drive server-level magic and pocket-dimension ticking on Fabric.
        ServerTickEvents.START_WORLD_TICK.register(world -> {
            if (world.isClientSide()) {
                return;
            }
            io.redspace.ironsspellbooks.IronsSpellbooks.MAGIC_MANAGER.tick(world);
            PocketDimensionManager.INSTANCE.tick(world);
        });

        // Restore custom patrol spawner behavior.
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world.isClientSide()) {
                return;
            }
            boolean doMobSpawning = world.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING);
            ICE_SPIDER_PATROL_SPAWNER.tick(world, doMobSpawning, doMobSpawning);
        });

        // Sync player-side spell/mana state and spell config when a player joins.
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (handler.getPlayer() instanceof ServerPlayer serverPlayer) {
                syncPlayerMagicState(serverPlayer);
                SpellConfigManager.onDatapackSync(new OnDatapackSyncEvent(server.getPlayerList(), serverPlayer));
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                io.redspace.ironsspellbooks.IronsSpellbooks.MAGIC_MANAGER.forget(handler.getPlayer().getUUID()));

        // Preserve and resync player magic state across respawn/death.
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            MagicData.getPlayerMagicData(newPlayer).copyFrom(
                    MagicData.getPlayerMagicData(oldPlayer),
                    newPlayer.registryAccess(),
                    !alive
            );
        });
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> syncPlayerMagicState(newPlayer));
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> syncPlayerMagicState(player));

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            io.redspace.ironsspellbooks.IronsSpellbooks.MCS = server;
            io.redspace.ironsspellbooks.IronsSpellbooks.OVERWORLD = server.overworld();
            IronsDataStorage.init(server.overworld().getDataStorage());
            SpellConfigManager.onDatapackSync(new OnDatapackSyncEvent(server.getPlayerList(), null));
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            io.redspace.ironsspellbooks.IronsSpellbooks.MCS = null;
            io.redspace.ironsspellbooks.IronsSpellbooks.OVERWORLD = null;
        });

    }

    private static void syncPlayerMagicState(ServerPlayer serverPlayer) {
        var magicData = MagicData.getPlayerMagicData(serverPlayer);
        magicData.getSyncedData().syncToPlayer(serverPlayer);
        magicData.getPlayerCooldowns().syncToPlayer(serverPlayer);
        magicData.getPlayerRecasts().syncAllToPlayer();
        PacketDistributor.sendToPlayer(serverPlayer, new SyncManaPacket(magicData, serverPlayer));
        if (ContentPackManager.INSTANCE.isEnabled(SpellSchoolMasteryPack.ID)) {
            SpellSchoolMasteryPack.applyAllBonuses(serverPlayer);
        }
    }
}
