package io.redspace.ironslib.statue.block.statue_block.player;

import io.redspace.ironslib.registry.IronsLibRegistries;
import io.redspace.ironslib.statue.PlayerStatuePose;
import io.redspace.ironslib.statue.StatueData;
import io.redspace.ironslib.statue.block.statue_block.AbstractStatueBlock;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockBox;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentMap.Builder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

public class PlayerStatueBlockEntity extends BlockEntity {
   @Nullable
   private StatueData statueData = null;

   public static PlayerStatueBlockEntity renderable(@Nullable StatueData statueData) {
      PlayerStatueBlockEntity fakeBlock = new PlayerStatueBlockEntity(
         BlockPos.ZERO, ((PlayerStatueBlock)IronsLibRegistries.BlockRegistry.PLAYER_STATUE_BLOCK).defaultBlockState()
      );
      fakeBlock.statueData = statueData;
      return fakeBlock;
   }

   public PlayerStatueBlockEntity(BlockPos pos, BlockState blockState) {
      super((BlockEntityType)IronsLibRegistries.BlockRegistry.STATUE_BLOCK_ENTITY, pos, blockState);
   }

   @Nullable
   public StatueData getStatueData() {
      PlayerStatueBlockEntity statue = this.getPrimaryController();
      return statue == null ? null : statue.statueData;
   }

   public void setStatueData(@Nullable StatueData statueData) {
      this.getPrimaryControllerOpt().ifPresent(statue -> statue.setControllerStatueData(statueData));
   }

   private void setControllerStatueData(@Nullable StatueData statueData) {
      this.statueData = statueData;
      if (this.level != null && this.getBlockState().getBlock() instanceof AbstractStatueBlock statueBlock) {
         for (BlockPos childPos : BlockBox.of(
            this.getBlockPos(), this.getBlockPos().offset(statueBlock.xSize - 1, statueBlock.ySize - 1, statueBlock.zSize - 1)
         )) {
            if (!childPos.equals(this.worldPosition) && this.level.getBlockEntity(childPos) instanceof PlayerStatueBlockEntity child) {
               child.statueData = this.statueData;
               setChanged(this.level, childPos, this.level.getBlockState(childPos));
            }
         }

         this.setChanged();
      }
   }

   public void setControllerFrom(PlayerStatueBlockEntity other) {
      this.setControllerStatueData(other.statueData);
   }

   public void setPlayerUuid(UUID uuid) {
      if (this.statueData == null) {
         this.setStatueData(new StatueData(uuid, PlayerStatuePose.defaultPose(), false));
      } else {
         this.setStatueData(this.statueData.updateUUID(uuid));
      }
   }

   public void setPose(PlayerStatuePose pose) {
      if (this.statueData != null) {
         this.setStatueData(this.statueData.updatePose(pose));
      }
   }

   public void setFlipped(boolean flipped) {
      if (this.statueData != null) {
         this.setStatueData(this.statueData.updateFlipped(flipped));
      }
   }

   public boolean isPrimary() {
      BlockState state = this.getBlockState();
      return (Integer)state.getValue(AbstractStatueBlock.X_POS) == 0
         && (Integer)state.getValue(AbstractStatueBlock.Y_POS) == 0
         && (Integer)state.getValue(AbstractStatueBlock.Z_POS) == 0;
   }

   @NotNull
   public Optional<PlayerStatueBlockEntity> getPrimaryControllerOpt() {
      return Optional.ofNullable(this.getPrimaryController());
   }

   @Nullable
   public PlayerStatueBlockEntity getPrimaryController() {
      BlockState state = this.getBlockState();
      int xPos = (Integer)state.getValue(AbstractStatueBlock.X_POS);
      int yPos = (Integer)state.getValue(AbstractStatueBlock.Y_POS);
      int zPos = (Integer)state.getValue(AbstractStatueBlock.Z_POS);
      if (xPos == 0 && yPos == 0 && zPos == 0) {
         return this;
      } else {
         return this.level != null && this.level.getBlockEntity(this.getBlockPos().offset(-xPos, -yPos, -zPos)) instanceof PlayerStatueBlockEntity statueBlock
            ? statueBlock
            : null;
      }
   }

   public void setChanged() {
      super.setChanged();
      if (this.level != null && !this.level.isClientSide()) {
         BlockState state = this.getBlockState();
         this.level.sendBlockUpdated(this.worldPosition, state, state, 2);
      }
   }

   protected void saveAdditional(ValueOutput output) {
      super.saveAdditional(output);
      output.storeNullable("statueData", StatueData.CODEC, this.statueData);
   }

   protected void loadAdditional(ValueInput input) {
      super.loadAdditional(input);
      this.statueData = (StatueData)input.read("statueData", StatueData.CODEC).orElse(null);
   }

   @NotNull
   public CompoundTag getUpdateTag(@NotNull Provider registries) {
      return this.saveWithoutMetadata(registries);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   protected void applyImplicitComponents(@NotNull DataComponentGetter components) {
      StatueData data = (StatueData)components.get((DataComponentType)IronsLibRegistries.ComponentRegistry.STATUE_ITEM_DATA);
      if (data != null) {
         this.statueData = data;
      }
   }

   protected void collectImplicitComponents(@NotNull Builder components) {
      if (this.statueData != null) {
         components.set((DataComponentType)IronsLibRegistries.ComponentRegistry.STATUE_ITEM_DATA, this.statueData);
      }
   }
}
