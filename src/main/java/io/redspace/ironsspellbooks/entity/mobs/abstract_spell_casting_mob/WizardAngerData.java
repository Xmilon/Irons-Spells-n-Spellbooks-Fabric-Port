package io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob;

import net.minecraft.core.UUIDUtil;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Per-player wizard hostility, stored in the existing angerLevels/player/anger format. */
public final class WizardAngerData {
    private final Map<UUID, Integer> levels = new HashMap<>();
    private int lastUpdate;

    public boolean contains(UUID player) { return levels.containsKey(player); }
    public int level(UUID player) { return levels.getOrDefault(player, 0); }

    public int increase(UUID player, int amount, int tick) {
        if (amount <= 0) return level(player);
        int value = (int) Math.min((long) level(player) + amount, 10);
        levels.put(player, value);
        lastUpdate = tick;
        return value;
    }

    public void increaseAll(int amount, int tick) {
        if (amount <= 0 || levels.isEmpty()) return;
        levels.replaceAll((player, level) -> (int) Math.min((long) level + amount, 10));
        lastUpdate = tick;
    }

    public void forgive(UUID player) { levels.remove(player); }

    public void decay(int tick) {
        if (levels.isEmpty() || (long) lastUpdate + 400 >= tick) return;
        levels.replaceAll((player, level) -> level - 1);
        levels.values().removeIf(level -> level <= 0);
        lastUpdate = tick;
    }

    public void save(ValueOutput output) {
        if (levels.isEmpty()) return;
        var entries = output.childrenList("angerLevels");
        levels.forEach((player, anger) -> {
            var entry = entries.addChild();
            entry.store("player", UUIDUtil.CODEC, player);
            entry.putInt("anger", anger);
        });
    }

    public void load(ValueInput input) {
        levels.clear();
        lastUpdate = 0;
        for (var entry : input.childrenListOrEmpty("angerLevels")) {
            int anger = entry.getIntOr("anger", 0);
            if (anger > 0) entry.read("player", UUIDUtil.CODEC).ifPresent(player -> levels.put(player, Math.min(anger, 10)));
        }
    }
}
