package io.redspace.ironsspellbooks.content;

import io.redspace.ironsspellbooks.data.IronsDataStorage;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SpellSchoolMasteryStore {
    public static final SpellSchoolMasteryStore INSTANCE = new SpellSchoolMasteryStore();

    private static final String PLAYERS_TAG = "Players";
    private static final String PLAYER_UUID = "Uuid";
    private static final String PLAYER_NAME = "Name";
    private static final String CASTS_TAG = "Casts";
    private static final String BONUSES_TAG = "Bonuses";

    private final Map<UUID, PlayerMasteryData> dataByPlayer = new HashMap<>();

    private SpellSchoolMasteryStore() {
    }

    public PlayerMasteryData getOrCreate(UUID uuid, String name) {
        PlayerMasteryData data = dataByPlayer.computeIfAbsent(uuid, ignored -> new PlayerMasteryData());
        if (name != null && !name.isBlank()) {
            data.lastKnownName = name;
        }
        return data;
    }

    public PlayerMasteryData get(UUID uuid) {
        return dataByPlayer.get(uuid);
    }

    public boolean ensureSchoolInitialized(UUID uuid, String name, String schoolId) {
        if (schoolId == null || schoolId.isBlank()) {
            return false;
        }
        PlayerMasteryData data = getOrCreate(uuid, name);
        boolean changed = false;
        if (!data.castCounts.containsKey(schoolId)) {
            data.castCounts.put(schoolId, 0);
            changed = true;
        }
        if (!data.powerBonuses.containsKey(schoolId)) {
            data.powerBonuses.put(schoolId, 0d);
            changed = true;
        }
        if (changed) {
            markDirty();
        }
        return changed;
    }

    public int incrementCast(UUID uuid, String name, String schoolId) {
        PlayerMasteryData data = getOrCreate(uuid, name);
        int next = data.castCounts.getOrDefault(schoolId, 0) + 1;
        data.castCounts.put(schoolId, next);
        markDirty();
        return next;
    }

    public double addBonus(UUID uuid, String name, String schoolId, double delta) {
        PlayerMasteryData data = getOrCreate(uuid, name);
        double next = data.powerBonuses.getOrDefault(schoolId, 0d) + delta;
        if (next < 0d) {
            next = 0d;
        }
        data.powerBonuses.put(schoolId, next);
        markDirty();
        return next;
    }

    public int getCastCount(UUID uuid, String schoolId) {
        PlayerMasteryData data = dataByPlayer.get(uuid);
        if (data == null) {
            return 0;
        }
        return data.castCounts.getOrDefault(schoolId, 0);
    }

    public double getBonus(UUID uuid, String schoolId) {
        PlayerMasteryData data = dataByPlayer.get(uuid);
        if (data == null) {
            return 0d;
        }
        return data.powerBonuses.getOrDefault(schoolId, 0d);
    }

    public Map<UUID, PlayerMasteryData> getAll() {
        return dataByPlayer;
    }

    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag root = new CompoundTag();
        ListTag players = new ListTag();
        for (var entry : dataByPlayer.entrySet()) {
            CompoundTag playerTag = new CompoundTag();
            playerTag.store(PLAYER_UUID, net.minecraft.core.UUIDUtil.CODEC, entry.getKey());
            PlayerMasteryData data = entry.getValue();
            if (data.lastKnownName != null && !data.lastKnownName.isBlank()) {
                playerTag.putString(PLAYER_NAME, data.lastKnownName);
            }
            CompoundTag casts = new CompoundTag();
            for (var castEntry : data.castCounts.entrySet()) {
                casts.putInt(castEntry.getKey(), castEntry.getValue());
            }
            playerTag.put(CASTS_TAG, casts);

            CompoundTag bonuses = new CompoundTag();
            for (var bonusEntry : data.powerBonuses.entrySet()) {
                bonuses.putDouble(bonusEntry.getKey(), bonusEntry.getValue());
            }
            playerTag.put(BONUSES_TAG, bonuses);

            players.add(playerTag);
        }
        root.put(PLAYERS_TAG, players);
        return root;
    }

    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag root) {
        dataByPlayer.clear();
        if (!root.contains(PLAYERS_TAG)) {
            return;
        }
        ListTag players = root.getListOrEmpty(PLAYERS_TAG);
        for (Tag entry : players) {
            if (!(entry instanceof CompoundTag playerTag)) continue;
            if (!playerTag.contains(PLAYER_UUID)) {
                continue;
            }
            UUID uuid = playerTag.read(PLAYER_UUID, net.minecraft.core.UUIDUtil.CODEC).orElse(null);
            if (uuid == null) continue;
            PlayerMasteryData data = new PlayerMasteryData();
            if (playerTag.contains(PLAYER_NAME)) {
                data.lastKnownName = playerTag.getStringOr(PLAYER_NAME, "");
            }
            if (playerTag.contains(CASTS_TAG)) {
                CompoundTag casts = playerTag.getCompoundOrEmpty(CASTS_TAG);
                for (String key : casts.keySet()) {
                    data.castCounts.put(key, casts.getIntOr(key, 0));
                }
            }
            if (playerTag.contains(BONUSES_TAG)) {
                CompoundTag bonuses = playerTag.getCompoundOrEmpty(BONUSES_TAG);
                for (String key : bonuses.keySet()) {
                    data.powerBonuses.put(key, bonuses.getDoubleOr(key, 0d));
                }
            }
            dataByPlayer.put(uuid, data);
        }
    }

    private static void markDirty() {
        if (IronsDataStorage.INSTANCE != null) {
            IronsDataStorage.INSTANCE.setDirty();
        }
    }

    public static class PlayerMasteryData {
        private String lastKnownName;
        private final Map<String, Integer> castCounts = new HashMap<>();
        private final Map<String, Double> powerBonuses = new HashMap<>();

        public String getLastKnownName() {
            return lastKnownName;
        }
    }
}
