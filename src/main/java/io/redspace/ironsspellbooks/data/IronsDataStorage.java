package io.redspace.ironsspellbooks.data;

import io.redspace.ironsspellbooks.capabilities.magic.PocketDimensionManager;
import io.redspace.ironsspellbooks.capabilities.magic.PortalManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.content.ContentPackManager;
import io.redspace.ironsspellbooks.content.SpellSchoolMasteryStore;
import io.redspace.ironsspellbooks.effect.guiding_bolt.GuidingBoltManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.SavedDataStorage;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.util.datafix.DataFixTypes;
import org.jetbrains.annotations.NotNull;

public class IronsDataStorage extends SavedData {
    public static IronsDataStorage INSTANCE;

    // The managers only need a registry provider for nested codecs that are not present in this data
    private static final HolderLookup.Provider PROVIDER = net.minecraft.core.RegistryAccess.EMPTY;
    private static final SavedDataType<IronsDataStorage> TYPE = new SavedDataType<>(
            net.minecraft.resources.Identifier.fromNamespaceAndPath("irons_spellbooks", "irons_spellbooks_data"),
            IronsDataStorage::new,
            CompoundTag.CODEC.xmap(tag -> IronsDataStorage.load(tag, PROVIDER), storage -> storage.save(new CompoundTag(), PROVIDER)),
            DataFixTypes.SAVED_DATA_COMMAND_STORAGE);

    public static void init(SavedDataStorage dimensionDataStorage) {
        if (dimensionDataStorage != null) {
            IronsDataStorage.INSTANCE = dimensionDataStorage.computeIfAbsent(TYPE);
        }
    }

    public @NotNull CompoundTag save(@NotNull CompoundTag pCompoundTag, HolderLookup.Provider pRegistries) {
        var tag = new CompoundTag();
        //TODO: make annotation for this?
        tag.put("GuidingBoltManager", GuidingBoltManager.INSTANCE.serializeNBT(pRegistries));
        tag.put("PortalManager", PortalManager.INSTANCE.serializeNBT(pRegistries));
        tag.put("PocketDimensionIdManager", PocketDimensionManager.INSTANCE.serializeNBT(pRegistries));
        tag.put("SummonManager", SummonManager.INSTANCE.serializeNBT(pRegistries));
        tag.put("ContentPackManager", ContentPackManager.INSTANCE.serializeNBT(pRegistries));
        tag.put("SpellSchoolMasteryStore", SpellSchoolMasteryStore.INSTANCE.serializeNBT(pRegistries));
        return tag;
    }

    public static IronsDataStorage load(CompoundTag tag, HolderLookup.Provider pRegistries) {
        //TODO: make annotation for this?
        if (tag.contains("GuidingBoltManager")) {
            GuidingBoltManager.INSTANCE.deserializeNBT(pRegistries, tag.getCompoundOrEmpty("GuidingBoltManager"));
        }
        if (tag.contains("PortalManager")) {
            PortalManager.INSTANCE.deserializeNBT(pRegistries, tag.getCompoundOrEmpty("PortalManager"));
        }
        if (tag.contains("PocketDimensionIdManager")) {
            PocketDimensionManager.INSTANCE.deserializeNBT(pRegistries, tag.getCompoundOrEmpty("PocketDimensionIdManager"));
        }
        if (tag.contains("SummonManager")) {
            SummonManager.INSTANCE.deserializeNBT(pRegistries, tag.getCompoundOrEmpty("SummonManager"));
        }
        if (tag.contains("ContentPackManager")) {
            ContentPackManager.INSTANCE.deserializeNBT(pRegistries, tag.getCompoundOrEmpty("ContentPackManager"));
        }
        if (tag.contains("SpellSchoolMasteryStore")) {
            SpellSchoolMasteryStore.INSTANCE.deserializeNBT(pRegistries, tag.getCompoundOrEmpty("SpellSchoolMasteryStore"));
        }

        return new IronsDataStorage();
    }

}


