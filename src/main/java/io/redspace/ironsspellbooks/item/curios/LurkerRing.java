package io.redspace.ironsspellbooks.item.curios;

import io.redspace.ironsspellbooks.compat.TrinketsSlots;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class LurkerRing extends PassiveAbilityCurio {
    public static final int COOLDOWN_IN_TICKS = 15 * 20;
    public static final float MULTIPLIER = 1.5f;

    public LurkerRing() {
        super(new Properties().stacksTo(1), TrinketsSlots.RING_SLOT);
    }

    @Override
    public Component getDescription(ItemStack stack) {
        return Component.literal(" ").append(Component.translatable(this.getDescriptionId() + ".desc",
                (int) ((MULTIPLIER - 1) * 100)
        )).withStyle(descriptionStyle);
    }

    @Override
    protected int getCooldownTicks() {
        return COOLDOWN_IN_TICKS;
    }

    public static float handleAbility(net.minecraft.world.entity.LivingEntity target, net.minecraft.world.damagesource.DamageSource source, float amount) {
        var RING = ((LurkerRing) ItemRegistry.LURKER_RING.get());
        if (source.getEntity() instanceof ServerPlayer attackingPlayer) {
            if (attackingPlayer.isInvisible() && RING.isEquippedBy(attackingPlayer) && RING.tryProcCooldown(attackingPlayer)) {
                return amount * MULTIPLIER;
            }
        }
        return amount;
    }
}


