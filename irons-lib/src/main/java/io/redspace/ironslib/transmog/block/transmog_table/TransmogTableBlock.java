package io.redspace.ironslib.transmog.block.transmog_table;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class TransmogTableBlock extends Block {
   public TransmogTableBlock(Properties properties) {
      super(properties);
   }

   public PushReaction getPistonPushReaction(@NotNull BlockState pState) {
      return PushReaction.IMMOVEABLE;
   }

   @NotNull
   protected InteractionResult useWithoutItem(
      @NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer, @NotNull BlockHitResult pHitResult
   ) {
      if (pLevel.isClientSide()) {
         return InteractionResult.SUCCESS;
      }

      pPlayer.openMenu(pState.getMenuProvider(pLevel, pPos));
      return InteractionResult.SUCCESS;
   }

   @Nullable
   public MenuProvider getMenuProvider(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos) {
      return new ExtendedMenuProvider<BlockPos>() {
         @Override
         public BlockPos getScreenOpeningData(ServerPlayer player) {
            return pPos;
         }

         @Override
         public Component getDisplayName() {
            return Component.translatable("block.irons_lib.transmog_table");
         }

         @Override
         public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
            return new TransmogTableMenu(id, inventory, ContainerLevelAccess.create(pLevel, pPos));
         }
      };
   }
}
