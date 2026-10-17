package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.config.ClientConfigs;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$attackInput(CallbackInfoReturnable<Boolean> cir) {
        var event = new net.neoforged.neoforge.client.event.InputEvent.InteractionKeyMappingTriggered(false, true);
        io.redspace.ironsspellbooks.player.ClientInputEvents.onUseInput(event);
        if (event.isCanceled()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$useInput(CallbackInfo ci) {
        var event = new net.neoforged.neoforge.client.event.InputEvent.InteractionKeyMappingTriggered(true, false);
        io.redspace.ironsspellbooks.player.ClientInputEvents.onUseInput(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = "shouldEntityAppearGlowing", at = @At(value = "RETURN"), cancellable = true)
    public void irons_spellbooks$changeGlowOutline(Entity pEntity, CallbackInfoReturnable<Boolean> cir) {
        if (Minecraft.getInstance().player == null || pEntity == null || cir.getReturnValue()) {
            return;
        }
        if (ClientConfigs.safeGet(ClientConfigs.SUMMONS_GLOW) && ClientMagicData.getActiveSummons().contains(pEntity.getUUID())) {
            cir.setReturnValue(true);
        } else if (Minecraft.getInstance().player.hasEffect(MobEffectRegistry.PLANAR_SIGHT) && pEntity instanceof LivingEntity && Mth.abs((float) (pEntity.getY() - Minecraft.getInstance().player.getY())) < 18) {
            cir.setReturnValue(true);
        }
    }
}
