package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.wispforest.accessories.api.client.AccessoriesRenderStateKeys;
import io.wispforest.accessories.api.client.AccessoryRenderState;
import io.wispforest.accessories.api.client.renderers.AccessoryRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Draws a worn spellbook on the wearer's hip. Native Accessories renderer; binding it to the spellbook items
 * belongs to the Accessories integration, which is not wired yet.
 */
@OnlyIn(Dist.CLIENT)
public class SpellBookCurioRenderer implements AccessoryRenderer {
    @Override
    public boolean shouldCreateStackRenderState() {
        return true;
    }

    @Override
    public <S extends LivingEntityRenderState> void render(AccessoryRenderState accessoryState, S state, EntityModel<S> model, PoseStack poseStack, SubmitNodeCollector collector) {
        if (!(model instanceof HumanoidModel<?> humanoidModel)) {
            return;
        }
        var item = accessoryState.getStateData(AccessoriesRenderStateKeys.ITEM_STACK_STATE);
        if (item == null || item.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        humanoidModel.body.translateAndRotate(poseStack);
        //Negative X is right, Negative Z is Forward
        //Scale by 1/16th, we are now dealing with units of pixels
        poseStack.translate((state instanceof HumanoidRenderState humanoid && !humanoid.chestEquipment.isEmpty() ? -5.5 : -4.5) * .0625f, 9 * .0625f, 0);
        poseStack.rotateDegrees(Axis.YP, Mth.PI * Mth.RAD_TO_DEG);
        poseStack.rotateDegrees(Axis.ZP, (Mth.PI - 5 * Mth.DEG_TO_RAD) * Mth.RAD_TO_DEG);
        poseStack.scale(.625f, .625f, .625f);
        item.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }
}
