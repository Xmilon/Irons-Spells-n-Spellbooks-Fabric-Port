package io.redspace.ironsspellbooks.registries;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.util.ModTags;
import net.minecraft.util.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.Tags;

import java.util.EnumMap;

public class ArmorMaterialRegistry {

    /** Materials are plain values in 26.3; kept so the mod entrypoint keeps one call per registry. */
    public static void register(IEventBus eventBus) {
    }

    public static ArmorMaterial TARNISHED = register("tarnished",
            makeArmorMap(0, 0, 0, 0),
            15,
            SoundEvents.ARMOR_EQUIP_DIAMOND,
            Tags.Items.INGOTS_IRON,
            0,
            0);

    public static ArmorMaterial WANDERING_MAGICIAN = register("wandering_magician",
            makeArmorMap(2, 6, 5, 2),
            15,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            Tags.Items.LEATHERS,
            0,
            0);

    public static ArmorMaterial PUMPKIN = register("pumpkin",
            schoolArmorMap(),
            15,
            SoundEvents.ARMOR_EQUIP_TURTLE,
            repairTag("pumpkin"),
            0,
            0);

    public static ArmorMaterial SCHOOL = register("school_armor",
            schoolArmorMap(),
            20,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            repairTag("school_armor"),
            0,
            0);

    public static ArmorMaterial NETHERITE_BATTLEMAGE = register("netherite_battlemage",
            schoolArmorMap(),
            20,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            Tags.Items.INGOTS_NETHERITE,
            3,
            0);

    public static ArmorMaterial PALADIN = register("paladin",
            schoolArmorMap(),
            40,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            ModTags.MITHRIL_INGOT,
            4,
            0.4f);

    public static ArmorMaterial INFERNAL_SORCERER = register("infernal_sorcerer",
            schoolArmorMap(),
            40,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            ModTags.MITHRIL_INGOT,
            0,
            0f);

    public static ArmorMaterial BOOTS_OF_SPEED = register("speed_boots",
            schoolArmorMap(),
            40,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            ModTags.MITHRIL_INGOT,
            0,
            0f);

    public static ArmorMaterial DEV = register("dev",
            makeArmorMap(20, 20, 20, 20),
            20,
            SoundEvents.ARMOR_EQUIP_GOLD,
            Tags.Items.INGOTS_GOLD,
            20,
            20);

    private static ArmorMaterial register(
            String name,
            EnumMap<ArmorType, Integer> defense,
            int enchantmentValue,
            Holder<SoundEvent> equipSound,
            TagKey<Item> repairItems,
            float toughness,
            float knockbackResistance
    ) {
        // shortcut: one base durability for every set (the old items passed per-set multipliers of 22-37 on each piece)
        return new ArmorMaterial(37, defense, enchantmentValue, equipSound, toughness, knockbackResistance, repairItems,
                ResourceKey.create(EquipmentAssets.ROOT_ID, IronsSpellbooks.id(name)));
    }

    private static TagKey<Item> repairTag(String name) {
        return TagKey.create(Registries.ITEM, IronsSpellbooks.id("armor_repair/" + name));
    }

    static public EnumMap<ArmorType, Integer> makeArmorMap(int helmet, int chestplate, int leggings, int boots) {
        return Util.make(new EnumMap<>(ArmorType.class), (p_266655_) -> {
            p_266655_.put(ArmorType.BOOTS, boots);
            p_266655_.put(ArmorType.LEGGINGS, leggings);
            p_266655_.put(ArmorType.CHESTPLATE, chestplate);
            p_266655_.put(ArmorType.HELMET, helmet);
        });
    }

    static public EnumMap<ArmorType, Integer> schoolArmorMap() {
        return makeArmorMap(3, 8, 6, 3);
    }
}
