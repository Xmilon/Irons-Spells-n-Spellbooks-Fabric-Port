package io.redspace.ironsspellbooks.block.pedestal;

import io.redspace.ironsspellbooks.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class PedestalTile extends BlockEntity {
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState newState) {
        if (level instanceof net.minecraft.server.level.ServerLevel) drops();
        super.preRemoveSideEffects(pos, newState);
    }

    private static final String NBT_HELD_ITEM = "heldItem";

    private ItemStack heldItem = ItemStack.EMPTY;

    public PedestalTile(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockRegistry.PEDESTAL_TILE.get(), pWorldPosition, pBlockState);
    }

    public ItemStack getHeldItem() {
        return heldItem;
    }

    public void setHeldItem(ItemStack newItem) {
        heldItem = newItem;
        setChanged();
    }

    public void drops() {
        SimpleContainer simpleContainer = new SimpleContainer(heldItem);
        Containers.dropContents(this.level, this.worldPosition, simpleContainer);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        heldItem = input.read(NBT_HELD_ITEM, ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store(NBT_HELD_ITEM, ItemStack.OPTIONAL_CODEC, heldItem);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

}

