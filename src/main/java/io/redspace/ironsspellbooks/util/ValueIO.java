package io.redspace.ironsspellbooks.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.function.Consumer;

/**
 * Bridges legacy CompoundTag based (de)serializers to the ValueInput/ValueOutput save API by nesting their data under a key.
 * Saves are not layout compatible with the 1.21 mod, which matches the fresh-world target of this port.
 */
public final class ValueIO {
    private ValueIO() {
    }

    public static void write(ValueOutput out, String key, Consumer<CompoundTag> writer) {
        CompoundTag tag = new CompoundTag();
        writer.accept(tag);
        out.store(key, CompoundTag.CODEC, tag);
    }

    public static void read(ValueInput in, String key, Consumer<CompoundTag> reader) {
        in.read(key, CompoundTag.CODEC).ifPresent(reader);
    }
}
