package io.redspace.ironsspellbooks.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import javax.annotation.Nonnull;

public class WeatherCopperProcessor implements StructureProcessor {

    public static final MapCodec<WeatherCopperProcessor> CODEC = Codec.FLOAT.fieldOf("bias").xmap(WeatherCopperProcessor::new, obj -> obj.bias);
    float bias;

    public WeatherCopperProcessor(float bias) {
        this.bias = bias;
    }

    @Override
    public StructureTemplate.StructureBlockInfo processBlock(@Nonnull LevelReader level, @Nonnull BlockPos jigsawPiecePos, @Nonnull BlockPos jigsawPieceBottomCenterPos, @Nonnull BlockPos localPos, @Nonnull StructureTemplate.StructureBlockInfo blockInfoGlobal, @Nonnull StructurePlaceSettings settings) {
        if (blockInfoGlobal.state().getBlock() instanceof WeatheringCopper copperBlock) {
            float f = Mth.lerp(settings.getRandom(blockInfoGlobal.pos()).nextFloat(), bias, 1);
            int weatherStage = (int) (f * 4);
            BlockState state = blockInfoGlobal.state();
            for (int i = 0; i < weatherStage; i++) {
                var nextState = copperBlock.getNext(state);
                if (nextState.isPresent()) {
                    state = nextState.get().getBlock().withPropertiesOf(blockInfoGlobal.state());
                }
            }

            return new StructureTemplate.StructureBlockInfo(blockInfoGlobal.pos(), state, blockInfoGlobal.nbt());
        }

        return blockInfoGlobal;
    }

    @Nonnull
    @Override
    public MapCodec<WeatherCopperProcessor> codec() {
        return CODEC;
    }
}
