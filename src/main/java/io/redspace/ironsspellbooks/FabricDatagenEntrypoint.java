package io.redspace.ironsspellbooks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.redspace.ironsspellbooks.util.ScrollSchoolModels;
import io.redspace.ironsspellbooks.registries.FeatureRegistry;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.ArrayList;

public class FabricDatagenEntrypoint implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        var pack = generator.createPack();
        pack.addProvider(MithrilLootTablesProvider::new);
        pack.addProvider(MithrilWorldgenProvider::new);
        pack.addProvider(ScrollItemModelProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.FEATURE, FeatureRegistry::bootstrapFeature)
                .add(Registries.PLACED_FEATURE, FeatureRegistry::bootstrapPlacedFeature);
    }

    private static class MithrilLootTablesProvider implements DataProvider {
        private final PackOutput.PathProvider blockLootPathProvider;

        private MithrilLootTablesProvider(FabricPackOutput output) {
            this.blockLootPathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "loot_table/blocks");
        }

        @Override
        public CompletableFuture<?> run(CachedOutput cachedOutput) {
            CompletableFuture<?> mithril = DataProvider.saveStable(cachedOutput, selfDrop("irons_spellbooks:mithril_ore"), blockLootPathProvider.json(Identifier.parse("irons_spellbooks:mithril_ore")));
            CompletableFuture<?> deepslateMithril = DataProvider.saveStable(cachedOutput, selfDrop("irons_spellbooks:deepslate_mithril_ore"), blockLootPathProvider.json(Identifier.parse("irons_spellbooks:deepslate_mithril_ore")));
            return CompletableFuture.allOf(mithril, deepslateMithril);
        }

        @Override
        public String getName() {
            return "Iron's Spellbooks Mithril Loot Tables";
        }

        private static JsonObject selfDrop(String blockId) {
            JsonObject root = new JsonObject();
            root.addProperty("type", "minecraft:block");

            JsonArray pools = new JsonArray();
            JsonObject pool = new JsonObject();
            pool.addProperty("rolls", 1);
            JsonArray entries = new JsonArray();
            JsonObject entry = new JsonObject();
            entry.addProperty("type", "minecraft:item");
            entry.addProperty("name", blockId);
            entries.add(entry);
            pool.add("entries", entries);
            pools.add(pool);

            root.add("pools", pools);
            return root;
        }
    }

    private static class MithrilWorldgenProvider extends FabricDynamicRegistryProvider {
        private MithrilWorldgenProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries);
        }

        @Override
        protected void configure(HolderLookup.Provider registries, Entries entries) {
            entries.add(registries.lookupOrThrow(Registries.FEATURE), FeatureRegistry.MITHRIL_ORE_FEATURE);
            entries.add(registries.lookupOrThrow(Registries.PLACED_FEATURE), FeatureRegistry.MITHRIL_ORE_PLACEMENT);
        }

        @Override
        public String getName() {
            return "Iron's Spellbooks Mithril Worldgen";
        }
    }
    private static class ScrollItemModelProvider implements DataProvider {
        private final PackOutput.PathProvider itemModelPathProvider;
        private final PackOutput.PathProvider itemDefinitionPathProvider;

        private ScrollItemModelProvider(FabricPackOutput output) {
            this.itemModelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/item");
            this.itemDefinitionPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");
        }

        @Override
        public CompletableFuture<?> run(CachedOutput cachedOutput) {
            var writes = new ArrayList<CompletableFuture<?>>();
            writes.add(DataProvider.saveStable(cachedOutput, scrollModel("scroll"), itemModelPathProvider.json(IronsSpellbooks.id("scroll"))));
            writes.add(DataProvider.saveStable(cachedOutput, scrollItemDefinition(), itemDefinitionPathProvider.json(IronsSpellbooks.id("scroll"))));
            for (String school : ScrollSchoolModels.schoolOrder()) {
                String name = "scroll_" + school;
                writes.add(DataProvider.saveStable(cachedOutput, scrollModel(name), itemModelPathProvider.json(IronsSpellbooks.id(name))));
            }
            return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
        }

        @Override
        public String getName() {
            return "Iron's Spellbooks Scroll Item Models";
        }

        private static JsonObject scrollModel(String name) {
            JsonObject root = new JsonObject();
            root.addProperty("parent", "item/generated");

            JsonObject textures = new JsonObject();
            textures.addProperty("layer0", "irons_spellbooks:item/" + name);
            root.add("textures", textures);
            return root;
        }

        private static JsonObject scrollItemDefinition() {
            JsonObject root = new JsonObject();
            JsonObject schoolModel = new JsonObject();
            schoolModel.addProperty("type", "irons_spellbooks:school");
            schoolModel.addProperty("kind", "scroll");
            JsonObject fallback = new JsonObject();
            fallback.addProperty("type", "minecraft:range_dispatch");
            fallback.addProperty("property", "minecraft:custom_model_data");
            fallback.addProperty("index", 0);
            fallback.add("fallback", modelReference("scroll"));
            JsonArray entries = new JsonArray();
            var schools = ScrollSchoolModels.schoolOrder();
            for (String school : schools) {
                float predicateValue = ScrollSchoolModels.predicateValueForSchool(school);
                if (predicateValue <= 0f) {
                    continue;
                }
                JsonObject entry = new JsonObject();
                entry.addProperty("threshold", predicateValue);
                entry.add("model", modelReference("scroll_" + school));
                entries.add(entry);
            }
            fallback.add("entries", entries);
            schoolModel.add("fallback", fallback);
            root.add("model", schoolModel);
            return root;
        }

        private static JsonObject modelReference(String name) {
            JsonObject model = new JsonObject();
            model.addProperty("type", "minecraft:model");
            model.addProperty("model", "irons_spellbooks:item/" + name);
            return model;
        }
    }
}
//hello!! ^u^
