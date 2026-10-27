package io.redspace.ironslib.statue.block.statue_block;

import io.redspace.ironslib.IronsLib;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import io.redspace.ironslib.internal.network.packet.SelectStatuePosePacket;
import io.redspace.ironslib.patreon.PatreonData;
import io.redspace.ironslib.statue.PlayerStatuePose;
import io.redspace.ironslib.statue.StatueData;
import io.redspace.ironslib.statue.block.statue_block.player.PlayerStatueBlockEntity;
import io.redspace.ironslib.util.GuiGraphicsHelper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class StatuePoseScreen extends Screen {
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("irons_lib", "textures/gui/statue_pose_screen.png");
   private static final Quaternionf PREVIEW_ANGLE = new Quaternionf().rotationXYZ(0.43633232F, (float) (-Math.PI / 6), (float) Math.PI);
   private static final Quaternionf OPTION_ANGLE = new Quaternionf().rotationXYZ(0.43633232F, (float) (Math.PI / 6), (float) Math.PI);
   private static final int PREVIEW_X = 8;
   private static final int PREVIEW_Y = 24;
   private static final int PREVIEW_WIDTH = 108;
   private static final int PREVIEW_HEIGHT = 162;
   private static final int OPTIONS_WINDOW_X = 122;
   private static final int OPTIONS_WINDOW_Y = 24;
   private static final int OPTIONS_WINDOW_WIDTH = 108;
   private static final int OPTIONS_WINDOW_HEIGHT = 162;
   private static final int TITLE_X = 67;
   private static final int TITLE_Y = 8;
   private static final int TITLE_WIDTH = 103;
   private static final int TITLE_HEIGHT = 10;
   private static final int POSE_OPTION_WIDTH = 36;
   private static final int POSE_OPTION_HEIGHT = 54;
   private final BlockPos pos;
   private int leftPos;
   private int topPos;
   private final int imageWidth;
   private final int imageHeight;
   private int scrollOffset;
   private boolean isScrollbarHeld;
   private boolean isDraggingPreview;
   private float previewRotationDegrees;
   private List<StatuePoseScreen.PoseOption> poseOptions;
   PlayerStatueBlockEntity previewStatue;
   StatueData statueData;
   Button flipPoseButton;

   public StatuePoseScreen(BlockPos pos, StatueData statueData) {
      super(Component.empty());
      this.pos = pos;
      this.imageWidth = 246;
      this.imageHeight = 196;
      this.statueData = statueData;
      this.setupPreviewStatue();
      this.setupFlipPoseButton();
   }

   public void init() {
      this.leftPos = (this.width - this.imageWidth) / 2;
      this.topPos = (this.height - this.imageHeight) / 2;
      this.poseOptions = new ArrayList<>();
      ArrayList<PlayerStatuePose> poses = new ArrayList<>(List.of(PlayerStatuePose.values()));
      poses.sort(Comparator.comparing(PlayerStatuePose::getSerializedName));
      if (this.statueData != null) {
         int selectedPose = -1;

         for (int i = 0; i < poses.size(); i++) {
            PlayerStatuePose pose = poses.get(i);
            int optionsPerRow = 3;
            int x = this.leftPos + 122 + i % optionsPerRow * 36;
            int y = this.topPos + 24 + i / optionsPerRow * 54;
            this.poseOptions
               .add(new StatuePoseScreen.PoseOption(x, y, i, this.statueData, pose));
            if (this.statueData.pose() == pose) {
               selectedPose = i;
            }
         }

         if (selectedPose >= 0) {
            this.setScrollOffset(Math.min(selectedPose / 3, this.getMaxScroll()));
         } else {
            this.setScrollOffset(this.scrollOffset);
         }

         this.setupFlipPoseButton();
      }
   }

   private void setupFlipPoseButton() {
      this.flipPoseButton = new Button(
         this.leftPos + 8 + 108 - 22, this.topPos + 24 + 162 - 22, 22, 22, Component.empty(),
         button -> this.updatePose(this.statueData.pose(), !this.statueData.flipped()), Supplier::get
      ) {
         static final WidgetSprites SPRITES = new WidgetSprites(
            IronsLib.id("statue_pose_screen/flip_pose"), IronsLib.id("statue_pose_screen/flip_pose"), IronsLib.id("statue_pose_screen/flip_pose_highlighted")
         );

         protected void extractContents(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
            GuiGraphicsHelper.blitSprite(
               guiGraphics, SPRITES.get(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight()
            );
         }
      };
      if (this.statueData == null) {
         this.flipPoseButton.active = false;
         this.flipPoseButton.visible = false;
      }
   }

   public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
      super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
      if (this.previewStatue != null) {
         float scale = 59.4F;
         Quaternionf rotation = new Quaternionf(PREVIEW_ANGLE)
            .rotateAxis(this.previewRotationDegrees * (float) (Math.PI / 180.0), new Vector3f(0.0F, 1.0F, 0.0F));
         GuiGraphicsHelper.renderBlockEntityInInventory(
            guiGraphics,
            this.leftPos + 8,
            this.topPos + 24,
            this.leftPos + 8 + 108,
            this.topPos + 24 + 162,
            scale,
            new Vector3f(0.0F, 2.35F, 0.0F),
            rotation,
            null,
            this.previewStatue
         );
      }

      this.flipPoseButton.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

      for (StatuePoseScreen.PoseOption option : this.poseOptions) {
         if (option.visible) {
            option.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
         }
      }
   }

   public void extractBackground(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
      super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
      GuiGraphicsHelper.blit(guiGraphics, TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
      GuiGraphicsHelper.blitSprite(guiGraphics, IronsLib.id("transmog_table/scroller"), this.getScrollBarX(), this.getScrollBarY(), 6, 27);
      this.renderPlayerName(guiGraphics);
   }

   private void renderPlayerName(@NotNull GuiGraphicsExtractor guiGraphics) {
      Component title;
      if (this.statueData == null) {
         title = Component.translatable("block.irons_lib.player_statue.unknown_player");
      } else {
         title = Component.literal(PatreonData.profileFromUUID(this.statueData.uuid()).username());
      }

      int width = this.font.width(title.getString());
      float scale = Math.clamp(82.4F / width, 0.0F, 1.0F);
      GuiGraphicsHelper.pushPose(guiGraphics);
      GuiGraphicsHelper.translate(guiGraphics, this.leftPos + 67 + 51.5F, this.topPos + 8 + 10);
      if (scale < 1.0F) {
         GuiGraphicsHelper.scale(guiGraphics, scale, scale);
      }

      GuiGraphicsHelper.drawString(guiGraphics, this.font, title, -width / 2, -9, -1);
      GuiGraphicsHelper.popPose(guiGraphics);
   }

   public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
      double mouseX = event.x();
      double mouseY = event.y();
      this.isScrollbarHeld = false;
      this.isDraggingPreview = false;
      if (this.flipPoseButton.mouseClicked(event, doubleClick)) {
         return true;
      }

      for (StatuePoseScreen.PoseOption option : this.poseOptions) {
         if (option.mouseClicked(event, doubleClick)) {
            return true;
         }
      }

      if (mouseX >= this.getScrollBarX() && mouseX < this.getScrollBarX() + 6 && mouseY >= this.getScrollBarY() && mouseY < this.getScrollBarY() + 27) {
         this.isScrollbarHeld = true;
         return true;
      } else if (mouseX >= this.leftPos + 8 && mouseX < this.leftPos + 8 + 108 && mouseY >= this.topPos + 24 && mouseY < this.topPos + 24 + 162) {
         this.isDraggingPreview = true;
         return true;
      } else {
         return super.mouseClicked(event, doubleClick);
      }
   }

   public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
      int maxScroll = this.getMaxScroll();
      int newScroll = Math.clamp(this.scrollOffset - (int)pScrollY, 0, maxScroll);
      if (newScroll != this.scrollOffset) {
         this.setScrollOffset(newScroll);
         return true;
      } else {
         return false;
      }
   }

   public boolean mouseDragged(MouseButtonEvent event, double pDragX, double pDragY) {
      double mouseX = event.x();
      double mouseY = event.y();
      int max = this.getMaxScroll();
      if (this.isScrollbarHeld) {
         int scrollZoneMin = this.topPos + 24;
         int scrollZoneMax = scrollZoneMin + 162;
         float scrollOffs = ((float)mouseY - scrollZoneMin - 7.5F) / (scrollZoneMax - scrollZoneMin - 15.0F);
         scrollOffs = Mth.clamp(scrollOffs, 0.0F, 1.0F);
         int i = Math.max((int)(scrollOffs * max + 0.5), 0);
         if (i != this.scrollOffset) {
            this.setScrollOffset(i);
         }

         return true;
      } else if (this.isDraggingPreview) {
         this.previewRotationDegrees += (float)(pDragX / 108.0 * 180.0);
         return true;
      } else {
         return super.mouseDragged(event, pDragX, pDragY);
      }
   }

   public boolean keyPressed(KeyEvent event) {
      if (super.keyPressed(event)) {
         return true;
      } else if (this.minecraft.options.keyInventory.matches(event)) {
         this.onClose();
         return true;
      } else {
         return false;
      }
   }

   public boolean isPauseScreen() {
      return false;
   }

   private int getMaxScroll() {
      int optionsPerRow = 3;
      int rowsRequired = (int)Math.ceil((double)this.poseOptions.size() / optionsPerRow);
      return Math.max(0, rowsRequired - 3);
   }

   private void setScrollOffset(int scrollOffset) {
      this.scrollOffset = scrollOffset;
      int optionsPerRow = 3;
      int minIndex = scrollOffset * optionsPerRow;
      int optionsPerColumn = 3;
      int maxIndex = minIndex + optionsPerRow * optionsPerColumn;

      for (int i = 0; i < this.poseOptions.size(); i++) {
         StatuePoseScreen.PoseOption option = this.poseOptions.get(i);
         option.setY(option.originalY - scrollOffset * 54);
         if (i >= minIndex && i < maxIndex) {
            option.active = true;
            option.visible = true;
         } else {
            option.active = false;
            option.visible = false;
         }
      }
   }

   private int getScrollBarX() {
      return this.leftPos + 122 + 108 + 2;
   }

   private int getScrollBarY() {
      int maxScroll = this.getMaxScroll();
      float scrollProgress = maxScroll == 0 ? 0.0F : (float)this.scrollOffset / maxScroll;
      return this.topPos + 24 + (int)(scrollProgress * 135.0F);
   }

   private void updatePose(PlayerStatuePose pose, boolean flipped) {
      StatueData current = this.statueData;
      StatueData updated = current.updatePose(pose).updateFlipped(flipped);
      if (!current.equals(updated)) {
         this.statueData = updated;
         this.setupPreviewStatue();
         ClientPlayNetworking.send(new SelectStatuePosePacket(this.pos, this.statueData.pose(), this.statueData.flipped()));
      }
   }

   private void setupPreviewStatue() {
      this.previewStatue = PlayerStatueBlockEntity.renderable(this.statueData);
   }

   class PoseOption extends Button {
      final int index;
      final int originalY;
      final int originalX;
      final PlayerStatueBlockEntity entity;
      final PlayerStatuePose pose;

      PoseOption(int x, int y, int index, StatueData statueData, PlayerStatuePose pose) {
         super(x, y, 36, 54, Component.empty(),
            button -> StatuePoseScreen.this.updatePose(pose, StatuePoseScreen.this.statueData.flipped()), DEFAULT_NARRATION);
         this.setTooltip(
            Tooltip.create(
               Component.translatable(pose.descriptionId()),
               Component.translatable("block.irons_lib.player_statue.pose_guide")
                  .withStyle(new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY})
            )
         );
         this.index = index;
         this.originalY = this.getY();
         this.originalX = this.getX();
         this.entity = PlayerStatueBlockEntity.renderable(new StatueData(statueData.uuid(), pose, false));
         this.pose = pose;
      }

      protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
         boolean hovered = this.isHoveredOrFocused();
         boolean selected = this.pose == StatuePoseScreen.this.statueData.pose();
         Identifier frameSprite = IronsLib.id("transmog_table/transmog_option");
         if (selected) {
            frameSprite = frameSprite.withSuffix("_selected");
         } else if (hovered) {
            frameSprite = frameSprite.withSuffix("_highlighted");
         }

         if (!frameSprite.getPath().endsWith("option")) {
            frameSprite = frameSprite.withPrefix("gui/sprites/");
         }

         GuiGraphicsHelper.blitSprite(guiGraphics, frameSprite, this.getX(), this.getY(), this.getWidth(), this.getHeight());
         float scale = 19.800001F;
         GuiGraphicsHelper.renderBlockEntityInInventory(
            guiGraphics,
            this.getX(),
            this.getY(),
            this.getX() + 36,
            this.getY() + 54,
            scale,
            new Vector3f(0.0F, 2.35F, 0.0F),
            StatuePoseScreen.OPTION_ANGLE,
            null,
            this.entity
         );
      }
   }
}
