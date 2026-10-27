package io.redspace.ironslib.statue.block.statue_block;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.util.GeckoLibUtil;
import io.redspace.ironslib.statue.block.statue_block.player.PlayerStatueBlockRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class StaticModel
   extends GeoModel<StaticModel>
   implements GeoAnimatable,
   GeoRenderer<StaticModel, Void, PlayerStatueBlockRenderer.PlayerStatueBlockRenderState> {
   private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath("irons_lib", "animations/statue_poses.animation.json");
   private final Identifier model;
   private final Identifier texture;
   AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public StaticModel(String modid, String name) {
      this(
         Identifier.fromNamespaceAndPath(modid, String.format("statue/%s", name)),
         Identifier.fromNamespaceAndPath(modid, String.format("textures/entity/statue/%s.png", name))
      );
   }

   public StaticModel(Identifier model, Identifier texture) {
      this.model = model;
      this.texture = texture;
   }

   public @NonNull Identifier getModelResource(@NonNull GeoRenderState renderState) {
      return this.model;
   }

   public @NonNull Identifier getTextureResource(@NonNull GeoRenderState renderState) {
      return this.texture;
   }

   public @NonNull Identifier getAnimationResource(StaticModel animatable) {
      return ANIMATION;
   }

   public void registerControllers(@NonNull ControllerRegistrar controllers) {
   }

   public @NonNull AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public @NonNull GeoModel<StaticModel> getGeoModel() {
      return this;
   }

   public PlayerStatueBlockRenderer.PlayerStatueBlockRenderState createRenderState(StaticModel animatable, @Nullable Void relatedObject) {
      return new PlayerStatueBlockRenderer.PlayerStatueBlockRenderState();
   }

   public void fireCompileRenderLayersEvent() {
   }

   public void fireCompileRenderStateEvent(
      StaticModel animatable, @Nullable Void relatedObject, PlayerStatueBlockRenderer.PlayerStatueBlockRenderState renderState, float partialTick
   ) {
   }

   public boolean firePreRenderEvent(
      @NonNull RenderPassInfo<PlayerStatueBlockRenderer.PlayerStatueBlockRenderState> renderPassInfo, @NonNull SubmitNodeCollector renderTasks
   ) {
      return true;
   }
}
