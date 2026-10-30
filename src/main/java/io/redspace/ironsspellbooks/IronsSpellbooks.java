package io.redspace.ironsspellbooks;

import com.mojang.logging.LogUtils;
import io.redspace.ironsspellbooks.api.config.SpellConfigManager;
import io.redspace.ironsspellbooks.api.magic.MagicHelper;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.config.ClientConfigs;
import io.redspace.ironsspellbooks.config.ConfigBootstrap;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.registries.*;
import io.redspace.ironsspellbooks.setup.CommonSetup;
import io.redspace.ironsspellbooks.setup.ModSetup;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class IronsSpellbooks implements ModInitializer {
    // Directly reference a slf4j logger
    public static final String MODID = "irons_spellbooks";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static MagicManager MAGIC_MANAGER;

    public static MinecraftServer MCS;
    public static ServerLevel OVERWORLD;

    @Override
    public void onInitialize() {
        ConfigBootstrap.registerConfigs();
        ModSetup.setup();

        MAGIC_MANAGER = new MagicManager();
        MagicHelper.MAGIC_MANAGER = MAGIC_MANAGER;

        // NeoForge custom registry events do not exist on Fabric; bootstrap these registries eagerly.
        SchoolRegistry.bootstrapRegistry();
        SpellRegistry.bootstrapRegistry();

        var eventBus = NeoForge.EVENT_BUS;
        SchoolRegistry.register(eventBus);
        SpellRegistry.register(eventBus);
        ItemRegistry.register(eventBus);
        AttributeRegistry.register();
        BlockRegistry.register(eventBus);
        MenuRegistry.register(eventBus);
        EntityRegistry.register(eventBus);
        LootRegistry.register(eventBus);
        MobEffectRegistry.register(eventBus);
        ParticleRegistry.register();
        SoundRegistry.register(eventBus);
        FeatureRegistry.register();
        PotionRegistry.register(eventBus);
        CommandArgumentRegistry.register(eventBus);
        StructureProcessorRegistry.register();
        StructureElementRegistry.register(eventBus);
        CreativeTabRegistry.register(eventBus);
        DataAttachmentRegistry.register(eventBus);

        ComponentRegistry.register();
        PoiTypeRegistry.register(eventBus);
        FluidRegistry.register(eventBus);
        RecipeRegistry.register(eventBus);
        CommandRegistry.bootstrapFabric();

        CommonSetup.bootstrapFabric();
        SpellConfigManager.INSTANCE = new SpellConfigManager();

        // Keep eager class init to preserve defaults and side effects.
        ClientConfigs.SPEC.toString();
        ServerConfigs.SPEC.toString();

        net.fabricmc.loader.api.FabricLoader.getInstance()
                .getEntrypointContainers("irons_spellbooks:addon", io.redspace.ironsspellbooks.api.IronsSpellbooksAddon.class)
                .forEach(container -> container.getEntrypoint().onInitialize());
    }

    public static Identifier id(@NotNull String path) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, path);
    }
}
