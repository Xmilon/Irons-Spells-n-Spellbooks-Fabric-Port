package io.redspace.ironslib.statue.worldgen;

import com.mojang.serialization.MapCodec;
import io.redspace.ironslib.patreon.PatreonData;
import io.redspace.ironslib.patreon.data.ProfileResult;
import io.redspace.ironslib.registry.IronsLibRegistries;
import io.redspace.ironslib.statue.PlayerStatuePose;
import io.redspace.ironslib.statue.StatueData;
import io.redspace.ironslib.statue.block.statue_block.AbstractStatueBlock;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.jetbrains.annotations.NotNull;

public class RandomizePlayerStatueProcessor implements StructureProcessor {
   public static final MapCodec<RandomizePlayerStatueProcessor> MAP_CODEC = MapCodec.unit(RandomizePlayerStatueProcessor::new);

   @Nullable
   public StructureBlockInfo processBlock(
      @NotNull LevelReader level,
      @NotNull BlockPos blockPos,
      @NotNull BlockPos pivot,
      @NotNull BlockPos placement,
      @NotNull StructureBlockInfo relativeBlockInfo,
      @NotNull StructurePlaceSettings settings
   ) {
      BlockState state = relativeBlockInfo.state();
      if (!state.is((Block)IronsLibRegistries.BlockRegistry.PLAYER_STATUE_BLOCK)) {
         return relativeBlockInfo;
      }

      boolean isPrimary = (Integer)state.getValue(AbstractStatueBlock.X_POS) == 0
         && (Integer)state.getValue(AbstractStatueBlock.Y_POS) == 0
         && (Integer)state.getValue(AbstractStatueBlock.Z_POS) == 0;
      if (!isPrimary) {
         return relativeBlockInfo;
      }

      List<ProfileResult> profiles = PatreonData.getInstance()
         .getProfileValues()
         .stream()
         .filter(profileResult -> profileResult.permissions().supportsStatues())
         .toList();
      if (profiles.isEmpty()) {
         return relativeBlockInfo;
      }

      ProfileResult profile = profiles.get((int)(Math.random() * profiles.size()));
      PlayerStatuePose pose = PlayerStatuePose.values()[(int)(Math.random() * PlayerStatuePose.values().length)];
      StatueData statueData = new StatueData(profile.uuid(), pose, false);
      CompoundTag tag = relativeBlockInfo.nbt() == null ? new CompoundTag() : relativeBlockInfo.nbt().copy();
      StatueData.CODEC
         .encodeStart(NbtOps.INSTANCE, statueData)
         .result()
         .filter(Tag.class::isInstance)
         .map(Tag.class::cast)
         .ifPresent(encoded -> tag.put("statueData", encoded));
      return new StructureBlockInfo(relativeBlockInfo.pos(), relativeBlockInfo.state(), tag);
   }

   @NotNull
   public MapCodec<? extends StructureProcessor> codec() {
      return MAP_CODEC;
   }
}
