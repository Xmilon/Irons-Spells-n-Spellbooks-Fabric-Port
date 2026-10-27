package io.redspace.ironsspellbooks.entity.spells;

import net.minecraft.world.level.storage.ValueOutput;

import net.minecraft.world.level.storage.ValueInput;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.neoforged.neoforge.entity.PartEntity;


public class ConePart extends PartEntity<AbstractConeProjectile> {

    public final AbstractConeProjectile parentEntity;
    public final String name;
    private final EntityDimensions size;

    public ConePart(AbstractConeProjectile coneProjectile, String name, float scaleX, float scaleY) {
        super(coneProjectile);
        this.size = EntityDimensions.scalable(scaleX, scaleY);
        this.refreshDimensions();
        this.parentEntity = coneProjectile;
        this.name = name;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
    }

    @Override
    protected void readAdditionalSaveData(ValueInput compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(ValueOutput compoundTag) {

    }

    @Override
    public boolean is(Entity entity) {
        return this == entity || this.parentEntity == entity;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return this.size;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }
}
