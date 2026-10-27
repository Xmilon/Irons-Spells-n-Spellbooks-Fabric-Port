package io.redspace.ironslib.statue.block.statue_block.player;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import io.redspace.ironslib.internal.network.packet.CritParticlesAreaPacket;
import io.redspace.ironslib.internal.network.packet.OpenStatuePoseScreenPacket;
import io.redspace.ironslib.patreon.PatreonData;
import io.redspace.ironslib.statue.StatueData;
import io.redspace.ironslib.statue.block.statue_block.AbstractStatueBlock;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerStatueBlock extends AbstractStatueBlock {

   public PlayerStatueBlock(Properties properties) {
      super(1, 2, 1, properties);
   }

   @Nullable
   public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
      return new PlayerStatueBlockEntity(pos, state);
   }

   @NotNull
   protected InteractionResult useItemOn(
      @NotNull ItemStack stack,
      @NotNull BlockState state,
      @NotNull Level level,
      @NotNull BlockPos pos,
      @NotNull Player player,
      @NotNull InteractionHand hand,
      @NotNull BlockHitResult hitResult
   ) {
      if (stack.is(Items.NAME_TAG) && stack.has(DataComponents.CUSTOM_NAME)) {
         String username = ((Component)stack.get(DataComponents.CUSTOM_NAME)).getString();
         if (level.getBlockEntity(pos) instanceof PlayerStatueBlockEntity statueBlockEntity
            && PatreonData.getPatreonPermissionsByUsername(username).supportsStatues()) {
            UUID uuid = PatreonData.profileFromUsername(username);
            if (uuid != null) {
               StatueData statueData = statueBlockEntity.getStatueData();
               if (statueData == null || !statueData.uuid().equals(uuid)) {
                  statueBlockEntity.setPlayerUuid(uuid);
                  level.playSound(null, pos, SoundEvents.AXE_SCRAPE.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
                  if (level instanceof ServerLevel serverLevel) {
                     Vec3i offset = new Vec3i(-(Integer)state.getValue(X_POS), -(Integer)state.getValue(Y_POS), -(Integer)state.getValue(Z_POS));
                     var payload = new CritParticlesAreaPacket(pos.offset(offset), pos.offset(offset).offset(this.xSize - 1, this.ySize - 1, this.zSize - 1));
                     PlayerLookup.tracking(serverLevel, ChunkPos.containing(pos)).forEach(recipient -> ServerPlayNetworking.send(recipient, payload));
                  }

                  statueBlockEntity.setChanged();
                  if (!player.hasInfiniteMaterials()) {
                     stack.shrink(1);
                  }

                  return InteractionResult.SUCCESS;
               }
            }
         }
      }

      return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
   }

   @NotNull
   protected InteractionResult useWithoutItem(
      @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult
   ) {
      if (player.isCrouching()) {
         if (player instanceof ServerPlayer serverPlayer) {
            ServerPlayNetworking.send(serverPlayer, new OpenStatuePoseScreenPacket(pos));
         }

         return InteractionResult.SUCCESS;
      } else {
         return super.useWithoutItem(state, level, pos, player, hitResult);
      }
   }
}
