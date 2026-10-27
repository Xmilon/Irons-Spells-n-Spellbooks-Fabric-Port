package io.redspace.ironsspellbooks.item;

import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class SpellSlotUpgradeItem extends Item {
    private final int maxSlots;
    private final Component description;

    public SpellSlotUpgradeItem(int maxSlotsToUpgradeTo) {
        super(ItemPropertiesHelper.material().rarity(Rarity.RARE));
        this.maxSlots = maxSlotsToUpgradeTo;
        this.description = Component.translatable("item.irons_spellbooks.spell_slot_upgrade_desc", maxSlotsToUpgradeTo).withStyle(ChatFormatting.GRAY);
    }

    public int maxSlots() {
        return maxSlots;
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
