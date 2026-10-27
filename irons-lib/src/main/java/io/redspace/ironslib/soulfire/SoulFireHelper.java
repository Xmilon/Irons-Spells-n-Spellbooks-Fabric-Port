package io.redspace.ironslib.soulfire;

import net.minecraft.world.entity.Entity;

public final class SoulFireHelper {
   private SoulFireHelper() {
   }

   public static void igniteSoul(Entity entity, int ticks) {
      if (!entity.level().isClientSide()) {
         entity.igniteForTicks(ticks);
         setSoulFire(entity, true);
      }
   }

   public static void setSoulFire(Entity entity, boolean soul) {
      if (!entity.level().isClientSide()) {
         SoulFireState state = SoulFireHooks.state(entity);
         if (soul) {
            state.soulRequestedAt = entity.level().getGameTime();
         }

         SoulFireHooks.setSoul(entity, state, soul);
      }
   }

   public static boolean isSoulFire(Entity entity) {
      if (!entity.isOnFire()) {
         return false;
      }

      SoulFireState state = SoulFireHooks.state(entity);
      return entity.level().isClientSide() && entity.getRemainingFireTicks() > 0 ? state.localSoul : state.soul;
   }
}
