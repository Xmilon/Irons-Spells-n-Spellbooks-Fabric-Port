package io.redspace.ironsspellbooks.damage;

import io.redspace.ironsspellbooks.api.entity.NoKnockbackProjectile;
import io.redspace.ironsspellbooks.api.events.SpellDamageEvent;
import io.redspace.ironsspellbooks.api.events.LivingDamageEvents;
import io.redspace.ironsspellbooks.effect.*;
import io.redspace.ironsspellbooks.item.curios.BetrayerSignetRingItem;
import io.redspace.ironsspellbooks.item.curios.ExpulsionRing;
import io.redspace.ironsspellbooks.item.curios.LurkerRing;
import io.redspace.ironsspellbooks.player.ServerPlayerEvents;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class DamageSources {
    public static DamageSource get(Level level, ResourceKey<DamageType> damageType) {
        return level.damageSources().source(damageType);
    }

    public static Holder<DamageType> getHolderFromResource(Entity entity, ResourceKey<DamageType> damageTypeResourceKey) {
        var option = entity.level().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).get(damageTypeResourceKey);
        if (option.isPresent()) {
            return option.get();
        } else {
            return entity.level().damageSources().genericKill().typeHolder();
        }
    }

    public static boolean applyDamage(Entity target, float baseAmount, DamageSource damageSource) {
        if (!(target.level() instanceof ServerLevel serverLevel) || !Float.isFinite(baseAmount) || baseAmount < 0) return false;
        if (target instanceof LivingEntity livingTarget && damageSource instanceof SpellDamageSource spellDamageSource) {
            var e = new SpellDamageEvent(livingTarget, baseAmount, spellDamageSource);
            SpellDamageEvent.EVENT.invoker().onDamage(e);
            if (e.isCanceled()) {
                return false;
            }
            baseAmount = e.getAmount();
            if (!Float.isFinite(baseAmount) || baseAmount < 0) return false;
            float adjustedDamage = baseAmount * getResist(livingTarget, spellDamageSource.spell.getSchoolType());
            if (damageSource.getEntity() instanceof LivingEntity livingAttacker) {
                if (isFriendlyFireBetween(livingAttacker, livingTarget)) {
                    return false;
                }
                livingAttacker.setLastHurtMob(target);
            }
            if (damageSource.getDirectEntity() instanceof NoKnockbackProjectile) {
                ignoreNextKnockback(livingTarget);
            }
            return livingTarget.hurtServer(serverLevel, damageSource, adjustedDamage);
        } else {
            return target.hurtServer(serverLevel, damageSource, baseAmount);
        }
    }

    private static final HashMap<UUID, Integer> knockbackImmunes = new HashMap<>();

    public static void ignoreNextKnockback(LivingEntity livingEntity) {
        if (livingEntity.level() instanceof ServerLevel serverLevel) {
            var tickCount = serverLevel.getServer().getTickCount();
            //help manage memory
            knockbackImmunes.entrySet().removeIf(entry -> tickCount - entry.getValue() >= 10);
            //enter entity
            knockbackImmunes.put(livingEntity.getUUID(), tickCount);
        }
    }

    public static void registerNativeEvents() {
        LivingDamageEvents.ALLOW_DAMAGE.register((level, target, source, amount) -> {
            var summon = summonSource(source);
            if (summon != null && summon.getSummoner() != null && summon.getSummoner().getUUID().equals(target.getUUID())) return false;
            return ServerPlayerEvents.onLivingIncomingDamage(level, target, source, amount);
        });
        LivingDamageEvents.MODIFY_DAMAGE.register((level, target, source, amount) -> {
            var summon = summonSource(source);
            if (summon != null && summon.getSummoner() instanceof LivingEntity owner) {
                amount *= (float) AttributeRegistry.getValueOrDefault(owner, AttributeRegistry.SUMMON_DAMAGE, 1);
            }
            amount = BlightEffect.reduceDamageOutput(source, amount);
            amount = OakskinEffect.reduceDamage(target, amount);
            amount = SpiderAspectEffect.increaseDamage(target, source, amount);
            TrueInvisibilityEffect.onDealDamage(source);
            amount = BetrayerSignetRingItem.handleAbility(target, source, amount);
            ExpulsionRing.handleAbility(target, source, amount);
            return LurkerRing.handleAbility(target, source, amount);
        });
        LivingDamageEvents.MODIFY_HEALTH_DAMAGE.register(ServerPlayerEvents::onBeforeDamageTaken);
        LivingDamageEvents.AFTER_DAMAGE.register((level, target, source, amount) -> {
            if (source instanceof SpellDamageSource spell) {
                SpellHitEffects.apply(target, source.getEntity(), amount, spell.getLifestealPercent(), spell.getFreezeTicks(), spell.getFireTime());
            }
            if (amount > 0) EchoingStrikesEffect.createEcho(target, source, amount);
        });
        LivingDamageEvents.AFTER_HIT.register((level, target, source, amount) -> {
            if (source instanceof SpellDamageSource spell && spell.getIFrames() >= 0) target.damageCooldownTime = spell.getIFrames();
        });
        LivingDamageEvents.ALLOW_KNOCKBACK.register((target, source) -> !cancelKnockback(target));
        LivingDamageEvents.MODIFY_HEALING.register(BlightEffect::reduceHealing);
        LivingDamageEvents.MODIFY_HEALING.register(SoulBurnMobEffect::soulBurnReduceHealing);
        LivingDamageEvents.FINISH_USING_ITEM.register(GluttonyEffect::finishEating);
    }

    private static @Nullable IMagicSummon summonSource(DamageSource source) {
        return source.getDirectEntity() instanceof IMagicSummon summon ? summon
                : source.getEntity() instanceof IMagicSummon summon ? summon : null;
    }

    private static boolean cancelKnockback(LivingEntity entity) {
        if (!(entity.level() instanceof ServerLevel level)) return false;
        var tick = knockbackImmunes.remove(entity.getUUID());
        return tick != null && level.getServer().getTickCount() - tick <= 1;
    }
    public static boolean isFriendlyFireBetween(@Nullable Entity attacker, @Nullable Entity target) {
        if (attacker == null || target == null) {
            return false;
        }
        if (attacker instanceof IMagicSummon summon) {
            var tmp = summon.getSummoner();
            if (tmp != null) {
                attacker = tmp;
            }
        }
        if (target instanceof IMagicSummon summon) {
            var tmp = summon.getSummoner();
            if (tmp != null) {
                target = tmp;
            }
        }
        if (attacker.isPassengerOfSameVehicle(target)) {
            return true;
        }
        if (attacker instanceof Player playerAttacker && target instanceof Player playertarget
                && !playerAttacker.canHarmPlayer(playertarget)) {
            return true;
        }
        var team = attacker.getTeam();
        if (team != null) {
            return team.isAlliedTo(target.getTeam()) && !team.isAllowFriendlyFire();
        }
        //We already manually checked for teams, so this will only return true for any overrides (such as summons)
        return attacker.isAlliedTo(target);
    }

    /**
     * Returns the resistance multiplier of the entity. (If they are resistant, the value is < 1)
     */
    public static float getResist(LivingEntity entity, SchoolType damageSchool) {
            var baseResist = AttributeRegistry.getValueOrDefault(entity, AttributeRegistry.SPELL_RESIST, 1.0D);
        if (damageSchool == null)
            return 2 - (float) Utils.softCapFormula(baseResist);
        else
            return 2 - (float) Utils.softCapFormula(damageSchool.getResistanceFor(entity) * baseResist);
    }
}


