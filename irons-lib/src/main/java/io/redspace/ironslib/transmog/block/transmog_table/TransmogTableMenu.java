package io.redspace.ironslib.transmog.block.transmog_table;

import io.redspace.ironslib.patreon.PatreonData;
import io.redspace.ironslib.patreon.data.PatreonPermissions;
import io.redspace.ironslib.registry.IronsLibRegistries;
import io.redspace.ironslib.transmog.TransmogHolder;
import io.redspace.ironslib.transmog.TransmogItemData;
import io.redspace.ironslib.transmog.TransmogManager;
import io.redspace.ironslib.util.Utils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TransmogTableMenu extends AbstractContainerMenu {
   public static final EquipmentSlot[] HUMANOID_ARMOR_SLOTS = new EquipmentSlot[]{
      EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
   };
   public final ContainerLevelAccess access;
   public final Container transmogContainer = new SimpleContainer(1);
   public int selectedTransmogIndex = -1;
   public final Slot transmogSlot;
   public List<TransmogTableMenu.TransmogAction> transmogActions = new ArrayList<>();
   public Runnable armorSlotsChangedCallback = () -> {};
   public Runnable transmogSelectionChangedCallback = () -> {};

   public TransmogTableMenu(int containerId, Inventory inv, BlockPos position) {
      this(containerId, inv, ContainerLevelAccess.create(inv.player.level(), position));
   }

   public TransmogTableMenu(int containerId, Inventory inv, ContainerLevelAccess access) {
      super(IronsLibRegistries.MenuRegistry.TRANSMOG_TABLE_MENU, containerId);
      this.access = access;
      this.addPlayerInventory(19, 118, inv);
      this.addPlayerHotbar(19, 176, inv);
      this.addPlayerArmor(6, 26, inv);
      this.transmogSlot = new Slot(this.transmogContainer, 0, 33, 54) {
         public boolean mayPlace(@NotNull ItemStack stack) {
            return Utils.isEquippable(stack, slot -> slot.getType() == Type.HUMANOID_ARMOR);
         }

         public void setChanged() {
            super.setChanged();
            TransmogTableMenu.this.armorSlotsChangedCallback.run();
         }
      };
      this.addSlot(this.transmogSlot);
      this.createTransmogList(inv.player);
   }

   public void setData(int id, int data) {
      super.setData(id, data);
   }

   public int packTransmogRequest(int dyeColor) {
      dyeColor &= 16777215;
      return -dyeColor;
   }

   public int unpackTransmogColorFromRequest(int request) {
      request = -request;
      return request | 0xFF000000;
   }

   public boolean clickMenuButton(Player player, int id) {
      if (id < 0) {
         int dyeColor = this.unpackTransmogColorFromRequest(id);
         TransmogTableMenu.TransmogAction action = this.getSelectedTransmogAction();
         ItemStack transmogStack = this.transmogContainer.getItem(0);
         if (action != null) {
            if (action.remove) {
               TransmogItemData.remove(transmogStack);
               return true;
            }

            if (!transmogStack.isEmpty() && action.canPerform(transmogStack, PatreonData.getPatreonPermissions(player))) {
               TransmogItemData.set(transmogStack, new TransmogItemData(action.holder(), dyeColor));
               return true;
            }
         }

         return false;
      } else {
         if (id >= this.transmogActions.size()) {
            return false;
         }

         this.setSelectedTransmogIndex(id);
         return true;
      }
   }

   private void setSelectedTransmogIndex(int id) {
      this.selectedTransmogIndex = id;
      this.transmogSelectionChangedCallback.run();
   }

   private void createTransmogList(Player player) {
      List<TransmogHolder> accessibleTransmogs = new ArrayList<>();
      List<TransmogHolder> lockedTransmogs = new ArrayList<>();
      PatreonPermissions permissions = PatreonData.getPatreonPermissions(player);

      for (TransmogHolder holder : TransmogManager.getAllTransmogs()) {
         if (permissions.canUse(holder)) {
            accessibleTransmogs.add(holder);
         } else {
            lockedTransmogs.add(holder);
         }
      }

      accessibleTransmogs.sort(Comparator.comparing(holderx -> Component.translatable(holderx.descriptionId()).getString()));
      lockedTransmogs.sort(Comparator.comparing(holderx -> Component.translatable(holderx.descriptionId()).getString()));
      accessibleTransmogs.sort(Comparator.comparing(TransmogHolder::requiredPermission).reversed());
      lockedTransmogs.sort(Comparator.comparing(TransmogHolder::requiredPermission).reversed());
      this.transmogActions.add(new TransmogTableMenu.TransmogAction(true, null));

      for (TransmogHolder holder : accessibleTransmogs) {
         this.transmogActions.add(new TransmogTableMenu.TransmogAction(holder));
      }

      for (TransmogHolder holder : lockedTransmogs) {
         this.transmogActions.add(new TransmogTableMenu.TransmogAction(holder));
      }
   }

   private void addPlayerInventory(int x, int y, Inventory playerInventory) {
      for (int i = 0; i < 3; i++) {
         for (int l = 0; l < 9; l++) {
            this.addSlot(new Slot(playerInventory, l + i * 9 + 9, x + l * 18, y + i * 18));
         }
      }
   }

   private void addPlayerHotbar(int x, int y, Inventory playerInventory) {
      for (int i = 0; i < 9; i++) {
         this.addSlot(new Slot(playerInventory, i, x + i * 18, y));
      }
   }

   private void addPlayerArmor(int x, int y, Inventory playerInventory) {
      for (int i = 0; i < 4; i++) {
         EquipmentSlot equipmentslot = HUMANOID_ARMOR_SLOTS[i];
         this.addSlot(new TransmogArmorSlot(playerInventory, playerInventory.player, equipmentslot, 39 - i, x, y + i * 18) {
            public void setChanged() {
               super.setChanged();
               TransmogTableMenu.this.armorSlotsChangedCallback.run();
            }
         });
      }
   }

   public ItemStack quickMoveStack(Player playerIn, int index) {
      Slot sourceSlot = (Slot)this.slots.get(index);
      if (sourceSlot != null && sourceSlot.hasItem()) {
         ItemStack sourceStack = sourceSlot.getItem();
         ItemStack copyOfSourceStack = sourceStack.copy();
         boolean fromHotbar = index < 9;
         boolean fromInventory = index < 36;
         boolean fromArmor = !fromInventory && index < 40;
         boolean fromTransmogSlot = !fromInventory && !fromArmor;
         int transmogSlot = 40;
         if (fromArmor) {
            if (!this.moveItemStackTo(sourceStack, transmogSlot, transmogSlot + 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (fromInventory) {
            if (!this.moveItemStackTo(sourceStack, transmogSlot, transmogSlot + 1, false) && !this.moveItemStackTo(sourceStack, 36, 40, false)) {
               return ItemStack.EMPTY;
            }
         } else {
            if (!fromTransmogSlot) {
               return ItemStack.EMPTY;
            }

            if (!this.moveItemStackTo(sourceStack, 36, 40, false) && !this.moveItemStackTo(sourceStack, 0, 36, false)) {
               return ItemStack.EMPTY;
            }
         }

         if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
         } else {
            sourceSlot.setChanged();
         }

         sourceSlot.onTake(playerIn, sourceStack);
         return copyOfSourceStack;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public boolean stillValid(Player player) {
      return (Boolean)this.access
         .evaluate(
            (level, blockPos) -> player.distanceToSqr(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5) <= 64.0
               && level.getBlockState(blockPos).is((Block)IronsLibRegistries.BlockRegistry.TRANSMOG_TABLE),
            true
         );
   }

   public void removed(@NotNull Player player) {
      if (player instanceof ServerPlayer) {
         super.removed(player);
         this.access
            .execute(
               (p_39796_, p_39797_) -> {
                  ItemStack storedItem = this.transmogContainer.getItem(0);
                  if (!storedItem.isEmpty()
                     && Utils.isEquippable(
                        storedItem, slot -> slot.getType() == Type.HUMANOID_ARMOR && player.getItemBySlot(slot).isEmpty()
                           && storedItem.get(DataComponents.EQUIPPABLE).canBeEquippedBy(player.getType().builtInRegistryHolder())
                     )) {
                     player.setItemSlot(((Equippable)storedItem.get(DataComponents.EQUIPPABLE)).slot(), this.transmogContainer.removeItemNoUpdate(0));
                  } else {
                     this.clearContainer(player, this.transmogContainer);
                  }
               }
            );
      }
   }

   @Nullable
   public TransmogTableMenu.TransmogAction getSelectedTransmogAction() {
      return this.selectedTransmogIndex >= 0 && this.selectedTransmogIndex < this.transmogActions.size()
         ? this.transmogActions.get(this.selectedTransmogIndex)
         : null;
   }

   public record TransmogAction(boolean remove, TransmogHolder holder) {
      public TransmogAction(TransmogHolder holder) {
         this(false, holder);
      }

      public boolean canPerform(ItemStack itemStack, PatreonPermissions permissions) {
         return this.remove || permissions.canUse(this.holder) && this.holder.supportsSlot(itemStack);
      }
   }
}
