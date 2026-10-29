package io.redspace.ironsspellbooks.item.curios;

import net.minecraft.world.item.Item;

import io.redspace.ironsspellbooks.api.item.curios.AffinityData;

import net.minecraft.ChatFormatting;


import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;


import java.util.List;

public class AffinityRing extends CurioBaseItem {

    public AffinityRing(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltipAdder, TooltipFlag pIsAdvanced) {
        List<Component> tooltip = new java.util.ArrayList<>();
        appendLegacyTooltip(pStack, context, tooltip, pIsAdvanced);
        tooltip.forEach(tooltipAdder);
    }

    private void appendLegacyTooltip(ItemStack pStack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag pIsAdvanced) {
        var affinity = AffinityData.getAffinityData(pStack);
        if (affinity != AffinityData.NONE && !affinity.affinityData().isEmpty()) {
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("curios.modifiers.ring").withStyle(ChatFormatting.GOLD));
            tooltip.addAll(affinity.getDescriptionComponent());
        } else {
            tooltip.add(Component.translatable("tooltip.irons_spellbooks.empty_affinity_ring").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
    }

    @Override
    public Component getName(ItemStack pStack) {
        return Component.translatable(this.getDescriptionId(), AffinityData.getAffinityData(pStack).getNameForItem());
    }

}
