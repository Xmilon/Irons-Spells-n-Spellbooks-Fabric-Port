package io.redspace.ironslib.internal.client.gui_block_entity_renderer;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public record GuiBlockEntityRenderState(
   BlockEntityRenderState renderState,
   Vector3fc translation,
   Vector3fc rotationPivotOffset,
   Quaternionfc rotation,
   @Nullable Quaternionfc overrideCameraAngle,
   int x0,
   int y0,
   int x1,
   int y1,
   float scale,
   @Nullable ScreenRectangle scissorArea
) implements PictureInPictureRenderState {
   public GuiBlockEntityRenderState {
      translation = new Vector3f(translation);
      rotationPivotOffset = new Vector3f(rotationPivotOffset);
      rotation = new Quaternionf(rotation);
      overrideCameraAngle = overrideCameraAngle == null ? null : new Quaternionf(overrideCameraAngle);
   }

   public ScreenRectangle bounds() {
      return PictureInPictureRenderState.getBounds(this.x0, this.y0, this.x1, this.y1, this.scissorArea);
   }
}
