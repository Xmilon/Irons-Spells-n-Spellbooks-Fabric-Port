package io.redspace.ironslib.statue.block.statue_block;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import com.geckolib.animation.state.BoneSnapshot;
import com.geckolib.cache.animation.Animation;
import com.geckolib.cache.animation.BoneAnimation;
import com.geckolib.cache.animation.KeyframeStack;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.util.GeckoLibUtil;
import io.redspace.ironslib.statue.PlayerStatuePose;
import io.redspace.ironslib.statue.StatueData;
import io.redspace.ironslib.statue.block.statue_block.player.PlayerStatueBlockRenderer;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.util.TriConsumer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class PlayerStatueModel
   extends GeoModel<PlayerStatueModel>
   implements GeoAnimatable,
   GeoRenderer<PlayerStatueModel, Void, PlayerStatueBlockRenderer.PlayerStatueBlockRenderState> {
   private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath("irons_lib", "statue_poses");
   private final Identifier model;
   private final Identifier texture;
   AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public PlayerStatueModel(String modid, String name) {
      this(
         Identifier.fromNamespaceAndPath(modid, String.format("statue/%s", name)),
         Identifier.fromNamespaceAndPath(modid, String.format("textures/entity/statue/%s.png", name))
      );
   }

   public PlayerStatueModel(Identifier model, Identifier texture) {
      this.model = model;
      this.texture = texture;
   }

   public @NonNull Identifier getModelResource(@NonNull GeoRenderState renderState) {
      return this.model;
   }

   public @NonNull Identifier getTextureResource(@NonNull GeoRenderState renderState) {
      return ((PlayerStatueBlockRenderer.PlayerStatueBlockRenderState)renderState).statueTextureHolder.textureLocation();
   }

   public @NonNull Identifier getAnimationResource(PlayerStatueModel animatable) {
      return ANIMATION;
   }

   public void registerControllers(@NonNull ControllerRegistrar controllers) {
   }

   public void adjustModelBonesForRender(
      @NonNull RenderPassInfo<PlayerStatueBlockRenderer.PlayerStatueBlockRenderState> renderPassInfo, @NonNull BoneSnapshots snapshots
   ) {
      StatueData statueData = ((PlayerStatueBlockRenderer.PlayerStatueBlockRenderState)renderPassInfo.renderState()).statueData;
      if (statueData == null) {
         statueData = new StatueData(null, PlayerStatuePose.defaultPose(), false);
      }

      Animation poseAnimation = this.getBakedAnimation(null, statueData.pose().getSerializedName());
      Map<String, BoneAnimation> mappedPose = Arrays.stream(poseAnimation.boneAnimations())
         .collect(Collectors.toMap(BoneAnimation::boneName, Function.identity()));
      if (statueData.flipped()) {
         BoneAnimation tmp = mappedPose.get("arm_l");
         mappedPose.put("arm_l", mappedPose.get("arm_r"));
         mappedPose.put("arm_r", tmp);
         tmp = mappedPose.get("leg_l");
         mappedPose.put("leg_l", mappedPose.get("leg_r"));
         mappedPose.put("leg_r", tmp);
      }

      boolean flipped = statueData.flipped();
      mappedPose.forEach((bone, anim) -> snapshots.get(bone).ifPresent(boneSnapshot -> this.applyAnimation(boneSnapshot, anim, flipped)));
   }

   private void applyAnimation(BoneSnapshot boneSnapshot, BoneAnimation boneAnimation, boolean mirror) {
      if (boneAnimation != null) {
         KeyframeStack rotationKeyFrames = boneAnimation.rotationKeyFrames();
         KeyframeStack positionKeyFrames = boneAnimation.positionKeyFrames();
         this.applyValue(rotationKeyFrames, boneSnapshot::setRotation, false, mirror);
         this.applyValue(positionKeyFrames, boneSnapshot::setTranslation, true, mirror);
      }
   }

   private void applyValue(KeyframeStack keyframe, TriConsumer<Float, Float, Float> function, boolean position, boolean flipped) {
      float x = keyframe.xKeyframes().length == 0 ? 0.0F : (float)keyframe.xKeyframes()[0].startValue().get(null);
      float y = keyframe.yKeyframes().length == 0 ? 0.0F : (float)keyframe.yKeyframes()[0].startValue().get(null);
      float z = keyframe.zKeyframes().length == 0 ? 0.0F : (float)keyframe.zKeyframes()[0].startValue().get(null);
      if (flipped) {
         if (position) {
            x = -x;
         } else {
            y = -y;
            z = -z;
         }
      }

      function.accept(x, y, z);
   }

   public @NonNull AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public @NonNull GeoModel<PlayerStatueModel> getGeoModel() {
      return this;
   }

   public PlayerStatueBlockRenderer.PlayerStatueBlockRenderState createRenderState(PlayerStatueModel animatable, @Nullable Void relatedObject) {
      return new PlayerStatueBlockRenderer.PlayerStatueBlockRenderState();
   }

   public void fireCompileRenderLayersEvent() {
   }

   public void fireCompileRenderStateEvent(
      PlayerStatueModel animatable, @Nullable Void relatedObject, PlayerStatueBlockRenderer.PlayerStatueBlockRenderState renderState, float partialTick
   ) {
   }

   public boolean firePreRenderEvent(
      @NonNull RenderPassInfo<PlayerStatueBlockRenderer.PlayerStatueBlockRenderState> renderPassInfo, @NonNull SubmitNodeCollector renderTasks
   ) {
      return true;
   }
}
