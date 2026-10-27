package io.redspace.ironslib.soulfire;

import io.redspace.ironslib.soulfire.network.ClientboundSoulFirePacket;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class SoulFireHooks {
   private static final Set<Entity> ENTITIES_TO_SYNC = new HashSet<>();

   private SoulFireHooks() {
   }

   public static void initialize() {
      ServerTickEvents.END_SERVER_TICK.register(server -> flush());
      EntityTrackingEvents.START_TRACKING.register(SoulFireHooks::onStartTracking);
      ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register((player, origin, destination) -> {
         if (state(player).soul) {
            ServerPlayNetworking.send(player, new ClientboundSoulFirePacket(player.getId(), true));
         }
      });
      ServerLifecycleEvents.SERVER_STOPPED.register(server -> ENTITIES_TO_SYNC.clear());
   }

   static SoulFireState state(Entity entity) {
      return ((ISoulFireEntity)entity).irons_lib$getSoulFireState();
   }

   public static void onSetRemainingFireTicks(Entity entity, int oldTicks, int newTicks) {
      boolean freshBurn = oldTicks <= 0 && newTicks > 0;
      if (freshBurn) {
         SoulFireState state = state(entity);
         if (entity.level().isClientSide()) {
            state.localSoul = ((ISoulFireEntity)entity).irons_lib$isOnFireShared() && state.soul;
         } else if (state.soulRequestedAt != entity.level().getGameTime()) {
            setSoul(entity, state, false);
         }
      }
   }

   public static void onInsideSoulFire(Entity entity) {
      if (entity.level().isClientSide()) {
         state(entity).localSoul = true;
      } else {
         SoulFireHelper.setSoulFire(entity, true);
      }
   }

   static void setSoul(Entity entity, SoulFireState state, boolean soul) {
      state.soul = soul;
      if (soul != state.sentSoul) {
         ENTITIES_TO_SYNC.add(entity);
      }
   }

   public static void onSoulFirePacket(Entity entity, boolean soul) {
      state(entity).soul = soul;
   }

   public static void flush() {
      for (Entity entity : ENTITIES_TO_SYNC) {
         SoulFireState state = state(entity);
         if (!entity.isRemoved() && state.soul != state.sentSoul) {
            state.sentSoul = state.soul;
            Set<ServerPlayer> recipients = new HashSet<>(PlayerLookup.tracking(entity));
            if (entity instanceof ServerPlayer player) {
               recipients.add(player);
            }
            var payload = new ClientboundSoulFirePacket(entity.getId(), state.soul);
            recipients.forEach(player -> ServerPlayNetworking.send(player, payload));
         }
      }

      ENTITIES_TO_SYNC.clear();
   }

   public static void onStartTracking(Entity target, ServerPlayer player) {
      if (state(target).soul) {
         ServerPlayNetworking.send(player, new ClientboundSoulFirePacket(target.getId(), true));
      }
   }
}
