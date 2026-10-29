package io.redspace.ironsspellbooks.effect;

import net.minecraft.world.entity.LivingEntity;

public interface IMobEffectEndCallback {
    /**
     * Called once per removed effect by the native living-effect lifecycle mixin.
     */
    void onEffectRemoved(LivingEntity pLivingEntity, int pAmplifier);
}
