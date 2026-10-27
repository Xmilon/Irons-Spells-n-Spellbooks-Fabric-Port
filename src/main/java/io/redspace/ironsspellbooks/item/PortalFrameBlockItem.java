package io.redspace.ironsspellbooks.item;

import net.minecraft.world.item.Item;

import io.redspace.ironsspellbooks.registries.BlockRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

import java.util.List;

public class PortalFrameBlockItem extends BlockItem {
    private static final Component DESCRIPTION = Component.translatable("block.irons_spellbooks.portal_frame.desc").withStyle(ChatFormatting.GRAY);

    public PortalFrameBlockItem() {
        super(BlockRegistry.PORTAL_FRAME.get(), new Item.Properties().fireResistant().rarity(Rarity.RARE));
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltipAdder, TooltipFlag pTooltipFlag) {
        List<Component> pTooltipComponents = new java.util.ArrayList<>();
        appendLegacyTooltip(pStack, pContext, pTooltipComponents, pTooltipFlag);
        pTooltipComponents.forEach(tooltipAdder);
    }

    private void appendLegacyTooltip(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        pTooltipComponents.add(DESCRIPTION);
    }
}
