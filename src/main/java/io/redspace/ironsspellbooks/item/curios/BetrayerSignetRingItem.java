package io.redspace.ironsspellbooks.item.curios;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.compat.TrinketsSlots;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.render.CinderousRarity;
import net.minecraft.server.level.ServerPlayer;

public class BetrayerSignetRingItem extends PassiveAbilityCurio {
    public static final int COOLDOWN_IN_TICKS = 5 * 20;

    public BetrayerSignetRingItem() {
        super(new Properties().stacksTo(1).rarity(CinderousRarity.CINDEROUS_RARITY_PROXY.getValue()).fireResistant(), TrinketsSlots.RING_SLOT);
        this.showHeader = false; // prevent generative header since we have attributes
    }

    @Override
    protected int getCooldownTicks() {
        return COOLDOWN_IN_TICKS;
    }

    public static float handleAbility(net.minecraft.world.entity.LivingEntity target, net.minecraft.world.damagesource.DamageSource source, float amount) {
        var RING = ((BetrayerSignetRingItem) ItemRegistry.SIGNET_OF_THE_BETRAYER.get());
        if (source.getEntity() instanceof ServerPlayer attackingPlayer) {
            if (RING.isEquippedBy(attackingPlayer)) {
                var victim = target;
        var victimMaxMana = AttributeRegistry.getValueOrDefault(victim, AttributeRegistry.MAX_MANA, 100.0D);
                var victimBaseMana = victim.getAttributeBaseValue(AttributeRegistry.MAX_MANA);
                if (victimMaxMana > victimBaseMana && RING.tryProcCooldown(attackingPlayer)) {
                    /*
                    If a victim's mana is above the base mana, they are considering a mage
                    Therefore, we do extra damage based on the following (informal) formula:
                        - Σ(10 - n)% for n steps of 100 mana above base
                        - ie, 10% for the first 100, 9% for the second 100, 8% for the third 100, etc
                     */
                    var manaAboveBase = victimMaxMana - victimBaseMana;
                    double conversionRatioPer100 = 0.10;
                    double totalExtraDamagePercent = 0;
                    while (manaAboveBase > 0 && conversionRatioPer100 > 0) {
                        var step = Math.clamp(manaAboveBase, 0, 100) * .01;
                        totalExtraDamagePercent += step * conversionRatioPer100;
                        manaAboveBase -= 100;
                        conversionRatioPer100 -= 0.01;
                    }
                    return (float) (amount * Math.max(1, 1 + totalExtraDamagePercent));
                }
            }
        }
        return amount;
    }
}


