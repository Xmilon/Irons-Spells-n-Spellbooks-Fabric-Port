package io.redspace.ironslib.internal.client.gui_block_entity_renderer;

import com.mojang.blaze3d.platform.Lighting.Entry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;

public class GuiBlockEntityRenderer extends PictureInPictureRenderer<GuiBlockEntityRenderState> {
   private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;

   public GuiBlockEntityRenderer() {
      this.blockEntityRenderDispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
   }

   public @NonNull Class<GuiBlockEntityRenderState> getRenderStateClass() {
      return GuiBlockEntityRenderState.class;
   }

   protected void renderToTexture(GuiBlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
      Minecraft.getInstance().gameRenderer.lighting().setupFor(Entry.ENTITY_IN_UI);
      Vector3fc translation = state.translation();
      Vector3fc rotationPivotOffset = state.rotationPivotOffset();
      poseStack.translate(translation.x(), translation.y(), translation.z());
      poseStack.rotate(state.rotation());
      poseStack.translate(rotationPivotOffset.x(), rotationPivotOffset.y(), rotationPivotOffset.z());
      CameraRenderState camera = new CameraRenderState();
      Quaternionfc overrideCameraAngle = state.overrideCameraAngle();
      if (overrideCameraAngle != null) {
         camera.orientation = overrideCameraAngle.conjugate(new Quaternionf()).rotateY((float) Math.PI);
      }

      this.blockEntityRenderDispatcher.submit(state.renderState(), poseStack, submitNodeCollector, camera);
   }

   protected float getTranslateY(int width, int height) {
      return height / 2.0F;
   }

   protected @NonNull String getTextureLabel() {
      return "irons_lib:block_entity";
   }
}
