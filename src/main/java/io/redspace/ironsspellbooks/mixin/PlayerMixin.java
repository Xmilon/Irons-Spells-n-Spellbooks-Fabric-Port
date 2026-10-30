package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.item.armor.IDisableHat;
import io.redspace.ironsspellbooks.item.armor.IDisableJacket;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void irons_spellbooks$playerTickPre(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.tick.PlayerTickEvent.Pre((Player) (Object) this));
    }

    @Inject(method = "canEat", at = @At(value = "RETURN"), cancellable = true)
    void canEatForGluttony(boolean pCanAlwaysEat, CallbackInfoReturnable<Boolean> cir) {
        if (((Player) (Object) this).hasEffect(MobEffectRegistry.GLUTTONY)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "tryToStartFallFlying", at = @At(value = "HEAD"), cancellable = true)
    void irons_spellbooks$tryToStartAngelWingsFallFlying(CallbackInfoReturnable<Boolean> cir) {
        var self = (Player) (Object) this;
        if (!self.hasEffect(MobEffectRegistry.ANGEL_WINGS)) {
            return;
        }
        // Match vanilla start conditions, but allow Angel Wings effect as elytra source.
        if (!self.onGround() && !self.isFallFlying() && !self.isInWater() && !self.hasEffect(MobEffects.LEVITATION)) {
            ((PlayerAccessor) self).irons_spellbooks$startFallFlying();
            cir.setReturnValue(true);
        }
    }
}
