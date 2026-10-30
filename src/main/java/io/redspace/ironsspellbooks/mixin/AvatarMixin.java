package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.item.armor.IDisableHat;
import io.redspace.ironsspellbooks.item.armor.IDisableJacket;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.entity.Avatar;

@Mixin(Avatar.class)
public class AvatarMixin {
    @Inject(method = "isModelPartShown", at = @At(value = "RETURN"), cancellable = true)
    void irons_spellbooks$hideJacketLayers(PlayerModelPart part, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            var self = (net.minecraft.world.entity.Avatar) (Object) this;
            switch (part) {
                case PlayerModelPart.HAT:
                    cir.setReturnValue(!(self.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof IDisableHat));
                    break;
                case JACKET:
                case LEFT_SLEEVE:
                case RIGHT_SLEEVE:
                    if (self.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof IDisableJacket chestplate && chestplate.disableForSlot(EquipmentSlot.CHEST)) {
                        cir.setReturnValue(false);
                    }
                    break;
                case LEFT_PANTS_LEG:
                case RIGHT_PANTS_LEG:
                    if ((self.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof IDisableJacket leggings && leggings.disableForSlot(EquipmentSlot.LEGS))
                            || (self.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof IDisableJacket boots && boots.disableForSlot(EquipmentSlot.FEET))) {
                        cir.setReturnValue(false);
                    }
                    break;
            }
        }
    }
}
