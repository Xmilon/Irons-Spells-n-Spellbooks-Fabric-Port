package io.redspace.ironslib.statue.block.statue_block.decorative;

import io.redspace.ironslib.statue.block.statue_block.AbstractStatueBlock;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class DecorativeStatueBlockEntity extends BlockEntity {
   public static BlockEntitySupplier<DecorativeStatueBlockEntity> from(Supplier<BlockEntityType<DecorativeStatueBlockEntity>> type) {
      return (pos, state) -> new DecorativeStatueBlockEntity(type.get(), pos, state);
   }

   public DecorativeStatueBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
      super(type, pos, blockState);
   }

   public boolean isPrimary() {
      BlockState state = this.getBlockState();
      return (Integer)state.getValue(AbstractStatueBlock.X_POS) == 0
         && (Integer)state.getValue(AbstractStatueBlock.Y_POS) == 0
         && (Integer)state.getValue(AbstractStatueBlock.Z_POS) == 0;
   }

   @NotNull
   public Optional<DecorativeStatueBlockEntity> getPrimaryControllerOpt() {
      return Optional.ofNullable(this.getPrimaryController());
   }

   @Nullable
   public DecorativeStatueBlockEntity getPrimaryController() {
      BlockState state = this.getBlockState();
      int xPos = (Integer)state.getValue(AbstractStatueBlock.X_POS);
      int yPos = (Integer)state.getValue(AbstractStatueBlock.Y_POS);
      int zPos = (Integer)state.getValue(AbstractStatueBlock.Z_POS);
      if (xPos == 0 && yPos == 0 && zPos == 0) {
         return this;
      } else {
         return this.level != null
               && this.level.getBlockEntity(this.getBlockPos().offset(-xPos, -yPos, -zPos)) instanceof DecorativeStatueBlockEntity statueBlock
            ? statueBlock
            : null;
      }
   }
}
