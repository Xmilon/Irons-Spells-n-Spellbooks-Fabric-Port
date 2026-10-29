package io.redspace.ironsspellbooks.entity.mobs.goals;

import io.redspace.ironsspellbooks.entity.mobs.SupportMob;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.zombie.Zombie;

import java.util.List;

/** Real server fixtures exercise the production goals without loading the incomplete main mod. */
public final class TargetingPortProbe implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            var level = server.overworld();
            level.getChunk(0, 0);
            var healer = new SupportZombie(level);
            var near = new Zombie(level);
            var far = new Zombie(level);
            var healthy = new Zombie(level);
            var mobs = List.of(healer, near, far, healthy);
            try {
                for (int i = 0; i < mobs.size(); i++) {
                    var mob = mobs.get(i);
                    mob.setNoAi(true);
                    mob.snapTo(4 + i * 2, 100, 4);
                    require(level.addFreshEntity(mob), "Could not add fixture mob");
                }
                healer.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(8);
                near.setHealth(4);
                far.setHealth(6);
                var goal = new FindSupportableTargetGoal<>(healer, Zombie.class, false,
                        (candidate, suppliedLevel) -> suppliedLevel == level && candidate != healer
                                && candidate.getHealth() < candidate.getMaxHealth());
                healer.getRandom().setSeed(1234);
                boolean acquired = false;
                for (int i = 0; i < 200 && !acquired; i++) acquired = goal.canUse();
                require(acquired, "No injured support target acquired");
                healer.setTarget(far);
                goal.start();
                require(healer.getSupportTarget() == near, "Did not choose nearest eligible support target");
                require(healer.getTarget() == null, "Healing target became combat target");
                healer.tickCount = 20;
                require(goal.canContinueToUse(), "Eligible support target failed recheck");
                near.setHealth(near.getMaxHealth());
                require(!goal.canContinueToUse(), "Recovered support target stayed eligible");
                healer.setTarget(far);
                require(healer.getTarget() == far, "Fixture combat target was rejected before stop");
                goal.stop();
                require(healer.getSupportTarget() == null, "Stopped goal retained support target");
                require(healer.getTarget() == far, "Stopping support erased a newly acquired combat target");
                require(!goal.canContinueToUse(), "Stopped goal continued");

                var village = new GenericDefendVillageTargetGoal(healer);
                var previous = GenericDefendVillageTargetGoal.class.getDeclaredField("potentialTarget");
                previous.setAccessible(true);
                previous.set(village, near);
                require(!village.canUse(), "Village defence reused stale target without witnesses");

                var revenge = new GenericHurtByTargetGoal(healer, target -> target == near);
                healer.tickCount = 30;
                healer.setLastHurtByMob(near);
                require(!revenge.canUse(), "Revenge selected excluded attacker");
                var allowedRevenge = new GenericHurtByTargetGoal(healer, target -> false);
                require(allowedRevenge.canUse(), "Valid revenge attacker rejected");
                System.out.println("Native targeting server checks passed: nearest support, selector context, recovery, stop/combat isolation, stale village target and revenge filters");
                healer.needsSync = false;
                healer.push(.1, 0, 0);
                require(healer.needsSync, "Native impulse did not request synchronization");
                healer.setInvulnerableTime(10);
                healer.commonTick();
                require(healer.getInvulnerableTime() == 9, "Native common tick did not decrement invulnerability exactly once");
                System.out.println("Native entity API server checks passed: impulse synchronization and one-tick invulnerability countdown");
            } catch (ReflectiveOperationException exception) {
                throw new AssertionError(exception);
            } finally {
                mobs.forEach(LivingEntity::discard);
            }
            server.halt(false);
        });
    }

    private static final class SupportZombie extends Zombie implements SupportMob {
        private LivingEntity supportTarget;
        SupportZombie(ServerLevel level) { super(level); }
        @Override public LivingEntity getSupportTarget() { return supportTarget; }
        @Override public void setSupportTarget(LivingEntity target) { supportTarget = target; }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
