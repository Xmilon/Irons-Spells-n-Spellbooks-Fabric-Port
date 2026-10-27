package io.redspace.ironslib.util;

import io.redspace.ironslib.internal.client.gui_block_entity_renderer.GuiBlockEntityRenderState;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class GuiGraphicsHelper {
   public static void blit(GuiGraphicsExtractor gg, Identifier texture, int x, int y, int u, int v, int w, int h) {
      blit(gg, texture, x, y, u, v, w, h, 256, 256);
   }

   public static void blit(GuiGraphicsExtractor gg, Identifier texture, int x, int y, int u, int v, int w, int h, int texW, int texH) {
      gg.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, w, h, texW, texH);
   }

   public static void blitSprite(GuiGraphicsExtractor gg, Identifier sprite, int x, int y, int w, int h) {
      gg.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, w, h);
   }

   public static void blitSpriteColored(GuiGraphicsExtractor gg, Identifier sprite, int x, int y, int w, int h, int color) {
      gg.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, w, h, color);
   }

   public static void blitSpriteColored(GuiGraphicsExtractor gg, Identifier sprite, int x, int y, int w, int h, float r, float g, float b, float a) {
      gg.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, w, h, ARGB.colorFromFloat(a, r, g, b));
   }

   public static void drawString(GuiGraphicsExtractor gg, Font font, Component text, int x, int y, int color) {
      gg.text(font, text, x, y, color);
   }

   public static void drawString(GuiGraphicsExtractor gg, Font font, Component text, int x, int y, int color, boolean dropShadow) {
      gg.text(font, text, x, y, color, dropShadow);
   }

   public static void drawString(GuiGraphicsExtractor gg, Font font, String text, int x, int y, int color) {
      gg.text(font, text, x, y, color);
   }

   public static void renderTooltip(GuiGraphicsExtractor gg, Font font, List<Component> texts, Optional<TooltipComponent> optionalImage, int x, int y) {
      gg.setTooltipForNextFrame(font, texts, optionalImage, x, y);
   }

   public static void fill(GuiGraphicsExtractor gg, int x0, int y0, int x1, int y1, int color) {
      gg.fill(x0, y0, x1, y1, color);
   }

   public static void pushPose(GuiGraphicsExtractor gg) {
      gg.pose().pushMatrix();
   }

   public static void popPose(GuiGraphicsExtractor gg) {
      gg.pose().popMatrix();
   }

   public static void translate(GuiGraphicsExtractor gg, double x, double y) {
      gg.pose().translate((float)x, (float)y);
   }

   public static void translate(GuiGraphicsExtractor gg, double x, double y, double z) {
      gg.pose().translate((float)x, (float)y);
   }

   public static void scale(GuiGraphicsExtractor gg, float x, float y, float z) {
      gg.pose().scale(x, y);
   }

   public static void scale(GuiGraphicsExtractor gg, float x, float y) {
      gg.pose().scale(x, y);
   }

   public static void enableScissor(GuiGraphicsExtractor gg, int x0, int y0, int x1, int y1) {
      gg.enableScissor(x0, y0, x1, y1);
   }

   public static void disableScissor(GuiGraphicsExtractor gg) {
      gg.disableScissor();
   }

   public static void renderEntityInInventory(
      GuiGraphicsExtractor gg,
      int x0,
      int y0,
      int x1,
      int y1,
      float scale,
      Vector3f offset,
      Quaternionf rotation,
      @Nullable Quaternionf cameraAngle,
      LivingEntity entity
   ) {
      EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
      EntityRenderer<? super LivingEntity, ?> renderer = dispatcher.getRenderer(entity);
      EntityRenderState renderState = renderer.createRenderState(entity, 1.0F);
      renderState.shadowPieces.clear();
      renderState.outlineColor = 0;
      Vector3f translation = new Vector3f(offset.x, renderState.boundingBoxHeight / 2.0F + offset.y, offset.z);
      gg.entity(renderState, scale, translation, rotation, cameraAngle, x0, y0, x1, y1);
   }

   public static void renderBlockEntityInInventory(
      GuiGraphicsExtractor gg,
      int x0,
      int y0,
      int x1,
      int y1,
      float scale,
      Vector3f offset,
      Quaternionf rotation,
      @Nullable Quaternionf cameraAngle,
      BlockEntity blockEntity
   ) {
      Minecraft minecraft = Minecraft.getInstance();
      if (blockEntity.getLevel() == null && minecraft.level != null) {
         blockEntity.setLevel(minecraft.level);
      }

      BlockEntityRenderer<BlockEntity, BlockEntityRenderState> renderer = minecraft.getBlockEntityRenderDispatcher().getRenderer(blockEntity);
      if (renderer != null) {
         BlockEntityRenderState renderState = renderer.createRenderState();
         renderer.extractRenderState(blockEntity, renderState, 0.0F, Vec3.ZERO, null);
         Vector3f translation = new Vector3f(offset.x(), offset.y(), offset.z());
         gg.guiRenderState.addPicturesInPictureState(
            new GuiBlockEntityRenderState(renderState, translation, new Vector3f(-0.5F, 0.0F, -0.5F), rotation, cameraAngle, x0, y0, x1, y1, scale, null)
         );
      }
   }
}
