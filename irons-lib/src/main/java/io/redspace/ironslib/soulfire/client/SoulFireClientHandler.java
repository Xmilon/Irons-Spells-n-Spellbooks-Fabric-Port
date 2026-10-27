package io.redspace.ironslib.soulfire.client;

import io.redspace.ironslib.soulfire.SoulFireHelper;
import io.redspace.ironslib.soulfire.SoulFireHooks;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;

public final class SoulFireClientHandler {
   private static final RenderStateDataKey<Boolean> SOUL_FIRE = RenderStateDataKey.create(() -> "irons_lib:soul_fire");

   public static void onSoulFire(int entityId, boolean soul) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.level != null) {
         Entity entity = minecraft.level.getEntity(entityId);
         if (entity != null) {
            SoulFireHooks.onSoulFirePacket(entity, soul);
         }
      }
   }

   public static void captureRenderState(Entity entity, EntityRenderState state) {
      state.setData(SOUL_FIRE, state.displayFireAnimation && SoulFireHelper.isSoulFire(entity));
   }

   public static boolean isSoulFire(EntityRenderState state) {
      return Boolean.TRUE.equals(state.getData(SOUL_FIRE));
   }
}
