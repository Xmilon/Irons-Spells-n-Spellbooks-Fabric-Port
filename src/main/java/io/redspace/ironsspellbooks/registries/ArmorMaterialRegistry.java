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

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;

import java.util.EnumMap;

public class ArmorMaterialRegistry {

    public static ArmorMaterial TARNISHED = create("tarnished", 26,
            makeArmorMap(0, 0, 0, 0),
            15,
            SoundEvents.ARMOR_EQUIP_DIAMOND,
            ConventionalItemTags.IRON_INGOTS,
            0,
            0);

    public static ArmorMaterial WANDERING_MAGICIAN = create("wandering_magician", 22,
            makeArmorMap(2, 6, 5, 2),
            15,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            ConventionalItemTags.LEATHERS,
            0,
            0);

    public static ArmorMaterial PUMPKIN = create("pumpkin", 26,
            schoolArmorMap(),
            15,
            SoundEvents.ARMOR_EQUIP_TURTLE,
            repairTag("pumpkin"),
            0,
            0);

    public static ArmorMaterial SCHOOL = create("school_armor", 37,
            schoolArmorMap(),
            20,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            repairTag("school_armor"),
            0,
            0);

    public static ArmorMaterial NETHERITE_BATTLEMAGE = create("netherite_battlemage", 37,
            schoolArmorMap(),
            20,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            ConventionalItemTags.NETHERITE_INGOTS,
            3,
            0);

    public static ArmorMaterial PALADIN = create("paladin", 37,
            schoolArmorMap(),
            40,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            ModTags.MITHRIL_INGOT,
            4,
            0.4f);

    public static ArmorMaterial INFERNAL_SORCERER = create("infernal_sorcerer", 37,
            schoolArmorMap(),
            40,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            ModTags.MITHRIL_INGOT,
            0,
            0f);

    public static ArmorMaterial BOOTS_OF_SPEED = create("speed_boots", 37,
            schoolArmorMap(),
            40,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            ModTags.MITHRIL_INGOT,
            0,
            0f);

    public static ArmorMaterial DEV = create("dev", 0,
            makeArmorMap(20, 20, 20, 20),
            20,
            SoundEvents.ARMOR_EQUIP_GOLD,
            ConventionalItemTags.GOLD_INGOTS,
            20,
            20);

    private static ArmorMaterial create(
            String name, int durability,
            EnumMap<ArmorType, Integer> defense,
            int enchantmentValue,
            Holder<SoundEvent> equipSound,
            TagKey<Item> repairItems,
            float toughness,
            float knockbackResistance
    ) {

        return new ArmorMaterial(durability, defense, enchantmentValue, equipSound, toughness, knockbackResistance, repairItems,
                ResourceKey.create(EquipmentAssets.ROOT_ID, net.minecraft.resources.Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, name)));
    }

    private static TagKey<Item> repairTag(String name) {
        return TagKey.create(Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "armor_repair/" + name));
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
