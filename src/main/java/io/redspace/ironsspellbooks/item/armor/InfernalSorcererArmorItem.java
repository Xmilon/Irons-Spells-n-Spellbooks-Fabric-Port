package io.redspace.ironsspellbooks.item.armor;

import net.minecraft.world.item.Item;

import net.minecraft.world.item.equipment.ArmorMaterial;

import net.minecraft.world.item.equipment.ArmorType;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.effect.ImmolateEffect;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.registries.ArmorMaterialRegistry;
import io.redspace.ironsspellbooks.util.MinecraftInstanceHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.geckolib.renderer.GeoArmorRenderer;

import java.util.List;

public class InfernalSorcererArmorItem extends ImbuableChestplateArmorItem {
    public static final int COOLDOWN_TICKS = 1 * 20;

    public InfernalSorcererArmorItem(ArmorType type, Properties settings) {
        super(ArmorMaterialRegistry.INFERNAL_SORCERER, type, settings,
                new AttributeContainer(AttributeRegistry.MAX_MANA, 150, AttributeModifier.Operation.ADD_VALUE),
                new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.10, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
        );
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
        List<Component> tooltipComponents = new java.util.ArrayList<>();
        appendLegacyTooltip(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.forEach(tooltipAdder);
    }

    private void appendLegacyTooltip(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(
                Component.translatable(
                        "tooltip.irons_spellbooks.passive_ability_no_cooldown",
                        Component.literal(Utils.timeFromTicks(Utils.applyCooldownReduction(COOLDOWN_TICKS, MinecraftInstanceHelper.getPlayer()), 1)).withStyle(ChatFormatting.AQUA)
                ).withStyle(ChatFormatting.DARK_PURPLE)
        );
        tooltipComponents.add(Component.literal(" ").append(Component.translatable(this.getDescriptionId() + ".desc")).withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltipComponents.add(Component.literal(" ").append(Component.translatable(this.getDescriptionId() + ".immolate.desc",
                Component.literal(Utils.stringTruncation(ImmolateEffect.damageFor(MinecraftInstanceHelper.getPlayer()), 1)).withStyle(ChatFormatting.RED))
        ).withStyle(ChatFormatting.LIGHT_PURPLE));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public GeoArmorRenderer<?, ?> supplyRenderer() {
        return ArmorClientRenderers.create(this);
    }

}
