package io.redspace.ironsspellbooks.worldgen;

import com.mojang.serialization.MapCodec;
import io.redspace.ironsspellbooks.block.portal_frame.PortalFrameBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;

public class ClearPortalFrameDataProcessor implements StructureProcessor {
    public static final MapCodec<ClearPortalFrameDataProcessor> CODEC = MapCodec.unit(ClearPortalFrameDataProcessor::new);

    public ClearPortalFrameDataProcessor() {
    }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(@NotNull LevelReader levelReader,
                                                             @NotNull BlockPos jigsawPiecePos,
                                                             @NotNull BlockPos jigsawPieceBottomCenterPos,
                                                             @NotNull BlockPos localPos,
                                                             StructureTemplate.StructureBlockInfo blockInfoGlobal,
                                                             StructurePlaceSettings structurePlacementData) {
        if (blockInfoGlobal.state().getBlock() instanceof PortalFrameBlock) {
            blockInfoGlobal = new StructureTemplate.StructureBlockInfo(
                    blockInfoGlobal.pos(),
                    blockInfoGlobal.state(),
                    null);
        }
        return blockInfoGlobal;
    }

    @Override
    public MapCodec<ClearPortalFrameDataProcessor> codec() {
        return CODEC;
    }
}
