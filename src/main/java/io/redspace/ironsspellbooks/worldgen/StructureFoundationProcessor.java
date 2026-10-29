package io.redspace.ironsspellbooks.worldgen;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;

public class StructureFoundationProcessor implements StructureProcessor {
    public static final MapCodec<StructureFoundationProcessor> CODEC = BlockState.CODEC.fieldOf("block").xmap(StructureFoundationProcessor::new, proc -> proc.block);

    public final BlockState block;

    private StructureFoundationProcessor(BlockState block) {
        this.block = block;
    }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(@NotNull LevelReader levelReader,
                                                             @NotNull BlockPos jigsawPiecePos,
                                                             @NotNull BlockPos jigsawPieceBottomCenterPos,
                                                             @NotNull BlockPos localPos,
                                                             StructureTemplate.StructureBlockInfo blockInfoGlobal,
                                                             StructurePlaceSettings structurePlacementData) {
        // check if the current block is at local y = 0 (meaning we are at the min height of the structure piece)
        // and it is not air (and thus we wouldnt need foundation here)
        // and some other check from yungnickyung
        if (localPos.getY() == 0 && !blockInfoGlobal.state().is(Blocks.AIR)
                && !(levelReader instanceof WorldGenRegion worldGenRegion && !worldGenRegion.getCenter().equals(ChunkPos.containing(blockInfoGlobal.pos())))
        ) {
            BlockPos.MutableBlockPos mutable = blockInfoGlobal.pos().mutable().move(Direction.DOWN);
            BlockState currentState = levelReader.getBlockState(mutable);

            // while we have not traversed outside the bounds of the world and we have not yet hit solid material, create foundation block
            while (mutable.getY() > levelReader.getMinY()
                    && mutable.getY() <= levelReader.getMaxY()
                    && (currentState.isAir() || !levelReader.getFluidState(mutable).isEmpty())) {
                levelReader.getChunk(mutable).setBlockState(mutable, block, 0);
                // iterate downward
                mutable.move(Direction.DOWN);
                currentState = levelReader.getBlockState(mutable);
            }
        }
        return blockInfoGlobal;
    }

    @Override
    public MapCodec<StructureFoundationProcessor> codec() {
        return CODEC;
    }
}
