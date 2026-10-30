package io.redspace.ironsspellbooks.mixin;

import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.world.level.portal.TeleportTransition;

@Mixin(Entity.class)
public abstract class EntityEventsMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void irons_spellbooks$entityTickPre(CallbackInfo ci) {
        NeoForge.EVENT_BUS.post(new EntityTickEvent.Pre((Entity) (Object) this));
    }

    @Shadow
    private Entity vehicle;

    @Inject(method = "stopRiding", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$dismountEvent(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (this.vehicle != null && NeoForge.EVENT_BUS.post(new EntityMountEvent(self, this.vehicle, false)).isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = "changeDimension", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$changeDimensionEvent(TeleportTransition transition, CallbackInfoReturnable<Entity> cir) {
        Entity self = (Entity) (Object) this;
        if (!self.level().isClientSide() && NeoForge.EVENT_BUS.post(new EntityTravelToDimensionEvent(self, transition.newLevel().dimension())).isCanceled()) {
            cir.setReturnValue(null);
        }
    }
}
