package io.redspace.ironslib.statue.block.statue_block;

import io.redspace.ironslib.statue.block.statue_block.player.PlayerStatueBlockEntity;
import java.util.function.Function;
import net.minecraft.core.BlockBox;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public abstract class AbstractStatueBlock extends BaseEntityBlock {
   public static final IntegerProperty X_POS = IntegerProperty.create("x_offset", 0, 3);
   public static final IntegerProperty Y_POS = IntegerProperty.create("y_offset", 0, 3);
   public static final IntegerProperty Z_POS = IntegerProperty.create("z_offset", 0, 3);
   public static final int MAX = RotationSegment.getMaxSegmentIndex();
   private static final int ROTATIONS = MAX + 1;
   public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
   public final int xSize;
   public final int ySize;
   public final int zSize;
   private final Function<BlockState, VoxelShape> shapesCache;

   public AbstractStatueBlock(int xSize, int ySize, int zSize, Properties properties) {
      super(properties);
      this.xSize = xSize;
      this.ySize = ySize;
      this.zSize = zSize;
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(ROTATION, 0));
      this.shapesCache = this.getShapeForEachState(this::makeShape);
   }

   private VoxelShape makeShape(BlockState state) {
      int margin = 2;
      int x = -16 * (Integer)state.getValue(X_POS) + margin;
      int y = -16 * (Integer)state.getValue(Y_POS);
      int z = -16 * (Integer)state.getValue(Z_POS) + margin;
      return Block.box(x, y, z, x + this.xSize * 16 - margin * 2, y + this.ySize * 16, z + this.zSize * 16 - margin * 2);
   }

   public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
      Level level = context.getLevel();
      BlockPos clickedPos = context.getClickedPos();
      MutableBlockPos originPos = clickedPos.mutable();
      float rotation = Mth.wrapDegrees(context.getRotation());
      Direction clickedFace = context.getClickedFace();
      if (clickedFace == Direction.DOWN) {
         originPos.move(Direction.DOWN, this.ySize - 1);
      }

      if (clickedFace.getAxis() == Axis.X ^ rotation > 0.0F) {
         originPos.move(Direction.WEST, this.xSize - 1);
      }

      if (clickedFace.getAxis() == Axis.Z ^ Mth.abs(rotation) > 90.0F) {
         originPos.move(Direction.NORTH, this.zSize - 1);
      }

      for (BlockPos pos : BlockBox.of(originPos, originPos.offset(this.xSize - 1, this.ySize - 1, this.zSize - 1))) {
         if (!level.getBlockState(pos).canBeReplaced(context) || !level.getWorldBorder().isWithinBounds(pos)) {
            return null;
         }
      }

      int xoff = clickedPos.getX() - originPos.getX();
      int yoff = clickedPos.getY() - originPos.getY();
      int zoff = clickedPos.getZ() - originPos.getZ();
      return (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState()
                  .setValue(ROTATION, RotationSegment.convertToSegment(context.getRotation())))
               .setValue(X_POS, xoff))
            .setValue(Y_POS, yoff))
         .setValue(Z_POS, zoff);
   }

   public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
      super.setPlacedBy(level, pos, state, placer, stack);
      if (!level.isClientSide()) {
         int originalX = (Integer)state.getValue(X_POS);
         int originalY = (Integer)state.getValue(Y_POS);
         int originalZ = (Integer)state.getValue(Z_POS);
         int originalRotation = (Integer)state.getValue(ROTATION);
         BlockPos originPos = pos.offset(-originalX, -originalY, -originalZ);

         for (int x = 0; x < this.xSize; x++) {
            for (int y = 0; y < this.ySize; y++) {
               for (int z = 0; z < this.zSize; z++) {
                  if (x != originalX || y != originalY || z != originalZ) {
                     BlockPos fillPos = originPos.offset(x, y, z);
                     BlockState fillState = (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(ROTATION, originalRotation))
                              .setValue(X_POS, x))
                           .setValue(Y_POS, y))
                        .setValue(Z_POS, z);
                     level.setBlock(fillPos, fillState, 3);
                     level.updateNeighborsAt(fillPos, Blocks.AIR);
                     if (x == 0
                        && y == 0
                        && z == 0
                        && level.getBlockEntity(pos) instanceof PlayerStatueBlockEntity self
                        && level.getBlockEntity(fillPos) instanceof PlayerStatueBlockEntity controller) {
                        controller.setControllerFrom(self);
                     }
                  }
               }
            }
         }

         if (originPos.equals(pos) && level.getBlockEntity(pos) instanceof PlayerStatueBlockEntity controller) {
            controller.setControllerFrom(controller);
         }
      }
   }

   @NotNull
   protected VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
      return this.shapesCache.apply(state);
   }

   protected @NonNull BlockState updateShape(
      @NonNull BlockState myState,
      @NonNull LevelReader level,
      @NonNull ScheduledTickAccess ticks,
      @NonNull BlockPos myPos,
      @NonNull Direction directionToNeighbour,
      @NonNull BlockPos neighbourPos,
      @NonNull BlockState neighbourState,
      @NonNull RandomSource random
   ) {
      int originalX = (Integer)myState.getValue(X_POS);
      int originalY = (Integer)myState.getValue(Y_POS);
      int originalZ = (Integer)myState.getValue(Z_POS);
      BlockPos originPos = myPos.offset(-originalX, -originalY, -originalZ);

      for (BlockPos pos : BlockBox.of(originPos, originPos.offset(this.xSize - 1, this.ySize - 1, this.zSize - 1))) {
         BlockState neighborState = level.getBlockState(pos);
         if (!neighborState.is(this)) {
            return Blocks.AIR.defaultBlockState();
         }
      }

      return super.updateShape(myState, level, ticks, myPos, directionToNeighbour, neighbourPos, neighbourState, random);
   }

   @NotNull
   protected BlockState rotate(BlockState state, Rotation rotation) {
      return (BlockState)state.setValue(ROTATION, rotation.rotate((Integer)state.getValue(ROTATION), ROTATIONS));
   }

   @NotNull
   protected BlockState mirror(BlockState state, Mirror mirror) {
      return (BlockState)state.setValue(ROTATION, mirror.mirror((Integer)state.getValue(ROTATION), ROTATIONS));
   }

   protected void createBlockStateDefinition(@NotNull Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{ROTATION, X_POS, Y_POS, Z_POS});
   }

   @NotNull
   protected RenderShape getRenderShape(@NotNull BlockState state) {
      return RenderShape.MODEL;
   }
}
