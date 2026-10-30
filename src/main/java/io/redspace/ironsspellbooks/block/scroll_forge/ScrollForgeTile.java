package io.redspace.ironsspellbooks.block.scroll_forge;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.gui.scroll_forge.ScrollForgeMenu;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class ScrollForgeTile extends BlockEntity implements MenuProvider {
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState newState) {
        if (level instanceof net.minecraft.server.level.ServerLevel) drops();
        super.preRemoveSideEffects(pos, newState);
    }

    private ScrollForgeMenu menu;
    private boolean loadingInventory;

    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            if (loadingInventory) return;
            updateMenuSlots(slot);
            setChanged();
        }
    };

    public ScrollForgeTile(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockRegistry.SCROLL_FORGE_TILE.get(), pWorldPosition, pBlockState);
    }

    private void updateMenuSlots(int slot) {
        if (menu != null) {
            menu.onSlotsChanged(slot);
        }
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public ItemStack getStackInSlot(int slot) {
        return itemHandler.getStackInSlot(slot);
    }

    @Override
    public MutableComponent getDisplayName() {
        return Component.translatable("ui.irons_spellbooks.scroll_forge_title");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        //TODO: only allow one player to access at a time?
        menu = new ScrollForgeMenu(containerId, inventory, this);
        return menu;
    }

    public void setRecipeSpell(String spellId) {
        menu.setRecipeSpell(SpellRegistry.getSpell(spellId));
    }

    public void drops() {
        SimpleContainer simpleContainer
                = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots() - 1; i++) {
            simpleContainer.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, simpleContainer);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        loadingInventory = true;
        try {
            for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
                itemHandler.setStackInSlot(slot, ItemStack.EMPTY);
            }
            for (ValueInput entry : input.childOrEmpty("inventory").childrenListOrEmpty("Items")) {
                int slot = entry.getIntOr("Slot", -1);
                if (slot >= 0 && slot < itemHandler.getSlots()) {
                    entry.read(ItemStack.MAP_CODEC).ifPresent(stack -> itemHandler.setStackInSlot(slot, stack));
                }
            }
        } finally {
            loadingInventory = false;
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ValueOutput inventory = output.child("inventory");
        inventory.putInt("Size", itemHandler.getSlots());
        var items = inventory.childrenList("Items");
        for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                ValueOutput entry = items.addChild();
                entry.putInt("Slot", slot);
                entry.store(ItemStack.MAP_CODEC, stack);
            }
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }
}


