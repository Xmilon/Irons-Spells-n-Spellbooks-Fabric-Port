package io.redspace.ironslib.statue.block.statue_block.player;

import io.redspace.ironslib.patreon.PatreonData;
import io.redspace.ironslib.patreon.data.ProfileResult;
import io.redspace.ironslib.registry.IronsLibRegistries;
import io.redspace.ironslib.statue.StatueData;
import io.redspace.ironslib.util.TooltipUtils;
import java.util.ArrayList;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class PlayerStatueBlockItem extends BlockItem {
   ArrayList<Component> shiftTooltip = new ArrayList<>();

   public PlayerStatueBlockItem(Properties properties) {
      super((Block)IronsLibRegistries.BlockRegistry.PLAYER_STATUE_BLOCK, properties.useBlockDescriptionPrefix());
      this.shiftTooltip
         .add(
            Component.literal(" * ")
               .withStyle(ChatFormatting.DARK_GRAY)
               .append(Component.translatable("block.irons_lib.player_statue.guide.1").withStyle(ChatFormatting.WHITE))
         );
      this.shiftTooltip
         .add(
            Component.literal(" * ")
               .withStyle(ChatFormatting.DARK_GRAY)
               .append(Component.translatable("block.irons_lib.player_statue.guide.2").withStyle(ChatFormatting.WHITE))
         );
   }

   public void appendHoverText(
      @NotNull ItemStack stack, @NotNull TooltipContext context, TooltipDisplay display, @NotNull Consumer<Component> builder, @NotNull TooltipFlag tooltipFlag
   ) {
      super.appendHoverText(stack, context, display, builder, tooltipFlag);
      MutableComponent playername = Component.translatable("block.irons_lib.player_statue.unset_player");
      if (StatueData.has(stack)) {
         StatueData data = StatueData.get(stack);
         ProfileResult profile = PatreonData.profileFromUUID(data.uuid());
         if (profile == ProfileResult.NULL) {
            playername = Component.translatable("block.irons_lib.player_statue.unknown_player");
         } else {
            playername = Component.literal(profile.username());
         }
      }

      builder.accept(playername.withStyle(ChatFormatting.YELLOW));
      TooltipUtils.addShiftTooltip(builder, this.shiftTooltip);
   }
}
