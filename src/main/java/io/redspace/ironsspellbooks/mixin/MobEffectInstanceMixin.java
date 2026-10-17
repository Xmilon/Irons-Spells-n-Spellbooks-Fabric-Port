package io.redspace.ironsspellbooks.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

// Our DeferredHolder shim is not a Holder.Reference, so saving an instance built from it throws "Unregistered holder" (crashes player save)
@Mixin(MobEffectInstance.class)
public class MobEffectInstanceMixin {
    @ModifyVariable(method = "<init>(Lnet/minecraft/core/Holder;IIZZZLnet/minecraft/world/effect/MobEffectInstance;)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private static Holder<MobEffect> irons_spellbooks$useRegisteredHolder(Holder<MobEffect> holder) {
        return holder instanceof Holder.Reference<MobEffect> ? holder : BuiltInRegistries.MOB_EFFECT.wrapAsHolder(holder.value());
    }
}
