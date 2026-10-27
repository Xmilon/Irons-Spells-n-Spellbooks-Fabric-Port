package io.redspace.ironsspellbooks.item;

import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ArcaneSalvageItem extends Item {
    private static final Component description = Component.translatable("item.irons_spellbooks.arcane_salvage_desc").withStyle(ChatFormatting.GRAY);

    public ArcaneSalvageItem() {
        super(ItemPropertiesHelper.material());
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltipAdder, TooltipFlag pIsAdvanced) {
        List<Component> lines = new java.util.ArrayList<>();
        appendLegacyTooltip(pStack, context, lines, pIsAdvanced);
        lines.forEach(tooltipAdder);
    }

    private void appendLegacyTooltip(ItemStack pStack, Item.TooltipContext context, List<Component> lines, TooltipFlag pIsAdvanced) {
        lines.add(description);
    }
}
