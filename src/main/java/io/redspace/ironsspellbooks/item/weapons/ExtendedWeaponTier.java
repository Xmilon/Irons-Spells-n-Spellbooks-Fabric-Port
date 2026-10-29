package io.redspace.ironsspellbooks.item.weapons;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

import net.minecraft.world.level.block.Block;



public class ExtendedWeaponTier implements IronsWeaponTier {
    public static ExtendedWeaponTier HELLRAZOR = new ExtendedWeaponTier(2031, 12, -2.6f, 16, BlockTags.INCORRECT_FOR_NETHERITE_TOOL, repairTag("netherite_scrap"));
    public static ExtendedWeaponTier LEGIONNAIRE_FLAMBERGE = new ExtendedWeaponTier(2031, 10, -2.5f, 4, BlockTags.INCORRECT_FOR_NETHERITE_TOOL, repairTag("netherite_scrap"), new AttributeContainer(Attributes.ARMOR, 4, AttributeModifier.Operation.ADD_VALUE));
    public static ExtendedWeaponTier DECREPIT_FLAMBERGE = new ExtendedWeaponTier(1000, 10, -2.7f, 4, BlockTags.INCORRECT_FOR_NETHERITE_TOOL, repairTag("netherite_scrap"), new AttributeContainer(Attributes.ARMOR, 4, AttributeModifier.Operation.ADD_VALUE));
    public static ExtendedWeaponTier DECREPIT_SCYTHE = new ExtendedWeaponTier(1000, 10, -2.6f, 4, BlockTags.INCORRECT_FOR_NETHERITE_TOOL, repairTag("netherite_scrap"));
    public static ExtendedWeaponTier DREADSWORD = new ExtendedWeaponTier(1061, 6, -2.4f, 14, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, repairTag("arcane_ingot"));
    public static ExtendedWeaponTier MISERY = new ExtendedWeaponTier(1061, 7, -2.1f, 14, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, repairTag("arcane_ingot"));
    public static ExtendedWeaponTier METAL_MAGEHUNTER = new ExtendedWeaponTier(1561, 6, -2.4f, 12, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, repairTag("arcane_ingot"), new AttributeContainer(AttributeRegistry.SPELL_RESIST, .15, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    public static ExtendedWeaponTier CRYSTAL_MAGEHUNTER = new ExtendedWeaponTier(1561, 6, -2.4f, 12, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, repairTag("diamond"), new AttributeContainer(AttributeRegistry.SPELL_RESIST, .15, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    public static ExtendedWeaponTier SPELLBREAKER = new ExtendedWeaponTier(2031, 9, -2.2f, 12, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, repairTag("diamond"), new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, .15, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    public static ExtendedWeaponTier TRUTHSEEKER = new ExtendedWeaponTier(2031, 11, -3f, 10, BlockTags.INCORRECT_FOR_NETHERITE_TOOL, repairTag("arcane_ingot"));
    public static ExtendedWeaponTier CLAYMORE = new ExtendedWeaponTier(1000, 9, -2.7f, 8, BlockTags.INCORRECT_FOR_IRON_TOOL, repairTag("iron_ingot"));
    public static ExtendedWeaponTier AMETHYST_RAPIER = new ExtendedWeaponTier(2031, 7, -1.7f, 16, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, repairTag("amethyst_shard"));
    public static ExtendedWeaponTier ICE_GREATSWORD = new ExtendedWeaponTier(2031, 15, -3.1f, 16, BlockTags.INCORRECT_FOR_NETHERITE_TOOL, repairTag("ice"));
    public static ExtendedWeaponTier TWILIGHT_GALE = new ExtendedWeaponTier(2031, 12, -2.6f, 16, BlockTags.INCORRECT_FOR_NETHERITE_TOOL, repairTag("lightning_bottle"));
    public static ExtendedWeaponTier VOID_ICHOR = new ExtendedWeaponTier(2031, 8, -2f, 12, BlockTags.INCORRECT_FOR_NETHERITE_TOOL, repairTag("diamond"), new AttributeContainer(AttributeRegistry.COOLDOWN_REDUCTION, .15, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

    private static TagKey<Item> repairTag(String ingredient) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "weapon_repair/" + ingredient));
    }

    public ToolMaterial toolMaterial() {
        return new ToolMaterial(incorrectBlocksForDrops, uses, 1f, damage, enchantmentValue, repairItems);
    }
    int uses;
    float damage;
    float speed;
    int enchantmentValue;
    TagKey<Block> incorrectBlocksForDrops;
    TagKey<Item> repairItems;
    AttributeContainer[] attributes;

    public ExtendedWeaponTier(int uses, float damage, float speed, int enchantmentValue, TagKey<Block> incorrectBlocksForDrops, TagKey<Item> repairItems, AttributeContainer... attributes) {
        this.uses = uses;
        this.damage = damage;
        this.speed = speed;
        this.enchantmentValue = enchantmentValue;
        this.incorrectBlocksForDrops = incorrectBlocksForDrops;
        this.repairItems = repairItems;
        this.attributes = attributes;
    }

    public int getUses() {
        return uses;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return damage;
    }

    public TagKey<Block> getIncorrectBlocksForDrops() {
        return incorrectBlocksForDrops;
    }

    public int getEnchantmentValue() {
        return enchantmentValue;
    }

    public TagKey<Item> getRepairItemsTag() {
        return repairItems;
    }

    @Override
    public AttributeContainer[] getAdditionalAttributes() {
        return this.attributes;
    }
}
