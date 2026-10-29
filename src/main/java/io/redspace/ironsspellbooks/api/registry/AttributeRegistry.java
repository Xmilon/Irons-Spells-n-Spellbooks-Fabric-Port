package io.redspace.ironsspellbooks.api.registry;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.attribute.MagicPercentAttribute;
import io.redspace.ironsspellbooks.api.attribute.MagicRangedAttribute;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.compat.TrinketsSlots;
import io.redspace.ironsspellbooks.compat.trinkets.ITrinketItem;
import io.redspace.ironsspellbooks.compat.trinkets.TrinketSlotContext;
import io.redspace.ironsspellbooks.compat.trinkets.TrinketSlotResult;
import io.redspace.ironsspellbooks.compat.trinkets.TrinketsApi;
import io.redspace.ironsspellbooks.item.SpellBook;
import java.util.List;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class AttributeRegistry {
    public static final Holder.Reference<Attribute> MAX_MANA = register("max_mana", new MagicRangedAttribute("attribute.irons_spellbooks.max_mana", 100.0D, 0.0D, 1000000.0D).setSyncable(true));
    public static final Holder.Reference<Attribute> MANA_REGEN = register("mana_regen", new MagicPercentAttribute("attribute.irons_spellbooks.mana_regen", 1.0D, 0.0D, 100.0D).setSyncable(true));
    public static final Holder.Reference<Attribute> COOLDOWN_REDUCTION = register("cooldown_reduction", new MagicPercentAttribute("attribute.irons_spellbooks.cooldown_reduction", 1.0D, -100.0D, 100.0D).setSyncable(true));
    public static final Holder.Reference<Attribute> SPELL_POWER = register("spell_power", new MagicPercentAttribute("attribute.irons_spellbooks.spell_power", 1.0D, -100, 100.0D).setSyncable(true));
    public static final Holder.Reference<Attribute> SPELL_RESIST = register("spell_resist", new MagicPercentAttribute("attribute.irons_spellbooks.spell_resist", 1.0D, -100, 100.0D).setSyncable(true));
    public static final Holder.Reference<Attribute> CAST_TIME_REDUCTION = register("cast_time_reduction", new MagicPercentAttribute("attribute.irons_spellbooks.cast_time_reduction", 1.0D, -100, 100.0D).setSyncable(true));
    public static final Holder.Reference<Attribute> SUMMON_DAMAGE = register("summon_damage", new MagicPercentAttribute("attribute.irons_spellbooks.summon_damage", 1.0D, -100, 100.0D).setSyncable(true));
    public static final Holder.Reference<Attribute> CASTING_MOVESPEED = register("casting_movespeed", new MagicPercentAttribute("attribute.irons_spellbooks.casting_movespeed", 1, 0, 100.0D).setSyncable(true));
    public static final Holder.Reference<Attribute> FIRE_MAGIC_RESIST = newResistanceAttribute("fire");
    public static final Holder.Reference<Attribute> ICE_MAGIC_RESIST = newResistanceAttribute("ice");
    public static final Holder.Reference<Attribute> LIGHTNING_MAGIC_RESIST = newResistanceAttribute("lightning");
    public static final Holder.Reference<Attribute> HOLY_MAGIC_RESIST = newResistanceAttribute("holy");
    public static final Holder.Reference<Attribute> ENDER_MAGIC_RESIST = newResistanceAttribute("ender");
    public static final Holder.Reference<Attribute> BLOOD_MAGIC_RESIST = newResistanceAttribute("blood");
    public static final Holder.Reference<Attribute> EVOCATION_MAGIC_RESIST = newResistanceAttribute("evocation");
    public static final Holder.Reference<Attribute> NATURE_MAGIC_RESIST = newResistanceAttribute("nature");
    public static final Holder.Reference<Attribute> ELDRITCH_MAGIC_RESIST = newResistanceAttribute("eldritch");
    public static final Holder.Reference<Attribute> FIRE_SPELL_POWER = newPowerAttribute("fire");
    public static final Holder.Reference<Attribute> ICE_SPELL_POWER = newPowerAttribute("ice");
    public static final Holder.Reference<Attribute> LIGHTNING_SPELL_POWER = newPowerAttribute("lightning");
    public static final Holder.Reference<Attribute> HOLY_SPELL_POWER = newPowerAttribute("holy");
    public static final Holder.Reference<Attribute> ENDER_SPELL_POWER = newPowerAttribute("ender");
    public static final Holder.Reference<Attribute> BLOOD_SPELL_POWER = newPowerAttribute("blood");
    public static final Holder.Reference<Attribute> EVOCATION_SPELL_POWER = newPowerAttribute("evocation");
    public static final Holder.Reference<Attribute> NATURE_SPELL_POWER = newPowerAttribute("nature");
    public static final Holder.Reference<Attribute> ELDRITCH_SPELL_POWER = newPowerAttribute("eldritch");

    private static final List<Holder.Reference<Attribute>> ATTRIBUTES = List.of(
            MAX_MANA, MANA_REGEN, COOLDOWN_REDUCTION, SPELL_POWER, SPELL_RESIST,
            CAST_TIME_REDUCTION, SUMMON_DAMAGE, CASTING_MOVESPEED,
            FIRE_MAGIC_RESIST, ICE_MAGIC_RESIST, LIGHTNING_MAGIC_RESIST,
            HOLY_MAGIC_RESIST, ENDER_MAGIC_RESIST, BLOOD_MAGIC_RESIST,
            EVOCATION_MAGIC_RESIST, NATURE_MAGIC_RESIST, ELDRITCH_MAGIC_RESIST,
            FIRE_SPELL_POWER, ICE_SPELL_POWER, LIGHTNING_SPELL_POWER,
            HOLY_SPELL_POWER, ENDER_SPELL_POWER, BLOOD_SPELL_POWER,
            EVOCATION_SPELL_POWER, NATURE_SPELL_POWER, ELDRITCH_SPELL_POWER
    );

    public static void register() {
        FabricDefaultAttributeRegistry.MODIFY.register(context -> context.modifyAll((type, builder) -> {
            var existing = DefaultAttributes.getSupplier(type);
            for (var attribute : ATTRIBUTES) {
                if (!existing.hasAttribute(attribute)) {
                    builder.add(attribute);
                }
            }
        }));
    }

    private static Holder.Reference<Attribute> register(String name, Attribute attribute) {
        return Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, net.minecraft.resources.Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, name), attribute);
    }

    private static Holder.Reference<Attribute> newResistanceAttribute(String id) {
        return register(id + "_magic_resist", new MagicPercentAttribute(
                "attribute.irons_spellbooks." + id + "_magic_resist", 1.0D, -100, 100).setSyncable(true));
    }

    private static Holder.Reference<Attribute> newPowerAttribute(String id) {
        return register(id + "_spell_power", new MagicPercentAttribute(
                "attribute.irons_spellbooks." + id + "_spell_power", 1.0D, -100, 100).setSyncable(true));
    }

    public static double getValueOrDefault(LivingEntity entity, Holder<Attribute> attribute, double fallback) {
        if (entity == null || attribute == null) {
            return fallback;
        }
        AttributeInstance instance = entity.getAttribute(attribute);
        return instance != null ? instance.getValue() : fallback;
    }

    public static double getValueOrDefaultWithSpellbookFallback(LivingEntity entity, Holder<Attribute> attribute, double fallback) {
        double value = getValueOrDefault(entity, attribute, fallback);
        if (!(entity instanceof Player player)) {
            return value;
        }
        if (!player.level().isClientSide()) {
            return value;
        }
        if (Double.compare(value, fallback) != 0) {
            return value;
        }
        return getSpellbookAttributeFallback(player, attribute, fallback);
    }

    public static double getMaxManaWithFallback(LivingEntity entity) {
        double fromAttributes = getValueOrDefault(entity, MAX_MANA, 100.0D);
        if (!(entity instanceof Player player)) {
            return fromAttributes;
        }

        double fromEquipment = getMaxManaFromEquipment(player);
        double fromSpellbookCurio = getMaxManaFromSpellbookCurio(player);
        return Math.max(fromAttributes, Math.max(fromEquipment, fromSpellbookCurio));
    }

    private static double getMaxManaFromEquipment(Player player) {
        double base = 100.0D;
        double additive = 0.0D;
        double multipliedBase = 0.0D;
        double multipliedTotal = 0.0D;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) {
                continue;
            }
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty()) {
                continue;
            }
            ItemAttributeModifiers modifiers = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
            for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
                if (entry.attribute().value() != MAX_MANA.value()) {
                    continue;
                }
                if (entry.slot() != EquipmentSlotGroup.bySlot(slot)) {
                    continue;
                }
                switch (entry.modifier().operation()) {
                    case ADD_VALUE -> additive += entry.modifier().amount();
                    case ADD_MULTIPLIED_BASE -> multipliedBase += entry.modifier().amount();
                    case ADD_MULTIPLIED_TOTAL -> multipliedTotal += entry.modifier().amount();
                }
            }
        }

        return Math.max(100.0D, (base + additive) * (1.0D + multipliedBase) * (1.0D + multipliedTotal));
    }

    private static double getMaxManaFromSpellbookCurio(Player player) {
        ItemStack spellbookStack = Utils.getPlayerEquippedSpellbookStack(player);
        if (spellbookStack == null || spellbookStack.isEmpty() || !(spellbookStack.getItem() instanceof ITrinketItem curioItem)) {
            return 100.0D;
        }

        var TrinketSlotContext = new TrinketSlotContext(TrinketsSlots.SPELLBOOK_SLOT, player, 0, false, true);
        var modifiers = curioItem.getAttributeModifiers(TrinketSlotContext, net.minecraft.resources.Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "max_mana_fallback"), spellbookStack);
        double base = 100.0D;
        double additive = 0.0D;
        double multipliedBase = 0.0D;
        double multipliedTotal = 0.0D;

        for (var entry : modifiers.entries()) {
            if (entry.getKey().value() != MAX_MANA.value()) {
                continue;
            }
            switch (entry.getValue().operation()) {
                case ADD_VALUE -> additive += entry.getValue().amount();
                case ADD_MULTIPLIED_BASE -> multipliedBase += entry.getValue().amount();
                case ADD_MULTIPLIED_TOTAL -> multipliedTotal += entry.getValue().amount();
            }
        }

        return Math.max(100.0D, (base + additive) * (1.0D + multipliedBase) * (1.0D + multipliedTotal));
    }

    private static double getSpellbookAttributeFallback(Player player, Holder<Attribute> attribute, double fallback) {
        TrinketSlotResult result = TrinketsApi.getTrinketsInventory(player)
                .map(inv -> inv.findTrinkets(stack -> stack.getItem() instanceof SpellBook).stream()
                        .filter(entry -> TrinketsSlots.SPELLBOOK_SLOT.equals(entry.slotContext().identifier()))
                        .findFirst()
                        .orElse(null))
                .orElse(null);
        if (result == null) {
            return fallback;
        }
        ItemStack stack = result.stack();
        if (stack.isEmpty() || !(stack.getItem() instanceof ITrinketItem trinketItem)) {
            return fallback;
        }
        var modifiers = trinketItem.getAttributeModifiers(result.slotContext(), net.minecraft.resources.Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "client_spell_power_fallback"), stack);
        var attrModifiers = modifiers.get(attribute);
        if (attrModifiers == null || attrModifiers.isEmpty()) {
            return fallback;
        }
        return applyAttributeModifiers(fallback, attrModifiers);
    }

    private static double applyAttributeModifiers(double base, Iterable<AttributeModifier> modifiers) {
        double additive = 0.0D;
        double multipliedBase = 0.0D;
        double multipliedTotal = 0.0D;

        for (AttributeModifier modifier : modifiers) {
            switch (modifier.operation()) {
                case ADD_VALUE -> additive += modifier.amount();
                case ADD_MULTIPLIED_BASE -> multipliedBase += modifier.amount();
                case ADD_MULTIPLIED_TOTAL -> multipliedTotal += modifier.amount();
            }
        }
        return (base + additive) * (1.0D + multipliedBase) * (1.0D + multipliedTotal);
    }
}
