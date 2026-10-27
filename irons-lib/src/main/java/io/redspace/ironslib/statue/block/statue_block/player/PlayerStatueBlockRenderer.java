package io.redspace.ironslib.statue.block.statue_block.player;

import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.GeoRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironslib.statue.PlayerStatueModelType;
import io.redspace.ironslib.statue.StatueData;
import io.redspace.ironslib.statue.block.statue_block.PlayerStatueModel;
import io.redspace.ironslib.statue.block.statue_block.StaticModel;
import io.redspace.ironslib.statue.client.StatueTextureHolder;
import io.redspace.ironslib.statue.client.StatueTextureManager;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jspecify.annotations.NonNull;

public class PlayerStatueBlockRenderer implements BlockEntityRenderer<PlayerStatueBlockEntity, PlayerStatueBlockRenderer.PlayerStatueBlockRenderState> {
   StaticModel nullModel = new StaticModel("irons_lib", "sbeve");
   StaticModel statueBaseModel = new StaticModel("irons_lib", "statue_base");
   private static final Map<PlayerStatueModelType, PlayerStatueModel> PLAYER_MODELS = Map.of(
      PlayerStatueModelType.WIDE,
      new PlayerStatueModel("irons_lib", "player"),
      PlayerStatueModelType.SLIM,
      new PlayerStatueModel("irons_lib", "player_slim"),
      PlayerStatueModelType.LEGACY,
      new PlayerStatueModel("irons_lib", "player_legacy")
   );

   public PlayerStatueBlockRenderer(Context context) {
   }

   @NotNull
   private StatueTextureHolder resolvePlayerStatue(@Nullable StatueData statueData) {
      return StatueTextureManager.resolve(statueData);
   }

   @NotNull
   public boolean shouldRenderOffScreen() {
      return true;
   }

   public PlayerStatueBlockRenderer.PlayerStatueBlockRenderState createRenderState() {
      return new PlayerStatueBlockRenderer.PlayerStatueBlockRenderState();
   }

   public void extractRenderState(
      PlayerStatueBlockEntity blockEntity,
      PlayerStatueBlockRenderer.PlayerStatueBlockRenderState state,
      float partialTicks,
      @NonNull Vec3 cameraPosition,
      @org.jspecify.annotations.Nullable CrumblingOverlay breakProgress
   ) {
      BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
      state.statueTextureHolder = null;
      state.statueData = null;
      if (blockEntity.isPrimary()) {
         state.statueTextureHolder = this.resolvePlayerStatue(blockEntity.getStatueData());
         state.statueData = blockEntity.getStatueData();
         state.blockState = blockEntity.getBlockState();
      }
   }

   public void submit(
      PlayerStatueBlockRenderer.PlayerStatueBlockRenderState state,
      @NonNull PoseStack poseStack,
      @NonNull SubmitNodeCollector submitNodeCollector,
      @NonNull CameraRenderState camera
   ) {
      StatueTextureHolder statueTextureHolder = state.statueTextureHolder;
      if (statueTextureHolder != null) {
         poseStack.pushPose();
         BlockState blockState = state.blockState;
         poseStack.translate(((PlayerStatueBlock)blockState.getBlock()).xSize * 0.5F, 0.0F, ((PlayerStatueBlock)blockState.getBlock()).zSize * 0.5F);
         poseStack.rotateDegrees(Axis.YP, -RotationSegment.convertToDegrees((Integer)state.blockState.getValue(SkullBlock.ROTATION)));
         this.statueBaseModel.performRenderPass(state, poseStack, submitNodeCollector, camera);
         float statueBaseHeight = 0.125F;
         poseStack.translate(0.0F, statueBaseHeight, 0.0F);
         if (statueTextureHolder == StatueTextureManager.NULL) {
            this.nullModel.performRenderPass(state, poseStack, submitNodeCollector, camera);
         } else {
            PlayerStatueModel playermodel = PLAYER_MODELS.get(statueTextureHolder.modelType());
            playermodel.performRenderPass(state, poseStack, submitNodeCollector, camera);
         }

         poseStack.popPose();
      }
   }

   public static class PlayerStatueBlockRenderState extends BlockEntityRenderState implements GeoRenderState {
      @Nullable
      public StatueTextureHolder statueTextureHolder;
      @Nullable
      public StatueData statueData;
      public BlockState blockState;
      final Map<DataTicket<?>, Object> data = new Reference2ObjectOpenHashMap();

      @Internal
      public @NonNull Map<DataTicket<?>, Object> getDataMap() {
         return this.data;
      }
   }
}
