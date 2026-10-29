package io.redspace.ironsspellbooks.item;

import com.geckolib.renderer.GeoArmorRenderer;
import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.item.armor.ExtendedArmorItem;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.item.weapons.ExtendedWeaponTier;
import io.redspace.ironsspellbooks.registries.ArmorMaterialRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.LinkedHashMap;
import java.util.Map;

/** Isolated Fabric server probe; never packaged in the main mod. */
public final class EquipmentPortProbe implements ModInitializer {
    @Override
    public void onInitialize() {
        AttributeRegistry.register();
        var typeKey = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.parse("equipment_probe:custom_zombie"));
        var customType = Registry.register(BuiltInRegistries.ENTITY_TYPE, typeKey,
                net.minecraft.world.entity.EntityType.Builder.<net.minecraft.world.entity.monster.zombie.Zombie>of(
                        net.minecraft.world.entity.monster.zombie.Zombie::new, net.minecraft.world.entity.MobCategory.MONSTER).build(typeKey));
        net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry.register(customType,
                net.minecraft.world.entity.monster.zombie.Zombie.createAttributes().add(AttributeRegistry.MAX_MANA, 321));
        var ingredients = new LinkedHashMap<String, Item>();
        for (var name : new String[]{"arcane_ingot", "magic_cloth", "lightning_bottle", "mithril_ingot"}) {
            ingredients.put(name, register(name, new Item(properties(name))));
        }
        var tiers = Map.ofEntries(
                Map.entry("hellrazor", ExtendedWeaponTier.HELLRAZOR),
                Map.entry("legionnaire_flamberge", ExtendedWeaponTier.LEGIONNAIRE_FLAMBERGE),
                Map.entry("decrepit_flamberge", ExtendedWeaponTier.DECREPIT_FLAMBERGE),
                Map.entry("decrepit_scythe", ExtendedWeaponTier.DECREPIT_SCYTHE),
                Map.entry("dreadsword", ExtendedWeaponTier.DREADSWORD),
                Map.entry("misery", ExtendedWeaponTier.MISERY),
                Map.entry("metal_magehunter", ExtendedWeaponTier.METAL_MAGEHUNTER),
                Map.entry("crystal_magehunter", ExtendedWeaponTier.CRYSTAL_MAGEHUNTER),
                Map.entry("spellbreaker", ExtendedWeaponTier.SPELLBREAKER),
                Map.entry("truthseeker", ExtendedWeaponTier.TRUTHSEEKER),
                Map.entry("claymore", ExtendedWeaponTier.CLAYMORE),
                Map.entry("amethyst_rapier", ExtendedWeaponTier.AMETHYST_RAPIER),
                Map.entry("ice_greatsword", ExtendedWeaponTier.ICE_GREATSWORD),
                Map.entry("twilight_gale", ExtendedWeaponTier.TWILIGHT_GALE),
                Map.entry("void_ichor", ExtendedWeaponTier.VOID_ICHOR));
        var swords = new LinkedHashMap<String, Item>();
        tiers.forEach((name, tier) -> swords.put(name, register(name, new ExtendedSwordItem(tier, properties(name)))));
        var wandering = armor("wandering_helmet", ArmorMaterialRegistry.WANDERING_MAGICIAN, ArmorType.HELMET);
        var pumpkin = armor("pumpkin_helmet", ArmorMaterialRegistry.PUMPKIN, ArmorType.HELMET);
        var school = armor("school_chestplate", ArmorMaterialRegistry.SCHOOL, ArmorType.CHESTPLATE);
        var tarnished = armor("tarnished_crown", ArmorMaterialRegistry.TARNISHED, ArmorType.HELMET);
        var gold = armor("gold_crown", ArmorMaterialRegistry.DEV, ArmorType.HELMET);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            require(net.minecraft.world.entity.ai.attributes.DefaultAttributes.getSupplier(customType).getValue(AttributeRegistry.MAX_MANA) == 321,
                    "Global attributes overwrote the custom mob's mana");
            checkScrollAndCompassData();
            require(new ItemStack(wandering).getMaxDamage() == 242, "Wandering durability changed");
            require(new ItemStack(pumpkin).getMaxDamage() == 286, "Pumpkin durability changed");
            require(new ItemStack(school).getMaxDamage() == 592, "School durability changed");
            require(new ItemStack(tarnished).getMaxDamage() == 286, "Tarnished durability changed");
            require(!new ItemStack(gold).isDamageableItem(), "Developer crown became damageable");
            require(new ItemStack(pumpkin).is(ItemTags.GAZE_DISGUISE_EQUIPMENT), "Pumpkin gaze protection missing");
            requireRepair(pumpkin, Items.HAY_BLOCK, Items.PUMPKIN);
            requireRepair(school, ingredients.get("magic_cloth"), Items.LEATHER);
            requireRepair(wandering, Items.LEATHER, Items.IRON_INGOT);

