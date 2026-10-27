package io.redspace.ironslib.internal.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironslib.kinetic_weapon.KineticAnimation;
import io.redspace.ironslib.kinetic_weapon.client.KineticAnimations;
import net.minecraft.client.model.effects.SpearAnimations;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpearAnimations.class)
public class SpearAnimationsMixin {
   @Inject(method = "firstPersonUse", at = @At("HEAD"), cancellable = true)
   private static void irons_lib$kineticFirstPersonUse(
      float ticksSinceKineticHitFeedback, PoseStack poseStack, float timeHeld, HumanoidArm arm, ItemStack itemStack, CallbackInfo ci
   ) {
      KineticAnimation animation = KineticAnimations.get(itemStack);
      if (animation != null) {
         animation.firstPersonUse(ticksSinceKineticHitFeedback, poseStack, timeHeld, arm, itemStack);
         ci.cancel();
      }
   }

   @Inject(method = "thirdPersonHandUse", at = @At("HEAD"), cancellable = true)
   private static void irons_lib$kineticThirdPersonArmPose(
      ModelPart arm, ModelPart head, HumanoidArm holdingArm, ItemStack item, HumanoidRenderState state, CallbackInfo ci
   ) {
      KineticAnimation animation = KineticAnimations.get(item);
      if (animation != null) {
         animation.thirdPersonArmPose(arm, head, holdingArm == HumanoidArm.RIGHT, item, state);
         ci.cancel();
      }
   }

   @Inject(method = "thirdPersonUseItem", at = @At("HEAD"), cancellable = true)
   private static void irons_lib$kineticThirdPersonItem(
      ArmedEntityRenderState state, PoseStack poseStack, float timeHeld, HumanoidArm arm, ItemStack actualItem, CallbackInfo ci
   ) {
      KineticAnimation animation = KineticAnimations.get(actualItem);
      if (animation != null) {
         animation.thirdPersonItem(state, poseStack, timeHeld, arm, actualItem);
         ci.cancel();
      }
   }
}
