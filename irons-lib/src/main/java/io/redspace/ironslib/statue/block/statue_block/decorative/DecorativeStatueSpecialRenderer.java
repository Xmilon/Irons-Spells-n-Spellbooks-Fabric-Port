package io.redspace.ironslib.statue.block.statue_block.decorative;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer.BakingContext;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class DecorativeStatueSpecialRenderer implements SpecialModelRenderer<DecorativeStatueSpecialRenderer.RenderContext> {
   public DecorativeStatueSpecialRenderer.@Nullable RenderContext extractArgument(ItemStack stack) {
      if (stack.getItem() instanceof BlockItem blockItem) {
         Block block = blockItem.getBlock();
         if (block instanceof BaseEntityBlock entityBlock) {
            BlockEntityType<?> blockEntityType = resolveBlockEntityType(block, entityBlock);
            return blockEntityType == null ? null : new DecorativeStatueSpecialRenderer.RenderContext(block, blockEntityType);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public void submit(
      DecorativeStatueSpecialRenderer.@Nullable RenderContext context,
      @NonNull PoseStack poseStack,
      @NonNull SubmitNodeCollector submitNodeCollector,
      int lightCoords,
      int overlayCoords,
      boolean hasFoil,
      int outlineColor
   ) {
      if (context != null) {
         BlockEntity blockEntity = DecorativeStatueBlockEntity.from(() -> (BlockEntityType<DecorativeStatueBlockEntity>)context.blockEntityType())
            .create(BlockPos.ZERO, context.block().defaultBlockState());
         Minecraft minecraft = Minecraft.getInstance();
         if (minecraft.level != null) {
            blockEntity.setLevel(minecraft.level);
         }

         BlockEntityRenderer<BlockEntity, BlockEntityRenderState> renderer = minecraft.getBlockEntityRenderDispatcher().getRenderer(blockEntity);
         if (renderer != null) {
            BlockEntityRenderState state = renderer.createRenderState();
            renderer.extractRenderState(blockEntity, state, 0.0F, Vec3.ZERO, null);
            renderer.submit(state, poseStack, submitNodeCollector, new CameraRenderState());
         }
      }
   }

   public void getExtents(Consumer<Vector3fc> output) {
   }

   private static @Nullable BlockEntityType<?> resolveBlockEntityType(Block block, BaseEntityBlock entityBlock) {
      if (block instanceof DecorativeStatueBlock decorativeStatue) {
         return (BlockEntityType<?>)decorativeStatue.type.value();
      } else {
         BlockEntity blockEntity = entityBlock.newBlockEntity(BlockPos.ZERO, block.defaultBlockState());
         return blockEntity != null ? blockEntity.getType() : null;
      }
   }

   public record RenderContext(Block block, BlockEntityType<?> blockEntityType) {
   }

   public record Unbaked() implements net.minecraft.client.renderer.special.SpecialModelRenderer.Unbaked<DecorativeStatueSpecialRenderer.RenderContext> {
      public static final MapCodec<DecorativeStatueSpecialRenderer.Unbaked> MAP_CODEC = MapCodec.unit(new DecorativeStatueSpecialRenderer.Unbaked());

      public MapCodec<DecorativeStatueSpecialRenderer.Unbaked> type() {
         return MAP_CODEC;
      }

      public SpecialModelRenderer<DecorativeStatueSpecialRenderer.RenderContext> bake(BakingContext context) {
         return new DecorativeStatueSpecialRenderer();
      }
   }
}
