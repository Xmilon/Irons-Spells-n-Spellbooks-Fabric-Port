package io.redspace.ironsspellbooks.entity.mobs.goals;

import io.redspace.ironsspellbooks.entity.mobs.SupportMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class FindSupportableTargetGoal<M extends Mob & SupportMob> extends TargetGoal {
    private final SupportMob supportMob;
    private final Class<? extends LivingEntity> targetType;
    private final TargetingConditions supportConditions;
    private final int randomInterval = reducedTickDelay(40);
    private LivingEntity target;

    public FindSupportableTargetGoal(M mob, Class<? extends LivingEntity> targetType, boolean mustSee,
                                     TargetingConditions.Selector selector) {
        super(mob, mustSee, false);
        this.supportMob = mob;
        this.targetType = targetType;
        this.supportConditions = TargetingConditions.forNonCombat().range(getFollowDistance() * 2).selector(selector);
        setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (randomInterval > 0 && mob.getRandom().nextInt(randomInterval) != 0) return false;
        var level = getServerLevel(mob);
        var candidates = level.getEntitiesOfClass(targetType, mob.getBoundingBox().inflate(getFollowDistance()), entity -> true);
        target = level.getNearestEntity(candidates, supportConditions, mob, mob.getX(), mob.getEyeY(), mob.getZ());
        return target != null;
    }

    @Override
    public void start() {
        supportMob.setSupportTarget(target);
        mob.setTarget(null);
        super.start();
    }

    @Override
    public boolean canContinueToUse() {
        var supportTarget = supportMob.getSupportTarget();
        return supportTarget != null && (mob.tickCount % 20 != 0
                || supportConditions.test(getServerLevel(mob), mob, supportTarget));
    }

    @Override
    public void stop() {
        supportMob.setSupportTarget(null);
        target = null;
        targetMob = null;
    }
}
