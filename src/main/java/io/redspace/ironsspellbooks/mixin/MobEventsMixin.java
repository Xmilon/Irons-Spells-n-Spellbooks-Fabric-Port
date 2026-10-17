package io.redspace.ironsspellbooks.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobEventsMixin {
    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$changeTargetEvent(LivingEntity target, CallbackInfo ci) {
        if (NeoForge.EVENT_BUS.post(new LivingChangeTargetEvent((Mob) (Object) this, target)).isCanceled()) {
            ci.cancel();
        }
    }
}
