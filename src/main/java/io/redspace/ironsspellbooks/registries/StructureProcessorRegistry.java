package io.redspace.ironsspellbooks.registries;

import com.mojang.serialization.MapCodec;
import io.redspace.ironsspellbooks.worldgen.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public final class StructureProcessorRegistry {
    public static void register() {
        Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, Identifier.fromNamespaceAndPath("irons_spellbooks", "clear_vault_data"), ClearVaultDataProcessor.CODEC);
        Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, Identifier.fromNamespaceAndPath("irons_spellbooks", "weather_copper"), WeatherCopperProcessor.CODEC);
        Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, Identifier.fromNamespaceAndPath("irons_spellbooks", "degrade_slabs_stairs"), DegradeSlabsStairsProcessor.CODEC);
        Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, Identifier.fromNamespaceAndPath("irons_spellbooks", "foundation"), StructureFoundationProcessor.CODEC);
        Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, Identifier.fromNamespaceAndPath("irons_spellbooks", "handle_lit_blocks"), HandleLitBlocksProcessor.CODEC);
        Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, Identifier.fromNamespaceAndPath("irons_spellbooks", "clear_portal_frame_data"), ClearPortalFrameDataProcessor.CODEC);
    }
}
