package io.redspace.ironsspellbooks.content;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import io.redspace.ironsspellbooks.api.util.EquipmentUtils;
import io.redspace.ironsspellbooks.worldgen.features.ExposedAirFeature;
import io.redspace.ironsspellbooks.worldgen.features.StructureFeatureConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;

import java.nio.file.Files;
import java.nio.file.Path;

/** Production component/feature logic and resource codecs, using only the current SDK. */
public final class NativeContentPortCheck implements net.fabricmc.api.ModInitializer {
    private static int qualityCount, conditionCount;

    @Override
    public void onInitialize() {
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            try {
                runChecks(server.registryAccess());
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            } finally {
                server.halt(false);
            }
        });
    }

    private static void runChecks(RegistryAccess registries) throws Exception {
        require(EquipmentUtils.isArmor(new ItemStack(Items.LEATHER_HELMET)), "Vanilla armor rejected");
        require(!EquipmentUtils.isArmor(new ItemStack(Items.CARVED_PUMPKIN)), "Pumpkin admitted as armor");
        require(!EquipmentUtils.isArmor(new ItemStack(Items.ELYTRA)), "Elytra admitted as armor");
        require(!EquipmentUtils.isArmor(new ItemStack(Items.IRON_SWORD)), "Sword admitted as armor");
        var zeroArmor = new ItemStack(Items.LEATHER_HELMET);
        zeroArmor.set(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.builder()
                .add(Attributes.ARMOR, new AttributeModifier(Identifier.parse("port_check:zero"), 0,
                        AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.HEAD).build());
        require(EquipmentUtils.isArmor(zeroArmor), "Zero-defense armor lost its armor identity");
        zeroArmor.set(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.builder()
                .add(Attributes.ARMOR, new AttributeModifier(Identifier.parse("port_check:wrong_slot"), 1,
                        AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.CHEST).build());
        require(!EquipmentUtils.isArmor(zeroArmor), "An unrelated slot modifier admitted non-armor");

        var stack = new ItemStack(Items.PAPER, 3);
        stack.set(DataComponents.CUSTOM_NAME, Component.literal("Saved scroll ingredients"));
        var ops = RegistryOps.create(NbtOps.INSTANCE, registries);
        var encoded = (CompoundTag) ItemStack.CODEC.encodeStart(ops, stack).getOrThrow();
        encoded.putInt("Slot", 2);
        var decoded = ItemStack.CODEC.parse(ops, encoded).getOrThrow();
        require(ItemStack.matches(stack, decoded) && encoded.getIntOr("Slot", -1) == 2,
                "Native stack codec changed components, count or legacy Slot data");

        var configuration = new StructureFeatureConfiguration(Identifier.parse("port_check:room"), 5, 4, 7, new BlockPos(1, 0, -1));
        var feature = new ExposedAirFeature(configuration);
        var featureJson = ExposedAirFeature.CODEC.codec().encodeStart(JsonOps.INSTANCE, feature).getOrThrow();
        require(ExposedAirFeature.CODEC.codec().parse(JsonOps.INSTANCE, featureJson).getOrThrow().equals(feature), "Feature round trip changed data");
        var invalid = StructureFeatureConfiguration.CODEC.encodeStart(JsonOps.INSTANCE, configuration).getOrThrow().getAsJsonObject();
        invalid.addProperty("x_size", 0);
        require(StructureFeatureConfiguration.CODEC.parse(JsonOps.INSTANCE, invalid).error().isPresent(), "Invalid feature size accepted");

        Path dataRoot = Path.of("src/main/resources/data");
        if (!Files.isDirectory(dataRoot)) dataRoot = Path.of("../../src/main/resources/data");
        try (var paths = Files.walk(dataRoot)) {
            for (var file : paths.filter(path -> path.toString().endsWith(".json")).toList()) {
                try (var reader = Files.newBufferedReader(file)) {
                    checkLoot(JsonParser.parseReader(reader), RegistryOps.create(JsonOps.INSTANCE, registries), file);
                }
            }
        }
        require(qualityCount > 0, "No migrated quality resources were checked");
        System.out.println("Native content checks passed: armor exclusions/zero defense, component stack+Slot round trip, feature codecs; "
                + qualityCount + " loot qualities and " + conditionCount + " loot conditions decoded");
    }

    private static void checkLoot(JsonElement node, RegistryOps<JsonElement> ops, Path file) {
        if (node.isJsonArray()) {
            node.getAsJsonArray().forEach(child -> checkLoot(child, ops, file));
        } else if (node.isJsonObject()) {
            var object = node.getAsJsonObject();
            if (object.has("function") && object.get("function").isJsonPrimitive()
                    && object.get("function").getAsString().startsWith("irons_spellbooks:")) {
                require(!object.has("conditions"), "Legacy conditions silently ignored in " + file);
                if (object.has("quality")) {
                    ContextFloatProviders.CODEC.parse(ops, object.get("quality"))
                            .getOrThrow(error -> new AssertionError(file + ": " + error));
                    qualityCount++;
                }
                if (object.has("condition")) {
                    LootItemCondition.CODEC.parse(ops, object.get("condition"))
                            .getOrThrow(error -> new AssertionError(file + ": " + error));
                    conditionCount++;
                }
            }
            object.asMap().values().forEach(child -> checkLoot(child, ops, file));
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
