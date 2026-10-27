package net.neoforged.neoforge.common;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluid;

public class Tags {
    public static class Fluids {
        public static final TagKey<Fluid> WATER = TagKey.create(net.minecraft.core.registries.Registries.FLUID, net.minecraft.resources.Identifier.withDefaultNamespace("water"));
    }

    public static class Biomes {
        public static final TagKey<Biome> NO_DEFAULT_MONSTERS = TagKey.create(net.minecraft.core.registries.Registries.BIOME, net.minecraft.resources.Identifier.withDefaultNamespace("does_not_exist"));
    }

    public static class Items {
        public static final TagKey<Item> INGOTS_IRON = TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.withDefaultNamespace("iron_ingots"));
        public static final TagKey<Item> LEATHERS = TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.withDefaultNamespace("leathers"));
        public static final TagKey<Item> INGOTS_NETHERITE = TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.withDefaultNamespace("netherite_ingots"));
        public static final TagKey<Item> INGOTS_GOLD = TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.withDefaultNamespace("gold_ingots"));
    }
}


