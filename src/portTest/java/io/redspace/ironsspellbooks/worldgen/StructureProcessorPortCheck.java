package io.redspace.ironsspellbooks.worldgen;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import io.redspace.ironsspellbooks.registries.StructureProcessorRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

import java.lang.reflect.Proxy;
import java.util.List;

/** Runs pure checks standalone and verifies real codec registration in the isolated Fabric server probe. */
public final class StructureProcessorPortCheck implements ModInitializer {
    @Override
    public void onInitialize() {
        StructureProcessorRegistry.register();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            check();
            var ops = RegistryOps.create(JsonOps.INSTANCE, server.registryAccess());
            for (String json : fixtures()) {
                var input = JsonParser.parseString(json);
                var processor = StructureProcessorType.SINGLE_CODEC.parse(ops, input).getOrThrow();
                var encoded = StructureProcessorType.SINGLE_CODEC.encodeStart(ops, processor).getOrThrow();
                require(encoded.getAsJsonObject().get("processor_type").equals(input.getAsJsonObject().get("processor_type")), "Processor ID changed");
            }
            var stairs = info(Blocks.STONE_STAIRS.defaultBlockState(), null);
            var slabs = info(Blocks.STONE_SLAB.defaultBlockState(), null);
            var settings = new StructurePlaceSettings();
            var degrade = new DegradeSlabsStairsProcessor(1, 0);
            require(degrade.processBlock(null, BlockPos.ZERO, BlockPos.ZERO, BlockPos.ZERO, stairs, settings).state().is(Blocks.STONE_SLAB), "Stairs did not degrade to slab");
            require(degrade.processBlock(null, BlockPos.ZERO, BlockPos.ZERO, BlockPos.ZERO, slabs, settings).state().isAir(), "Slab removal changed");
            System.out.println("Structure processor Fabric registration, dispatch codecs and loaded stair/slab tags passed");
            server.halt(false);
        });
    }

    public static void main(String[] args) {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
        check();
    }

    private static List<String> fixtures() {
        return List.of(
                "{\"processor_type\":\"irons_spellbooks:clear_vault_data\"}",
                "{\"processor_type\":\"irons_spellbooks:weather_copper\",\"bias\":0.5}",
                "{\"processor_type\":\"irons_spellbooks:degrade_slabs_stairs\",\"chance_stairs\":0.6,\"chance_slabs\":0.4}",
                "{\"processor_type\":\"irons_spellbooks:foundation\",\"block\":{\"id\":\"minecraft:stone\"}}",
                "{\"processor_type\":\"irons_spellbooks:handle_lit_blocks\",\"chanceLit\":0.5,\"byBlock\":{\"minecraft:campfire\":1.0}}",
                "{\"processor_type\":\"irons_spellbooks:clear_portal_frame_data\"}");
    }

    private static void check() {
        var ops = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
        var codecs = List.of(ClearVaultDataProcessor.CODEC, WeatherCopperProcessor.CODEC, DegradeSlabsStairsProcessor.CODEC,
                StructureFoundationProcessor.CODEC, HandleLitBlocksProcessor.CODEC, ClearPortalFrameDataProcessor.CODEC);
        for (int i = 0; i < codecs.size(); i++) {
            var json = JsonParser.parseString(fixtures().get(i));
            roundTrip(codecs.get(i), ops, json);
        }
        var nbt = new CompoundTag();
        nbt.putString("keep", "yes");
        nbt.putString("server_data", "old");
        nbt.putString("shared_data", "old");
        var vault = info(Blocks.VAULT.defaultBlockState(), nbt);
        var settings = new StructurePlaceSettings();
        var cleared = new ClearVaultDataProcessor().processBlock(null, BlockPos.ZERO, BlockPos.ZERO, BlockPos.ZERO, vault, settings);
        require(!cleared.nbt().contains("server_data") && !cleared.nbt().contains("shared_data"), "Vault data not cleared");
        require(cleared.nbt().getStringOr("keep", "").equals("yes"), "Unrelated vault data removed");
        require(nbt.contains("server_data") && nbt.contains("shared_data"), "Shared template NBT was mutated");
        var copper = info(Blocks.COPPER_BLOCK.weathering().unaffected().defaultBlockState(), null);
        var weather = new WeatherCopperProcessor(1);
        require(weather.processBlock(null, BlockPos.ZERO, BlockPos.ZERO, BlockPos.ZERO, copper, settings).state().is(Blocks.COPPER_BLOCK.weathering().oxidized()), "Copper aging changed");
        var seeded = new WeatherCopperProcessor(0);
        require(seeded.processBlock(null, BlockPos.ZERO, BlockPos.ZERO, BlockPos.ZERO, copper, settings).state()
                .equals(seeded.processBlock(null, BlockPos.ZERO, BlockPos.ZERO, BlockPos.ZERO, copper, new StructurePlaceSettings()).state()), "Copper aging is not position seeded");
        var lit = HandleLitBlocksProcessor.CODEC.codec().parse(ops, JsonParser.parseString("{\"chanceLit\":0,\"byBlock\":{\"minecraft:campfire\":1}}" )).getOrThrow();
        var campfire = info(Blocks.CAMPFIRE.defaultBlockState().setValue(BlockStateProperties.LIT, false), null);
        var furnace = info(Blocks.FURNACE.defaultBlockState().setValue(BlockStateProperties.LIT, true), null);
        require(lit.processBlock(null, BlockPos.ZERO, BlockPos.ZERO, BlockPos.ZERO, campfire, settings).state().getValue(BlockStateProperties.LIT), "Per-block light override lost");
        require(!lit.processBlock(null, BlockPos.ZERO, BlockPos.ZERO, BlockPos.ZERO, furnace, settings).state().getValue(BlockStateProperties.LIT), "Default light chance lost");
        var foundation = StructureFoundationProcessor.CODEC.codec().parse(ops, JsonParser.parseString("{\"block\":{\"id\":\"minecraft:stone\"}}" )).getOrThrow();
        var stone = info(Blocks.STONE.defaultBlockState(), null);
        require(foundation.processBlock(null, BlockPos.ZERO, BlockPos.ZERO, new BlockPos(0, 1, 0), stone, settings) == stone, "Foundation changed non-bottom block");
        LevelReader boundary = (LevelReader) Proxy.newProxyInstance(LevelReader.class.getClassLoader(), new Class<?>[]{LevelReader.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getBlockState" -> Blocks.AIR.defaultBlockState();
                    case "getMinY", "getMaxY" -> 1;
                    default -> throw new AssertionError("Unexpected out-of-bounds world access: " + method.getName());
                });
        foundation.processBlock(boundary, BlockPos.ZERO, BlockPos.ZERO, BlockPos.ZERO, stone, settings);
        require(WeatherCopperProcessor.CODEC.codec().parse(ops, JsonParser.parseString("{\"bias\":\"bad\"}")).error().isPresent(), "Malformed copper data accepted");
        checkBlockStateData(ops);
        System.out.println("Six structure processor codecs and vault isolation, seeded copper, light overrides and foundation bounds passed");
    }

    private static void checkBlockStateData(RegistryOps<com.google.gson.JsonElement> ops) {
        var folder = java.nio.file.Path.of("src/main/resources/data/irons_spellbooks/worldgen/processor_list");
        if (!java.nio.file.Files.isDirectory(folder)) folder = java.nio.file.Path.of("..").resolve(folder);
        int[] counts = {0, 0};
        try (var files = java.nio.file.Files.list(folder)) {
            for (var file : files.filter(path -> path.toString().endsWith(".json")).toList()) {
                try (var reader = java.nio.file.Files.newBufferedReader(file)) {
                    checkBlockStateNode(JsonParser.parseReader(reader), ops, counts);
                }
            }
        } catch (java.io.IOException exception) {
            throw new AssertionError("Could not inspect processor data", exception);
        }
        require(counts[0] > 0 && counts[1] == 1, "Unexpected processor block-state coverage");
        System.out.println("Native block-state JSON decoded: " + counts[0] + "; one wisewood output awaits the full block registry");
    }

    private static void checkBlockStateNode(com.google.gson.JsonElement json,
            RegistryOps<com.google.gson.JsonElement> ops, int[] counts) {
        if (json.isJsonArray()) {
            json.getAsJsonArray().forEach(child -> checkBlockStateNode(child, ops, counts));
        } else if (json.isJsonObject()) {
            var object = json.getAsJsonObject();
            require(!object.has("Name") && !object.has("Properties"), "Legacy block-state JSON remains");
            if (object.has("id")) {
                if (object.get("id").getAsString().equals("irons_spellbooks:wisewood_bookshelf")) {
                    counts[1]++;
                } else {
                    net.minecraft.world.level.block.state.BlockState.CODEC.parse(ops, object).getOrThrow();
                    counts[0]++;
                }
            }
            object.entrySet().forEach(entry -> checkBlockStateNode(entry.getValue(), ops, counts));
        }
    }

    private static <T extends StructureProcessor> void roundTrip(com.mojang.serialization.MapCodec<T> codec,
            RegistryOps<com.google.gson.JsonElement> ops, com.google.gson.JsonElement input) {
        var processor = codec.codec().parse(ops, input).getOrThrow();
        require(processor.codec() == codec, "Processor reports different codec");
        var encoded = codec.codec().encodeStart(ops, processor).getOrThrow();
        require(codec.codec().parse(ops, encoded).result().isPresent(), "Processor codec failed round trip");
    }

    private static StructureBlockInfo info(net.minecraft.world.level.block.state.BlockState state, CompoundTag nbt) {
        return new StructureBlockInfo(new BlockPos(2, 2, 2), state, nbt);
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
