package io.redspace.ironsspellbooks.entity.mobs.goals;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.WizardAngerData;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;

import java.util.UUID;

public final class WizardAngerDataCheck {
    public static void main(String[] args) {
        var alice = UUID.fromString("00000000-0000-0000-0000-000000000001");
        var bob = UUID.fromString("00000000-0000-0000-0000-000000000002");
        var data = new WizardAngerData();
        assert data.increase(alice, 1, 0) == 1;
        assert data.contains(alice) && !data.contains(bob);
        assert data.increase(alice, 1, 10) == 2 : "Hostility threshold increment changed";
        assert data.increase(alice, Integer.MAX_VALUE, 11) == 10 : "Anger overflowed its cap";
        assert data.increase(alice, -1, 12) == 10 : "Negative anger changed state";
        assert data.increase(bob, 3, 15) == 3;
        data.decay(415);
        assert data.level(bob) == 3 : "Decay fired before the original 400-tick interval";
        data.decay(416);
        assert data.level(alice) == 9 && data.level(bob) == 2;
        data.decay(416);
        assert data.level(bob) == 2 : "Decay fired twice in one tick";
        data.decay(817);
        data.decay(1218);
        assert !data.contains(bob) && data.level(alice) == 7 : "Expired anger did not clear";

        var output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        data.save(output);
        var saved = output.buildResult();
        var rows = saved.getListOrEmpty("angerLevels");
        assert rows.size() == 1 : "Saved anger row count changed";
        var row = (CompoundTag) rows.getFirst();
        assert row.read("player", UUIDUtil.CODEC).orElseThrow().equals(alice);
        assert row.getIntOr("anger", -1) == 7 : "Legacy anger field changed";
        var restored = new WizardAngerData();
        restored.load(TagValueInput.create(ProblemReporter.DISCARDING, RegistryAccess.EMPTY, saved));
        assert restored.level(alice) == 7;
        restored.increase(bob, 1, 0);
        restored.load(TagValueInput.create(ProblemReporter.DISCARDING, RegistryAccess.EMPTY, saved));
        assert !restored.contains(bob) : "Reload retained stale per-player anger";

        var invalid = new CompoundTag();
        var mixed = new ListTag();
        var malformed = new CompoundTag();
        malformed.putString("player", "bad uuid");
        malformed.putInt("anger", 5);
        mixed.add(malformed);
        mixed.add(row.copy());
        var negative = new CompoundTag();
        negative.store("player", UUIDUtil.CODEC, bob);
        negative.putInt("anger", -2);
        mixed.add(negative);
        invalid.put("angerLevels", mixed);
        restored.load(TagValueInput.create(ProblemReporter.DISCARDING, RegistryAccess.EMPTY, invalid));
        assert restored.level(alice) == 7 && !restored.contains(bob) : "Malformed row erased valid anger or accepted invalid anger";
        restored.increaseAll(2, 50);
        assert restored.level(alice) == 9 : "Bulk anger ignored requested amount";
        restored.forgive(alice);
        assert !restored.contains(alice) : "Forgiveness retained player anger";
        restored.load(TagValueInput.create(ProblemReporter.DISCARDING, RegistryAccess.EMPTY, new CompoundTag()));
        assert restored.level(alice) == 0 : "Empty reload retained anger";
        System.out.println("Wizard anger checks passed: thresholds/cap, overflow, decay, legacy save shape, reload isolation, malformed rows and forgiveness");
    }
}
