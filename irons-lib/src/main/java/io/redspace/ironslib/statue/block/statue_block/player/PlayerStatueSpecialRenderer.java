package io.redspace.ironslib.statue.block.statue_block.player;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import io.redspace.ironslib.statue.StatueData;
import io.redspace.ironslib.statue.client.StatueTextureHolder;
import io.redspace.ironslib.statue.client.StatueTextureManager;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer.BakingContext;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class PlayerStatueSpecialRenderer implements SpecialModelRenderer<PlayerStatueSpecialRenderer.RenderContext> {
   public PlayerStatueSpecialRenderer.@NonNull RenderContext extractArgument(ItemStack stack) {
      StatueData statueData = StatueData.get(stack);
      return new PlayerStatueSpecialRenderer.RenderContext(statueData, StatueTextureManager.resolve(statueData));
   }

   public void submit(
      PlayerStatueSpecialRenderer.@Nullable RenderContext context,
      @NonNull PoseStack poseStack,
      @NonNull SubmitNodeCollector submitNodeCollector,
      int lightCoords,
      int overlayCoords,
      boolean hasFoil,
      int outlineColor
   ) {
      PlayerStatueBlockEntity blockEntity = PlayerStatueBlockEntity.renderable(context != null ? context.statueData() : null);
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.level != null) {
         blockEntity.setLevel(minecraft.level);
      }

      BlockEntityRenderer<BlockEntity, BlockEntityRenderState> renderer = minecraft.getBlockEntityRenderDispatcher().getRenderer(blockEntity);
      if (renderer != null) {
         BlockEntityRenderState state = renderer.createRenderState();
         renderer.extractRenderState(blockEntity, state, 0.0F, Vec3.ZERO, null);
         state.lightCoords = lightCoords;
         renderer.submit(state, poseStack, submitNodeCollector, new CameraRenderState());
      }
   }

   public void getExtents(Consumer<Vector3fc> output) {
   }

   public record RenderContext(@Nullable StatueData statueData, StatueTextureHolder textureHolder) {
   }

   public record Unbaked() implements net.minecraft.client.renderer.special.SpecialModelRenderer.Unbaked<PlayerStatueSpecialRenderer.RenderContext> {
      public static final MapCodec<PlayerStatueSpecialRenderer.Unbaked> MAP_CODEC = MapCodec.unit(new PlayerStatueSpecialRenderer.Unbaked());

      public MapCodec<PlayerStatueSpecialRenderer.Unbaked> type() {
         return MAP_CODEC;
      }

      public SpecialModelRenderer<PlayerStatueSpecialRenderer.RenderContext> bake(BakingContext context) {
         return new PlayerStatueSpecialRenderer();
      }
   }
}
