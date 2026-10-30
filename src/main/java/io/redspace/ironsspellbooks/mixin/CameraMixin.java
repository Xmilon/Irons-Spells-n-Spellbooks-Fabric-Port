package io.redspace.ironsspellbooks.mixin;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Posts ViewportEvent.ComputeCameraAngles (camera shake) after the camera has been oriented. */
@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    private float xRot;
    @Shadow
    private float yRot;
    @Shadow
    @Final
    private Quaternionf rotation;

    @Shadow
    protected abstract void setRotation(float yRot, float xRot);

    @Inject(method = "update", at = @At("RETURN"))
    private void irons_spellbooks$computeCameraAngles(net.minecraft.client.DeltaTracker deltaTracker, CallbackInfo ci) {
        float partialTick = deltaTracker.getGameTimeDeltaPartialTick(true);
        var event = new ViewportEvent.ComputeCameraAngles((Camera) (Object) this, partialTick, this.yRot, this.xRot, 0f);
        NeoForge.EVENT_BUS.post(event);
        if (event.getYaw() != this.yRot || event.getPitch() != this.xRot) {
            this.setRotation(event.getYaw(), event.getPitch());
        }
        if (event.getRoll() != 0f) {
            this.rotation.rotateZ(event.getRoll() * ((float) Math.PI / 180f));
        }
    }
}
