package io.redspace.ironslib.internal.client;

import io.redspace.ironslib.statue.block.statue_block.StatuePoseScreen;
import io.redspace.ironslib.statue.block.statue_block.player.PlayerStatueBlockEntity;
import io.redspace.ironslib.statue.client.StatueTextureManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class ClientHelper {
   public static void openStatuePoseScreen(BlockPos blockPos) {
      if (Minecraft.getInstance().level != null && Minecraft.getInstance().level.getBlockEntity(blockPos) instanceof PlayerStatueBlockEntity statueBlock) {
         Minecraft.getInstance().setScreenAndShow(new StatuePoseScreen(blockPos, statueBlock.getStatueData()));
      }
   }

   public static void onPayloadRefresh() {
      StatueTextureManager.refresh();
   }

   public static void spawnCritParticlesInArea(BlockPos minPos, BlockPos maxPos) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         int minX = Math.min(minPos.getX(), maxPos.getX());
         int maxX = Math.max(minPos.getX(), maxPos.getX());
         int minY = Math.min(minPos.getY(), maxPos.getY());
         int maxY = Math.max(minPos.getY(), maxPos.getY());
         int minZ = Math.min(minPos.getZ(), maxPos.getZ());
         int maxZ = Math.max(minPos.getZ(), maxPos.getZ());
         int dx = maxX - minX + 1;
         int dy = maxY - minY + 1;
         int dz = maxZ - minZ + 1;
         long volume = (long)dx * dy * dz;
         int particleCount = (int)Mth.clamp(volume * 8L, 16L, 256L);
         RandomSource random = level.getRandom();

         for (int i = 0; i < particleCount; i++) {
            double x = minX + random.nextDouble() * dx;
            double y = minY + random.nextDouble() * dy;
            double z = minZ + random.nextDouble() * dz;
            level.addParticle(ParticleTypes.CRIT, x, y, z, random.nextGaussian() * 0.02, random.nextGaussian() * 0.02, random.nextGaussian() * 0.02);
         }
      }
   }
}
