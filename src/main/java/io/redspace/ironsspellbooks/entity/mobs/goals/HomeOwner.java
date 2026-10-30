package io.redspace.ironsspellbooks.entity.mobs.goals;

import io.redspace.ironsspellbooks.util.ValueIO;

import net.minecraft.world.level.storage.ValueOutput;

import net.minecraft.world.level.storage.ValueInput;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;

public interface HomeOwner {

    @Nullable
    BlockPos getHome();

    void setHome(BlockPos homePos);

    //fixme: self reference... (remove)
    default void serializeHome(HomeOwner self, ValueOutput out) {
        ValueIO.write(out, "home", tag -> serializeHome(self, tag));
    }

    default void deserializeHome(HomeOwner self, ValueInput in) {
        ValueIO.read(in, "home", tag -> deserializeHome(self, tag));
    }

    default void serializeHome(HomeOwner self, CompoundTag tag) {
        if (self.getHome() != null)
            tag.putIntArray("HomePos", new int[]{getHome().getX(), getHome().getY(), getHome().getZ()});
    }

    default void deserializeHome(HomeOwner self, CompoundTag tag) {
        if (tag.contains("HomePos")) {
            tag.getIntArray("HomePos").ifPresent(home -> self.setHome(new BlockPos(home[0], home[1], home[2])));
        }
    }
}