            swords.forEach((name, item) -> {
                var tier = tiers.get(name);
                var stack = new ItemStack(item);
                require(stack.getMaxDamage() == tier.getUses(), name + " durability changed");
                var modifiers = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS,
                        net.minecraft.world.item.component.ItemAttributeModifiers.EMPTY).modifiers();
                var damage = modifiers.stream().filter(entry -> entry.attribute().equals(Attributes.ATTACK_DAMAGE)).toList();
                require(damage.size() == 1 && damage.getFirst().modifier().amount() == tier.getAttackDamageBonus(),
                        name + " damage duplicated or changed");
                var speed = modifiers.stream().filter(entry -> entry.attribute().equals(Attributes.ATTACK_SPEED)).toList();
                require(speed.size() == 1 && speed.getFirst().modifier().amount() == tier.getSpeed(), name + " speed changed");
                String ingredient = tier.getRepairItemsTag().location().getPath().substring("weapon_repair/".length());
                var expected = ingredients.getOrDefault(ingredient,
                        BuiltInRegistries.ITEM.getValue(Identifier.withDefaultNamespace(ingredient)));
                requireRepair(item, expected, Items.STICK);
            });
            var modifiers = new ItemStack(school).get(DataComponents.ATTRIBUTE_MODIFIERS).modifiers();
            require(modifiers.stream().filter(entry -> entry.attribute().equals(AttributeRegistry.MAX_MANA)).count() == 1,
                    "Armor mana bonus duplicated");
            require(modifiers.stream().filter(entry -> entry.attribute().equals(Attributes.ARMOR)).count() == 1,
                    "Armor defense duplicated");
            org.slf4j.LoggerFactory.getLogger("EquipmentPortProbe").info(
                    "Equipment milestone passed: 15 weapon tiers, repair tags, armor durability/modifiers, indestructible crown and gaze protection");
            server.halt(false);
        });
    }

    private static void checkScrollAndCompassData() {
        var paper = new ItemStack(Items.PAPER);
        var school = new io.redspace.ironsspellbooks.api.spells.SchoolType(
                Identifier.parse("addon:fire"), ItemTags.ARROWS, net.minecraft.network.chat.Component.literal("Addon"),
                Attributes.ATTACK_DAMAGE, Attributes.ARMOR, net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK,
                net.minecraft.world.damagesource.DamageTypes.GENERIC);
        io.redspace.ironsspellbooks.util.ScrollSchoolTag.setSchool(paper, school);
        require(io.redspace.ironsspellbooks.util.ScrollSchoolTag.getSchool(paper).orElseThrow().equals("addon:fire"),
                "Declared addon school ID lost");
        require(paper.get(DataComponents.CUSTOM_MODEL_DATA).getFloat(0) == 5f, "Scroll numeric model ordinal changed");
        io.redspace.ironsspellbooks.util.ScrollSchoolTag.setSchool(paper, "addon:unknown");
        require(io.redspace.ironsspellbooks.util.ScrollSchoolTag.getSchool(paper).orElseThrow().equals("addon:unknown")
                && !paper.has(DataComponents.CUSTOM_MODEL_DATA), "Unknown school override was lost or left stale model data");
        var data = new io.redspace.ironsspellbooks.api.item.WaywardCompassData(new net.minecraft.core.BlockPos(-123, 64, 456));
        var encoded = io.redspace.ironsspellbooks.api.item.WaywardCompassData.CODEC
                .encodeStart(com.mojang.serialization.JsonOps.INSTANCE, data).getOrThrow();
        var copy = io.redspace.ironsspellbooks.api.item.WaywardCompassData.CODEC
                .parse(com.mojang.serialization.JsonOps.INSTANCE, encoded).getOrThrow();
        require(copy.equals(data), "Existing compass data layout changed");
        require(!data.lodestoneTracker().tracked() && data.lodestoneTracker().target().orElseThrow().dimension()
                .equals(net.minecraft.world.level.Level.OVERWORLD)
                && data.lodestoneTracker().target().orElseThrow().pos().equals(data.blockPos()), "Native compass target changed");
    }

    private static void requireRepair(Item item, Item expected, Item wrong) {
        var repairable = new ItemStack(item).get(DataComponents.REPAIRABLE);
        require(repairable != null && repairable.isValidRepairItem(new ItemStack(expected))
                && !repairable.isValidRepairItem(new ItemStack(wrong)), "Wrong repair ingredient for " + item);
    }

    private static Item armor(String id, ArmorMaterial material, ArmorType type) {
        return register(id, new TestArmor(material, type, properties(id)));
    }

    private static Item.Properties properties(String id) {
        return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("irons_spellbooks", id)));
    }

    private static Item register(String id, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath("irons_spellbooks", id), item);
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static final class TestArmor extends ExtendedArmorItem {
        TestArmor(ArmorMaterial material, ArmorType type, Item.Properties properties) {
            super(material, type, properties, new AttributeContainer(AttributeRegistry.MAX_MANA, 75,
                    AttributeModifier.Operation.ADD_VALUE));
        }
        @Override
        @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
        public GeoArmorRenderer<?, ?> supplyRenderer() { return null; }
    }
}
