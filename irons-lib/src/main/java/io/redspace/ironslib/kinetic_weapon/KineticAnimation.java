package io.redspace.ironslib.kinetic_weapon;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

public interface KineticAnimation {
   void firstPersonUse(float var1, PoseStack var2, float var3, HumanoidArm var4, ItemStack var5);

   default void thirdPersonArmPose(ModelPart arm, ModelPart head, boolean holdingInRightArm, ItemStack itemStack, HumanoidRenderState state) {
   }

   default void thirdPersonItem(ArmedEntityRenderState state, PoseStack poseStack, float ticksUsingItem, HumanoidArm arm, ItemStack itemStack) {
   }
}
