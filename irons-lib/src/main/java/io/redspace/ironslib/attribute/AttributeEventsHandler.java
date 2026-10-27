package io.redspace.ironslib.attribute;

import io.redspace.ironslib.registry.IronsLibRegistries;
import io.redspace.ironslib.util.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;
import org.jspecify.annotations.Nullable;

public final class AttributeEventsHandler {
   public static boolean shouldDodge(LivingEntity target) {
      if (target.level().isClientSide()
         || !(target.getRandom().nextDouble() < target.getAttributeValue(IronsLibRegistries.AttributeRegistry.DODGE_CHANCE) - 1.0)) {
         return false;
      }
      target.level().playSound(null, target.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, target.getSoundSource(), 1.5F, 2.0F);
      Utils.spawnParticles(
         target.level(), ParticleTypes.CAMPFIRE_COSY_SMOKE,
         target.getX(), target.getY() + target.getBbHeight() * 0.5F, target.getZ(),
         15, 0.2, 0.4, 0.2, 0.01, false
      );
      target.damageCooldownTime = 20;
      return true;
   }

   public static float modifyProjectileDamage(DamageSource source, float damage) {
      if (source.getEntity() instanceof LivingEntity attacker) {
         if (source.getDirectEntity() instanceof AbstractArrow) {
            double arrowDamage = attacker.getAttributeValue(IronsLibRegistries.AttributeRegistry.ARROW_DAMAGE);
            if (arrowDamage != 1.0 && arrowDamage >= 0.0) {
               damage *= (float) arrowDamage;
            }
         }
         if (source.getDirectEntity() instanceof Projectile) {
            double projectileDamage = attacker.getAttributeValue(IronsLibRegistries.AttributeRegistry.PROJECTILE_DAMAGE);
            if (projectileDamage != 1.0) {
               damage *= (float) projectileDamage;
            }
         }
      }
      return damage;
   }

   public static float modifyCriticalMultiplier(LivingEntity attacker, float multiplier) {
      if (!attacker.level().isClientSide()) {
         double criticalDamage = attacker.getAttributeValue(IronsLibRegistries.AttributeRegistry.CRIT_DAMAGE);
         if (criticalDamage != 1.0) {
            return (float) (multiplier + criticalDamage - 1.0);
         }
      }
      return multiplier;
   }

   public static float modifyMiningSpeed(LivingEntity miner, float speed) {
      double miningSpeed = miner.getAttributeValue(IronsLibRegistries.AttributeRegistry.MINING_SPEED);
      return miningSpeed == 1.0 ? speed : (float) (speed * miningSpeed);
   }

   public static float modifyHealing(LivingEntity target, float healing) {
      double healingReceived = target.getAttributeValue(IronsLibRegistries.AttributeRegistry.HEALING_RECEIVED);
      return healingReceived == 1.0 ? healing : (float) (healing * healingReceived);
   }

   public static int modifyExperience(@Nullable Entity recipient, int experience) {
      if (experience > 0 && recipient instanceof LivingEntity livingEntity) {
         double bonus = livingEntity.getAttributeValue(IronsLibRegistries.AttributeRegistry.EXPERIENCE_GAINED);
         return bonus == 1.0 ? experience : (int) Math.max(0.0, experience * bonus + 0.5);
      }
      return experience;
   }

   public static ItemAttributeModifiers remapModifiers(ItemAttributeModifiers modifiers) {
      List<Entry> remapped = null;
      for (Entry entry : modifiers.modifiers()) {
         Holder<Attribute> target = AttributeRemapRegistry.findTarget(entry.attribute()).orElse(null);
         if (target != null) {
            if (remapped == null) {
               remapped = new ArrayList<>(modifiers.modifiers());
            }
            remapped.removeIf(existing ->
               (existing.attribute().equals(entry.attribute()) || existing.attribute().equals(target))
                  && existing.modifier().id().equals(entry.modifier().id())
            );
            remapped.add(new Entry(target, entry.modifier(), entry.slot(), entry.display()));
         }
      }
      return remapped == null ? modifiers : new ItemAttributeModifiers(List.copyOf(remapped));
   }

   public static void handleAttributeEvent(
      AttributeInstance entry, BiConsumer<Holder<Attribute>, AttributeModifier> remove, BiConsumer<Holder<Attribute>, AttributeModifier> add
   ) {
      AttributeRemapRegistry.findTarget(entry.attribute()).ifPresent(target -> {
         remove.accept(entry.attribute(), entry.modifier());
         add.accept(target, entry.modifier());
      });
   }
}
