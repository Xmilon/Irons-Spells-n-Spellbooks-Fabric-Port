package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.player.ClientPlayerEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
// extends ClientInput: moveVector lives there, and a mixin can only reach superclass fields by inheriting them
public abstract class KeyboardInputMixin extends ClientInput {

    /** Slows the local player's movement input while casting. */
    @Inject(method = "tick", at = @At("RETURN"))
    private void irons_spellbooks$movementInputUpdate(CallbackInfo ci) {
        var player = Minecraft.getInstance().player;
        if (player != null && player.input == (Object) this) {
            float speed = ClientPlayerEvents.castingMovementMultiplier(player);
            if (speed != 1f) {
                this.moveVector = new Vec2(this.moveVector.x * speed, this.moveVector.y * speed);
            }
        }
    }
}
