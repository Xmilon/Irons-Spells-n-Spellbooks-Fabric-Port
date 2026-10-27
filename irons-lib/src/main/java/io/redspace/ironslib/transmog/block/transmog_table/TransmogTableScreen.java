package io.redspace.ironslib.transmog.block.transmog_table;

import com.mojang.authlib.GameProfile;
import io.redspace.ironslib.IronsLib;
import io.redspace.ironslib.internal.mixin.PlayerAccessor;
import io.redspace.ironslib.patreon.PatreonData;
import io.redspace.ironslib.patreon.data.PatreonPermissions;
import io.redspace.ironslib.registry.IronsLibRegistries;
import io.redspace.ironslib.transmog.TransmogHolder;
import io.redspace.ironslib.transmog.TransmogItemData;
import io.redspace.ironslib.transmog.client.ITransmogPreview;
import io.redspace.ironslib.util.GuiGraphicsHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.Equippable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TransmogTableScreen extends AbstractContainerScreen<TransmogTableMenu> {
   public static final GameProfile TRANSMOG_PREVIEW = new GameProfile(UUID.fromString("db3ebb97-ab61-484d-ba69-001dc920a330"), "[Transmog Preview]");
   private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("irons_lib", "textures/gui/transmog_table.png");
   private static final Identifier INDICATOR_SPRITE = Identifier.fromNamespaceAndPath("irons_lib", "transmog_table/place_transmog_indicator");
   private static final int TRANSMOG_WINDOW_X = 118;
   private static final int TRANSMOG_WINDOW_Y = 6;
   private static final int TRANSMOG_WINDOW_WIDTH = 72;
   private static final int TRANSMOG_WINDOW_HEIGHT = 108;
   private static final int TRANSMOG_OPTION_HEIGHT = 36;
   private static final int TRANSMOG_OPTION_WIDTH = 24;
   private static final int PREVIEW_WINDOW_X = 60;
   private static final int PREVIEW_WINDOW_Y = 24;
   private static final int PREVIEW_WINDOW_WIDTH = 52;
   private static final int PREVIEW_WINDOW_HEIGHT = 84;
   private static final Quaternionf ARMOR_STAND_ANGLE = new Quaternionf().rotationXYZ(0.43633232F, 0.0F, (float) Math.PI);
   private List<TransmogTableScreen.TransmogOption> transmogOptions = new ArrayList<>();
   private Button transmogForgeButton;
   private LivingEntity armorStandPreview;
   private LivingEntity playerPreview;
   private int scrollOffset;
   private boolean isScrollbarHeld;
   private boolean shouldShowPlacementHint;
   private int dyeColor = -1;
   @Nullable
   ColorPickerScreen popupScreen;
   protected static final WidgetSprites BUTTON_SPRITES = new WidgetSprites(
      IronsLib.id("gui/sprites/transmog_table/transmog_button_selected"),
      IronsLib.id("gui/sprites/transmog_table/transmog_button_disabled"),
      IronsLib.id("gui/sprites/transmog_table/transmog_button_highlighted")
   );

   public TransmogTableScreen(TransmogTableMenu menu, Inventory playerInventory, Component title) {
      super(menu, playerInventory, title, 204, 200);
      this.initPreviewEntities();
      ((TransmogTableMenu)this.menu).armorSlotsChangedCallback = this::onArmorSlotsChanged;
      ((TransmogTableMenu)this.menu).transmogSelectionChangedCallback = this::onSelectedTransmogChanged;
      this.shouldShowPlacementHint = true;
   }

   protected void init() {
      super.init();
      if (Minecraft.getInstance().player != null) {
         PatreonPermissions permissions = PatreonData.getPatreonPermissions(Minecraft.getInstance().player);
         this.transmogForgeButton = new Button(31 + this.leftPos, this.topPos + 77, 20, 20, Component.empty(), button -> {
               int id = ((TransmogTableMenu)this.menu).packTransmogRequest(this.dyeColor);
               if (((TransmogTableMenu)this.menu).clickMenuButton(Minecraft.getInstance().player, id)) {
                  Minecraft.getInstance().gameMode.handleInventoryButtonClick(((TransmogTableMenu)this.menu).containerId, id);
               }
            }, Supplier::get) {
                  protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
                     GuiGraphicsHelper.blitSprite(
                        guiGraphics,
                        TransmogTableScreen.BUTTON_SPRITES.get(this.active, this.isHoveredOrFocused()),
                        this.getX(),
                        this.getY(),
                        this.getWidth(),
                        this.getHeight()
                     );
                  }

                  public void playDownSound(SoundManager handler) {
                     handler.play(
                        SimpleSoundInstance.forUI(
                           (SoundEvent)IronsLibRegistries.SoundRegistry.TRANSMOG_TABLE_FORGE, (float)(0.9 + Math.random() * 0.25), 0.75F
                        )
                     );
                  }
               };
         this.transmogOptions = new ArrayList<>();

         for (int i = 0; i < ((TransmogTableMenu)this.menu).transmogActions.size(); i++) {
            TransmogTableMenu.TransmogAction action = ((TransmogTableMenu)this.menu).transmogActions.get(i);
            int optionsPerRow = 3;
            int x = this.leftPos + 118 + i % optionsPerRow * 24;
            int y = this.topPos + 6 + i / optionsPerRow * 36;
            this.transmogOptions
               .add(
                  new TransmogTableScreen.TransmogOption(
                     x, y,
                           button -> {
                              if (((TransmogTableMenu)this.menu)
                                 .clickMenuButton(Minecraft.getInstance().player, ((TransmogTableScreen.TransmogOption)button).index)) {
                                 Minecraft.getInstance()
                                    .gameMode
                                    .handleInventoryButtonClick(((TransmogTableMenu)this.menu).containerId, ((TransmogTableScreen.TransmogOption)button).index);
                              }
                           },
                     action,
                     i,
                     action.remove() || permissions.canUse(action.holder())
                  )
               );
         }

         this.onSelectedTransmogChanged();
         this.setScrollOffset(this.scrollOffset);
         if (this.popupScreen != null) {
            this.popupScreen.init();
            this.popupScreen.setPos(this.leftPos - this.popupScreen.imageWidth, this.topPos + 18);
         }
      }
   }

   protected void initPreviewEntities() {
      this.armorStandPreview = new RemotePlayer(Minecraft.getInstance().level, TRANSMOG_PREVIEW);
      this.armorStandPreview.yBodyRot = 210.0F;
      this.armorStandPreview.yHeadRot = this.armorStandPreview.yBodyRot;
      this.armorStandPreview.yHeadRotO = this.armorStandPreview.yBodyRot;
      this.armorStandPreview.setInvisible(true);
      ((ITransmogPreview)this.armorStandPreview).irons_lib$setTransmogPreview(true);
      this.playerPreview = new RemotePlayer(Minecraft.getInstance().level, Minecraft.getInstance().getGameProfile());
      ((ITransmogPreview)this.playerPreview).irons_lib$setTransmogPreview(true);
      EntityDataAccessor<Byte> key = PlayerAccessor.getDataPlayerModeCustomisation();
      this.playerPreview.getEntityData().set(key, (Byte)Minecraft.getInstance().player.getEntityData().get(key));
   }

   public void extractRenderState(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
      super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

      for (TransmogTableScreen.TransmogOption option : this.transmogOptions) {
         if (option.visible) {
            this.resetArmorstandPreview();
            option.setupArmorPreview(Minecraft.getInstance().player, this.armorStandPreview);
            option.renderArmorPreview(guiGraphics, mouseX, mouseY, partialTick, this.armorStandPreview, null);
         }
      }

      this.transmogForgeButton.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
      int x = this.leftPos + 60;
      int y = this.topPos + 24;
      InventoryScreen.extractEntityInInventoryFollowsMouse(guiGraphics, x, y, x + 52, y + 84, 34, 0.1875F, mouseX, mouseY, this.playerPreview);
      if (this.popupScreen != null) {
         this.popupScreen.extractPopupForeground(guiGraphics, mouseX, mouseY, partialTick);
      }

      if (((TransmogTableMenu)this.menu).selectedTransmogIndex >= 0) {
         if (!((TransmogTableMenu)this.menu).transmogSlot.hasItem() && this.shouldShowPlacementHint) {
            GuiGraphicsHelper.pushPose(guiGraphics);
            GuiGraphicsHelper.translate(
               guiGraphics, this.leftPos + ((TransmogTableMenu)this.menu).transmogSlot.x + 8, this.topPos + ((TransmogTableMenu)this.menu).transmogSlot.y + 8
            );
            float f = 1.5F + Mth.sin((this.minecraft.player.tickCount + partialTick) * 0.25F) * 0.35F;
            GuiGraphicsHelper.scale(guiGraphics, f, f);
            GuiGraphicsHelper.blitSprite(guiGraphics, INDICATOR_SPRITE, -8, -8, 16, 16);
            GuiGraphicsHelper.popPose(guiGraphics);
         } else {
            this.shouldShowPlacementHint = false;
         }
      }
   }

   protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int x, int y) {
      for (TransmogTableScreen.TransmogOption button : this.transmogOptions) {
         if (button.isHovered() && button.isActive()) {
            GuiGraphicsHelper.renderTooltip(guiGraphics, this.font, button.getTooltip(Minecraft.getInstance().player), Optional.empty(), x, y);
            return;
         }
      }

      super.extractTooltip(guiGraphics, x, y);
   }

   public void extractBackground(GuiGraphicsExtractor guiHelper, int mouseX, int mouseY, float partialTick) {
      super.extractBackground(guiHelper, mouseX, mouseY, partialTick);
      GuiGraphicsHelper.blit(guiHelper, TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
      GuiGraphicsHelper.blitSprite(guiHelper, IronsLib.id("transmog_table/scroller"), this.getScrollBarX(), this.getScrollBarY(), 6, 27);
      if (this.popupScreen != null) {
         this.popupScreen.extractPopupBackground(guiHelper, mouseX, mouseY, partialTick);
      }
   }

   protected void extractSlot(GuiGraphicsExtractor guiGraphics, Slot slot, int mouseX, int mouseY) {
      super.extractSlot(guiGraphics, slot, mouseX, mouseY);
      if (slot instanceof TransmogArmorSlot armorSlot) {
         ItemStack itemstack = armorSlot.getItem();
         if (itemstack.isEmpty() && slot.isActive()) {
            GuiGraphicsHelper.blitSprite(guiGraphics, armorSlot.getEmptyIcon(), slot.x, slot.y, 16, 16);
         }
      }
   }

   protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
   }

   private void openColorPicker() {
      this.popupScreen = new ColorPickerScreen(this::closeColorPicker, this::pickColor, this.dyeColor);
      this.popupScreen.init();
      this.popupScreen.setPos(this.leftPos - this.popupScreen.imageWidth, this.topPos + 18);
   }

   private void closeColorPicker() {
      this.popupScreen = null;
   }

   private void pickColor(int color) {
      this.dyeColor = color;
      this.setupPlayerPreview();
   }

   private int getScrollBarX() {
      return this.leftPos + 118 + 72 + 2;
   }

   private int getScrollBarY() {
      int maxScroll = this.getMaxScroll();
      float scrollProgress = maxScroll == 0 ? 0.0F : (float)this.scrollOffset / maxScroll;
      return this.topPos + 6 + (int)(scrollProgress * 81.0F);
   }

   public void updateTransmogButtonStatus() {
      this.transmogForgeButton.active = ((TransmogTableMenu)this.menu).transmogSlot.hasItem()
         && ((TransmogTableMenu)this.menu).getSelectedTransmogAction() != null
         && ((TransmogTableMenu)this.menu)
            .getSelectedTransmogAction()
            .canPerform(((TransmogTableMenu)this.menu).transmogSlot.getItem(), PatreonData.getPatreonPermissions(Minecraft.getInstance().player));
   }

   public void onSelectedTransmogChanged() {
      this.dyeColor = -1;
      TransmogTableMenu.TransmogAction transmog = ((TransmogTableMenu)this.menu).getSelectedTransmogAction();
      this.closeColorPicker();
      if (transmog != null && transmog.holder() != null) {
         this.dyeColor = transmog.holder().dyeConfig().defaultColor();
         if (transmog.holder().dyeConfig().dyeable()) {
            ItemStack currentItem = ((TransmogTableMenu)this.menu).transmogSlot.getItem();
            if (!currentItem.isEmpty() && TransmogItemData.has(currentItem)) {
               TransmogItemData currentItemData = TransmogItemData.get(currentItem);
               if (transmog.holder() == currentItemData.transmog()) {
                  this.dyeColor = currentItemData.dyeColor();
               }
            }

            this.openColorPicker();
         }
      }

      this.updateTransmogButtonStatus();
      this.setupPlayerPreview();
   }

   private int getMaxScroll() {
      int optionsPerRow = 3;
      int rowsRequired = (int)Math.ceil((double)this.transmogOptions.size() / optionsPerRow);
      return Math.max(0, rowsRequired - 3);
   }

   private void setScrollOffset(int scrollOffset) {
      this.scrollOffset = scrollOffset;
      int optionsPerRow = 3;
      int minIndex = scrollOffset * optionsPerRow;
      int optionsPerColumn = 3;
      int maxIndex = minIndex + optionsPerRow * optionsPerColumn;

      for (int i = 0; i < this.transmogOptions.size(); i++) {
         TransmogTableScreen.TransmogOption option = this.transmogOptions.get(i);
         option.setY(option.originalY - scrollOffset * 36);
         if (i >= minIndex && i < maxIndex) {
            option.active = true;
            option.visible = true;
         } else {
            option.active = false;
            option.visible = false;
         }
      }
   }

   public void resetArmorstandPreview() {
      this.armorStandPreview.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
      this.armorStandPreview.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
      this.armorStandPreview.setItemSlot(EquipmentSlot.LEGS, ItemStack.EMPTY);
      this.armorStandPreview.setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY);
   }

   public void onArmorSlotsChanged() {
      this.setupPlayerPreview();
      this.updateTransmogButtonStatus();
   }

   public void setupPlayerPreview() {
      Player actualPlayer = Minecraft.getInstance().player;
      this.playerPreview.setItemSlot(EquipmentSlot.HEAD, actualPlayer.getItemBySlot(EquipmentSlot.HEAD));
      this.playerPreview.setItemSlot(EquipmentSlot.CHEST, actualPlayer.getItemBySlot(EquipmentSlot.CHEST));
      this.playerPreview.setItemSlot(EquipmentSlot.LEGS, actualPlayer.getItemBySlot(EquipmentSlot.LEGS));
      this.playerPreview.setItemSlot(EquipmentSlot.FEET, actualPlayer.getItemBySlot(EquipmentSlot.FEET));
      ItemStack transmogContainerItem = ((TransmogTableMenu)this.menu).transmogContainer.getItem(0);
      if (!((TransmogTableMenu)this.menu).transmogContainer.isEmpty() && !transmogContainerItem.isEmpty()) {
         Equippable equippable = (Equippable)transmogContainerItem.get(DataComponents.EQUIPPABLE);
         if (equippable != null && equippable.slot().getType() == Type.HUMANOID_ARMOR) {
            ItemStack transmogPreview = transmogContainerItem.copy();
            TransmogTableMenu.TransmogAction selectedTransmog = ((TransmogTableMenu)this.menu).getSelectedTransmogAction();
            if (selectedTransmog != null) {
               if (selectedTransmog.remove()) {
                  TransmogItemData.remove(transmogPreview);
               } else if (selectedTransmog.holder().supportsSlot(equippable.slot())) {
                  TransmogItemData.set(transmogPreview, new TransmogItemData(selectedTransmog.holder(), this.dyeColor));
               }
            }

            this.playerPreview.setItemSlot(equippable.slot(), transmogPreview);
         }
      }
   }

   public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
      if (this.popupScreen != null && this.popupScreen.mouseClicked(event, doubleClick)) {
         return true;
      }

      if (this.transmogForgeButton.mouseClicked(event, doubleClick)) {
         return true;
      }

      for (TransmogTableScreen.TransmogOption option : this.transmogOptions) {
         if (option.mouseClicked(event, doubleClick)) {
            return true;
         }
      }

      double mouseX = event.x();
      double mouseY = event.y();
      if (mouseX >= this.getScrollBarX() && mouseX < this.getScrollBarX() + 6 && mouseY >= this.getScrollBarY() && mouseY < this.getScrollBarY() + 27) {
         this.isScrollbarHeld = true;
         return true;
      } else {
         this.isScrollbarHeld = false;
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

   public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
      if (this.popupScreen != null && this.popupScreen.mouseDragged(event, dragX, dragY)) {
         return true;
      }

      double mouseY = event.y();
      int max = this.getMaxScroll();
      if (this.isScrollbarHeld) {
         int scrollZoneMin = this.topPos + 6;
         int scrollZoneMax = scrollZoneMin + 108;
         float scrollOffs = ((float)mouseY - scrollZoneMin - 7.5F) / (scrollZoneMax - scrollZoneMin - 15.0F);
         scrollOffs = Mth.clamp(scrollOffs, 0.0F, 1.0F);
         int i = Math.max((int)(scrollOffs * max + 0.5), 0);
         if (i != this.scrollOffset) {
            this.setScrollOffset(i);
         }

         return true;
      } else {
         return super.mouseDragged(event, dragX, dragY);
      }
   }

   public boolean keyPressed(KeyEvent event) {
      return this.popupScreen != null && this.popupScreen.keyPressed(event) ? true : super.keyPressed(event);
   }

   public boolean charTyped(CharacterEvent event) {
      return this.popupScreen != null && this.popupScreen.charTyped(event) ? true : super.charTyped(event);
   }

   class TransmogOption extends Button {
      final TransmogTableMenu.TransmogAction action;
      final int index;
      final boolean unlocked;
      final ItemStack[] previewItems;
      final int originalY;
      final int originalX;

      private static ItemStack createStack(Item item, TransmogHolder holder) {
         ItemStack stack = new ItemStack(item);
         TransmogItemData.set(stack, new TransmogItemData(holder));
         return stack;
      }

      TransmogOption(int x, int y, OnPress onPress, TransmogTableMenu.TransmogAction action, int index, boolean unlocked) {
         super(x, y, 24, 36, Component.empty(), onPress, DEFAULT_NARRATION);
         this.action = action;
         this.index = index;
         this.unlocked = unlocked;
         this.previewItems = this.action.remove()
            ? null
            : new ItemStack[]{
               createStack(Items.IRON_BOOTS, this.action.holder()),
               createStack(Items.IRON_LEGGINGS, this.action.holder()),
               createStack(Items.IRON_CHESTPLATE, this.action.holder()),
               createStack(Items.IRON_HELMET, this.action.holder())
            };
         this.originalY = this.getY();
         this.originalX = this.getX();
      }

      protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
         boolean hovered = this.isHoveredOrFocused();
         boolean selected = ((TransmogTableMenu)TransmogTableScreen.this.menu).selectedTransmogIndex == this.index;
         Identifier frameSprite = IronsLib.id("transmog_table/transmog_option");
         if (selected) {
            frameSprite = frameSprite.withSuffix("_selected");
         } else {
            if (!this.unlocked) {
               frameSprite = frameSprite.withSuffix("_disabled");
            }

            if (hovered) {
               frameSprite = frameSprite.withSuffix("_highlighted");
            }
         }

         if (!frameSprite.getPath().endsWith("option")) {
            frameSprite = frameSprite.withPrefix("gui/sprites/");
         }

         GuiGraphicsHelper.blitSprite(guiGraphics, frameSprite, this.getX(), this.getY(), this.getWidth(), this.getHeight());
      }

      protected void setupArmorPreview(Player player, LivingEntity armorStand) {
         for (EquipmentSlot slot : TransmogTableMenu.HUMANOID_ARMOR_SLOTS) {
            ItemStack previewStack;
            if (this.action.remove()) {
               previewStack = player.getItemBySlot(slot).copy();
               Equippable transmogEquippable = (Equippable)((TransmogTableMenu)TransmogTableScreen.this.menu)
                  .transmogSlot
                  .getItem()
                  .get(DataComponents.EQUIPPABLE);
               if (transmogEquippable != null && transmogEquippable.slot() == slot) {
                  previewStack = ((TransmogTableMenu)TransmogTableScreen.this.menu).transmogSlot.getItem().copy();
               }

               TransmogItemData.remove(previewStack);
            } else {
               previewStack = this.action.holder().supportsSlot(slot) ? this.previewItems[slot.getIndex()] : ItemStack.EMPTY;
            }

            armorStand.setItemSlot(slot, previewStack);
         }
      }

      protected void renderArmorPreview(
         GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick, LivingEntity armorStand, @Nullable EquipmentSlot equipmentSlot
      ) {
         float scale = this.getWidth() / 16.0F * 9.5F;
         this.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
         GuiGraphicsHelper.renderEntityInInventory(
            guiGraphics,
            this.getX(),
            this.getY(),
            this.getX() + this.getWidth(),
            this.getY() + this.getHeight(),
            scale,
            new Vector3f(0.0F, 0.0F, 0.0F),
            TransmogTableScreen.ARMOR_STAND_ANGLE,
            null,
            armorStand
         );
         guiGraphics.nextStratum();
         if (this.action.remove()) {
            GuiGraphicsHelper.blitSpriteColored(
               guiGraphics,
               IronsLib.id("transmog_table/remove_transmog_overlay"),
               this.getX(),
               this.getY(),
               this.getWidth(),
               this.getHeight(),
               1.0F,
               1.0F,
               1.0F,
               0.75F
            );
         } else if (this.action.holder().dyeConfig().dyeable()) {
            GuiGraphicsHelper.blitSprite(
               guiGraphics, IronsLib.id("transmog_table/dyeable"), this.getX() + this.getWidth() - 12, this.getY() + this.getHeight() - 12, 11, 11
            );
         }

         if (!this.unlocked) {
            GuiGraphicsHelper.blitSprite(
               guiGraphics, IronsLib.id("transmog_table/lock"), this.getX() + this.getWidth() / 2 - 5, this.getY() + this.getHeight() / 2 - 7, 10, 14
            );
         }
      }

      public List<Component> getTooltip(LocalPlayer player) {
         List<Component> list = new ArrayList<>();
         if (this.action.remove()) {
            list.add(Component.translatable("tooltip.irons_lib.transmog_option.remove"));
         } else {
            TransmogHolder holder = this.action.holder();
            boolean canUse = PatreonData.getPatreonPermissions(player).canUse(holder);
            list.add(Component.translatable("tooltip.irons_lib.transmog_option.title").withStyle(ChatFormatting.LIGHT_PURPLE));
            list.add(Component.translatable(holder.descriptionId()).withStyle(ChatFormatting.ITALIC));
            list.add(Component.empty());
            list.add(
               Component.translatable(
                     "tooltip.irons_lib.transmog_option.requirement",
                     new Object[]{Component.translatable(holder.requiredPermission().getDescriptionId()).withStyle(ChatFormatting.GOLD)}
                  )
                  .withStyle(canUse ? ChatFormatting.GREEN : ChatFormatting.RED)
            );
            list.add(Component.empty());
            MutableComponent supportedSlotsList;
            if (this.action.holder().supportedSlots() == TransmogHolder.ALL_SLOTS) {
               supportedSlotsList = Component.translatable("tooltip.irons_lib.transmog_option.supported_slots.all");
            } else {
               supportedSlotsList = Component.empty();
               Set<EquipmentSlot> slots = this.action.holder().supportedSlots();
               int s = slots.size();
               int i = 0;

               for (EquipmentSlot slot : slots) {
                  supportedSlotsList.append(Component.translatable(String.format("tooltip.irons_lib.transmog_option.supported_slots.%s", slot.getName())));
                  if (++i < s) {
                     supportedSlotsList.append(", ");
                  }
               }
            }

            list.add(
               Component.translatable("tooltip.irons_lib.transmog_option.supported_slots", new Object[]{supportedSlotsList}).withStyle(ChatFormatting.GRAY)
            );
         }

         return list;
      }
   }
}
