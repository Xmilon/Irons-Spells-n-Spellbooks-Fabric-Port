package io.redspace.ironsspellbooks.registries;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.worldgen.features.ExposedAirFeature;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.BlockReplacement;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;

public class FeatureRegistry {
    public static void register() {
        Registry.register(BuiltInRegistries.FEATURE_TYPE, IronsSpellbooks.id("exposed_air_feature"), EXPOSED_AIR_FEATURE);
    }

    public static final MapCodec<ExposedAirFeature> EXPOSED_AIR_FEATURE = ExposedAirFeature.CODEC;

    public static final ResourceKey<Feature> MITHRIL_ORE_FEATURE = ResourceKey.create(Registries.FEATURE, IronsSpellbooks.id("ore_mithril_feature"));
    public static final ResourceKey<PlacedFeature> MITHRIL_ORE_PLACEMENT = ResourceKey.create(Registries.PLACED_FEATURE, IronsSpellbooks.id("ore_mithril_placement"));

    public static void bootstrapFeature(BootstrapContext<Feature> context) {
        //Rules for what ore should replace what block type
        RuleTest deepslateTest = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        RuleTest stoneTest = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);

        //Create target with rules and ore blockstates
        List<BlockReplacement> arcaneDebrisList = List.of(
                BlockReplacement.replace(stoneTest, BlockRegistry.MITHRIL_ORE.get().defaultBlockState()),
                BlockReplacement.replace(deepslateTest, BlockRegistry.MITHRIL_ORE_DEEPSLATE.get().defaultBlockState())
        );

        //Register Feature
        context.register(MITHRIL_ORE_FEATURE, new OreFeature(arcaneDebrisList, 3, 1.0f));
    }

    public static void bootstrapPlacedFeature(BootstrapContext<PlacedFeature> context) {
        HolderGetter<Feature> holdergetter = context.lookup(Registries.FEATURE);
        //Get feature
        Holder<Feature> holderArcaneDebris = holdergetter.getOrThrow(MITHRIL_ORE_FEATURE);

        //Create placement. We want the ore to generate from Y = -63 to -38. The ore generates uniformly across this range. 7 is an arbitrary rarity. Biome filter allows us to potentially limit it to certain biomes
        List<PlacementModifier> list = List.of(CountPlacement.of(7), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.absolute(-63), VerticalAnchor.absolute(-38)), BiomeFilter.biome());

        //Register Placement
        PlacementUtils.register(context, MITHRIL_ORE_PLACEMENT, holderArcaneDebris, list);
    }

}



