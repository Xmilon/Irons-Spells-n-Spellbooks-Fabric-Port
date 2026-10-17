package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.player.ClientPlayerEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin {
    @Inject(method = "tick", at = @At("RETURN"))
    private void irons_spellbooks$movementInputUpdate(boolean sneaking, float sneakingSpeedBonus, CallbackInfo ci) {
        var player = Minecraft.getInstance().player;
        if (player != null && player.input == (Input) (Object) this) {
            ClientPlayerEvents.onCalculatePlayerSpeed(new MovementInputUpdateEvent(player, (Input) (Object) this));
        }
    }
}
