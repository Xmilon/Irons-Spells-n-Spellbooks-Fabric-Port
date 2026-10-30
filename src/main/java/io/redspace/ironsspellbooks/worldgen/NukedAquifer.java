package io.redspace.ironsspellbooks.worldgen;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

/**
 * Removes water and lava from the immediate surroundings of structures that must stay dry, by filtering the vanilla aquifer.
 */
public record NukedAquifer(Aquifer delegate, IExtendedNoiseChunk.AquifierNuke nuke, BlockState defaultBlockState) implements Aquifer {
    @Override
    public BlockState computeSubstance(int x, int y, int z, double density) {
        var state = delegate.computeSubstance(x, y, z, density);
        if (state == null || !(state.is(Blocks.WATER) || state.is(Blocks.LAVA))) {
            return state;
        }
        for (BoundingBox box : nuke.boundingBoxes()) {
            //todo: it is likely most bb's aren't even in the chunk. can do prelimiary checks
            int dx = 0;
            if (x < box.minX()) {
                dx = box.minX() - x;
            } else if (x > box.maxX()) {
                dx = x - box.maxX();
            }

            int dy = 0;
            if (y < box.minY()) {
                dy = box.minY() - y;
            } else if (y > box.maxY()) {
                dy = y - box.maxY();
            }

            int dz = 0;
            if (z < box.minZ()) {
                dz = box.minZ() - z;
            } else if (z > box.maxZ()) {
                dz = z - box.maxZ();
            }

            int manhattanDistance = dx + dy + dz;
            if (manhattanDistance <= 5) {
                return manhattanDistance <= 3 ? Blocks.CAVE_AIR.defaultBlockState() : defaultBlockState;
            }
        }
        return state;
    }

    @Override
    public boolean shouldScheduleFluidUpdate() {
        return delegate.shouldScheduleFluidUpdate();
    }
}
