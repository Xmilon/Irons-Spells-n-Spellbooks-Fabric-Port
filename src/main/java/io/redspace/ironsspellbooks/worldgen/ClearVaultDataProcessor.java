package io.redspace.ironsspellbooks.worldgen;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;

public class ClearVaultDataProcessor implements StructureProcessor {
    public static final MapCodec<ClearVaultDataProcessor> CODEC = MapCodec.unit(ClearVaultDataProcessor::new);

    public ClearVaultDataProcessor() {
    }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(@NotNull LevelReader levelReader,
                                                             @NotNull BlockPos jigsawPiecePos,
                                                             @NotNull BlockPos jigsawPieceBottomCenterPos,
                                                             @NotNull BlockPos localPos,
                                                             StructureTemplate.StructureBlockInfo blockInfoGlobal,
                                                             StructurePlaceSettings structurePlacementData) {
        if (blockInfoGlobal.state().getBlock() instanceof VaultBlock) {
            CompoundTag nbt = blockInfoGlobal.nbt();
            if (nbt != null) {
                nbt = nbt.copy();
                nbt.remove("server_data");
                nbt.remove("shared_data");
                blockInfoGlobal = new StructureTemplate.StructureBlockInfo(
                        blockInfoGlobal.pos(),
                        blockInfoGlobal.state(),
                        nbt);
            }
        }
        return blockInfoGlobal;
    }

    @Override
    public MapCodec<ClearVaultDataProcessor> codec() {
        return CODEC;
    }
}
