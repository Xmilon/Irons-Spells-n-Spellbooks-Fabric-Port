package io.redspace.ironsspellbooks.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;
import java.util.Map;

public class HandleLitBlocksProcessor implements StructureProcessor {
    public static final MapCodec<HandleLitBlocksProcessor> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            Codec.DOUBLE.fieldOf("chanceLit").forGetter(obj -> obj.chanceLit),
            Codec.unboundedMap(Identifier.CODEC, Codec.DOUBLE).optionalFieldOf("byBlock", Map.of()).forGetter(obj -> obj.byBlock)
    ).apply(builder, HandleLitBlocksProcessor::new));

    public final double chanceLit;
    public final Map<Identifier, Double> byBlock;

    private HandleLitBlocksProcessor(double chanceLit, Map<Identifier, Double> byBlock) {
        this.chanceLit = chanceLit;
        this.byBlock = byBlock;
    }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(@NotNull LevelReader levelReader,
                                                             @NotNull BlockPos jigsawPiecePos,
                                                             @NotNull BlockPos jigsawPieceBottomCenterPos,
                                                             @NotNull BlockPos localPos,
                                                             StructureTemplate.StructureBlockInfo blockInfoGlobal,
                                                             StructurePlaceSettings structurePlacementData) {
        if (blockInfoGlobal.state().hasProperty(BlockStateProperties.LIT)) {
            double chanceToBeLit = byBlock.getOrDefault(BuiltInRegistries.BLOCK.getKey(blockInfoGlobal.state().getBlock()), chanceLit);
            RandomSource random = structurePlacementData.getRandom(blockInfoGlobal.pos());
            blockInfoGlobal = new StructureTemplate.StructureBlockInfo(blockInfoGlobal.pos(), blockInfoGlobal.state().setValue(BlockStateProperties.LIT, random.nextFloat() < chanceToBeLit), blockInfoGlobal.nbt());
        }
        return blockInfoGlobal;
    }

    @Override
    public MapCodec<HandleLitBlocksProcessor> codec() {
        return CODEC;
    }
}
