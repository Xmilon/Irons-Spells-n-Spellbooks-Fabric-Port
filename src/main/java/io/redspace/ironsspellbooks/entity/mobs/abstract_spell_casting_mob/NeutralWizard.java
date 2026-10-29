package io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class NeutralWizard extends AbstractSpellCastingMob implements NeutralMob {
    protected NeutralWizard(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    private long persistentAngerEndTime = NeutralMob.NO_ANGER_END_TIME;
    @Nullable private EntityReference<LivingEntity> persistentAngerTarget;
    private final WizardAngerData anger = new WizardAngerData();

    @Override
    public void startPersistentAngerTimer() {
        setTimeToRemainAngry(PERSISTENT_ANGER_TIME.sample(random));
    }

    @Override
    public long getPersistentAngerEndTime() { return persistentAngerEndTime; }

    @Override
    public void setPersistentAngerEndTime(long time) { persistentAngerEndTime = time; }

    @Override
    public void setPersistentAngerTarget(@Nullable EntityReference<LivingEntity> target) { persistentAngerTarget = target; }

    @Override
    @Nullable
    public EntityReference<LivingEntity> getPersistentAngerTarget() { return persistentAngerTarget; }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        addPersistentAngerSaveData(output);
        anger.save(output);
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        anger.load(input);
        readPersistentAngerSaveData(level(), input);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level() instanceof ServerLevel serverLevel) {
            updatePersistentAnger(serverLevel, true);
            anger.decay(tickCount);
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (source.getEntity() instanceof Player player && !player.isCreative()) {
            increaseAngerLevel(player, (int) Math.ceil(amount), !isHostileTowards(player, level));
        }
        return super.hurtServer(level, source, amount);
    }

    public void increaseAngerLevel(Player player, int levels, boolean showParticles) {
        if (!(level() instanceof ServerLevel serverLevel) || levels <= 0) return;
        int value = anger.increase(player.getUUID(), levels, tickCount);
        if (value < getAngerThreshold() && showParticles) {
            MagicManager.spawnParticles(serverLevel, ParticleTypes.ANGRY_VILLAGER, getX(), getY() + 1.25, getZ(), 15, .3, .2, .3, 0, false);
            getAngerSound().ifPresent(sound -> playSound(sound, getSoundVolume(), getVoicePitch()));
        }
        if (value >= getAngerThreshold()) setPersistentAngerTarget(EntityReference.of(player));
    }

    @Deprecated
    public void increaseAngerLevel(int levels, boolean showParticles) {
        IronsSpellbooks.LOGGER.warn("Warning! Use of deprecated NeutralWizard#increaseAngerLevel");
        if (level() instanceof ServerLevel) anger.increaseAll(levels, tickCount);
    }

    public Optional<SoundEvent> getAngerSound() { return Optional.empty(); }
    public int getAngerThreshold() { return 2; }

    public boolean isHostileTowards(LivingEntity entity, ServerLevel level) {
        return isAngryAt(entity, level) && (!(entity instanceof Player) || anger.level(entity.getUUID()) >= getAngerThreshold());
    }

    @Override
    public boolean isAngryAt(LivingEntity target, ServerLevel level) {
        return (target instanceof Player && anger.contains(target.getUUID())) || NeutralMob.super.isAngryAt(target, level);
    }

    public boolean guardsBlocks() { return true; }

    @Override
    public void readPersistentAngerSaveData(Level level, ValueInput input) {
        NeutralMob.super.readPersistentAngerSaveData(level, input);
        if (level instanceof ServerLevel serverLevel) {
            if (persistentAngerTarget == null) {
                input.read("AngryAt", UUIDUtil.CODEC).ifPresent(uuid -> setPersistentAngerTarget(EntityReference.of(uuid)));
            }
            if (getTarget() instanceof Player player && !isHostileTowards(player, serverLevel)) setTarget(null);
        }
    }

    @Override
    public void playerDied(ServerLevel level, Player player) {
        NeutralMob.super.playerDied(level, player);
        if (level.getGameRules().get(GameRules.FORGIVE_DEAD_PLAYERS)) anger.forgive(player.getUUID());
    }
}
