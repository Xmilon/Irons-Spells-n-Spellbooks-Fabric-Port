package io.redspace.ironsspellbooks.worldgen.features;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

import java.util.function.Predicate;

public record ExposedAirFeature(StructureFeatureConfiguration configuration) implements Feature {
    public static final MapCodec<ExposedAirFeature> CODEC = StructureFeatureConfiguration.CODEC
            .xmap(ExposedAirFeature::new, ExposedAirFeature::configuration).fieldOf("config");
    private static final BlockState AIR = Blocks.CAVE_AIR.defaultBlockState();

    @Override
    public MapCodec<ExposedAirFeature> codec() {
        return CODEC;
    }

    /**
     * Places the given feature at the given location.
     * During world generation, features are provided with a 3x3 region of chunks, centered on the chunk being generated, that they can safely generate into.
     *
     * @param context A context object with a reference to the level and the position
     *                the feature is being placed at
     */
    @Override
    public boolean place(WorldGenLevel level, net.minecraft.world.level.chunk.ChunkGenerator generator,
                         net.minecraft.util.RandomSource random, BlockPos origin) {
        // Blocks that cannot be replaced by the feature (e.g., water, bedrock, etc.)
        var xsize = configuration.xsize();
        var ysize = configuration.ysize();
        var zsize = configuration.zsize();
        int minX = -xsize / 2;
        int maxX = xsize / 2;

        int minY = -1; // assume structure includes floor block, embed 1 into ground
        int maxY = ysize - 1;

        int minZ = -zsize / 2;
        int maxZ = zsize / 2;

        int sideOpenings = 0; // Counts how many air openings exist on the floor level

        // Check if the area is suitable for a dungeon
        for (int dx = minX; dx <= maxX; dx++) {
            for (int dy = minY; dy <= maxY; dy++) {
                for (int dz = minZ; dz <= maxZ; dz++) {
                    BlockPos currentPos = origin.offset(dx, dy, dz);
                    boolean isSolid = level.getBlockState(currentPos).isSolid();

                    // Floor must be solid
                    if (dy == minY && !isSolid) return false;
                    // Ceiling must be empty
                    if (dy == maxY && isSolid) return false;
                    // Count number of air openings on the floor edges
                    if ((dx == minX || dx == maxX || dz == minZ || dz == maxZ)
                            && dy == 0
                            && level.isEmptyBlock(currentPos)
                            && level.isEmptyBlock(currentPos.above())) {
                        sideOpenings++;
                    }
                }
            }
        }

        // Require between 1 and 5 side openings for the dungeon to generate
        int perimeter = xsize * 2 + zsize * 2;
        if (sideOpenings < perimeter * .25 /*|| sideOpenings > 5*/) return false;

        var structureTemplateManager = level.getServer().getStructureTemplateManager();
        var structureTemplate = structureTemplateManager.getOrCreate(configuration.structureTemplateLocation());
        var placementSettings = (new StructurePlaceSettings()).setMirror(Mirror.NONE).setRotation(Rotation.NONE);
        var configuredOffset = configuration.offset();
        var structurePos = origin.offset(minX, -1, minZ).offset(configuredOffset);
        structureTemplate.placeInWorld(level, structurePos, structurePos, placementSettings, level.getRandom(), 2);

        return true;
    }
}


