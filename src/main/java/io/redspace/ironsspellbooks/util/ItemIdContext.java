package io.redspace.ironsspellbooks.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

/**
 * Items must know their registry key when constructed (Item.Properties#setId). Our deferred registration builds item
 * instances inside suppliers, so the registration loop publishes the key being built here and ItemMixin applies it.
 */
public final class ItemIdContext {
    private static final ThreadLocal<ResourceKey<Item>> CURRENT = new ThreadLocal<>();

    private ItemIdContext() {
    }

    public static void set(Identifier id) {
        CURRENT.set(ResourceKey.create(Registries.ITEM, id));
    }

    private static final ThreadLocal<ResourceKey<net.minecraft.world.level.block.Block>> CURRENT_BLOCK = new ThreadLocal<>();

    public static void setBlock(Identifier id) {
        CURRENT_BLOCK.set(ResourceKey.create(Registries.BLOCK, id));
    }

    public static ResourceKey<net.minecraft.world.level.block.Block> currentBlock() {
        return CURRENT_BLOCK.get();
    }

    public static ResourceKey<Item> current() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
        CURRENT_BLOCK.remove();
    }
}
