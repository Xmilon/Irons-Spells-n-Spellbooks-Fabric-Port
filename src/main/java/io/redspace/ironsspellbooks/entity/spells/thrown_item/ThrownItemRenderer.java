package io.redspace.ironsspellbooks.entity.spells.thrown_item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import io.redspace.ironsspellbooks.render.EntityBackedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class ThrownItemRenderer extends EntityBackedRenderer<ThrownItemProjectile> {

    private final ItemModelResolver itemModelResolver;

    public ThrownItemRenderer(Context context) {
        super(context);
        this.itemModelResolver = context.getItemModelResolver();
    }

    @Override
    protected void submit(ThrownItemProjectile entity, float partialTick, int packedLight, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        var item = entity.getThrownItem();
        if (item.isEmpty()) {
            item = Items.STONE.getDefaultInstance();
        }
        poseStack.pushPose();
        Vec3 motion = entity.deltaMovementOld.add(entity.getDeltaMovement().subtract(entity.deltaMovementOld).scale(partialTick));
        float xRot = -((float) (Mth.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        float yRot = -((float) (Mth.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 90.0F);
        poseStack.rotateDegrees(Axis.YP, yRot);
        poseStack.rotateDegrees(Axis.XP, xRot - (entity.tickCount + partialTick) * 36);
        float scale = entity.getScale();
        poseStack.scale(scale, scale, scale);
        var renderState = new ItemStackRenderState();
        itemModelResolver.updateForNonLiving(renderState, item, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, entity);
        renderState.submit(poseStack, collector, packedLight, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }

    public Identifier getTextureLocation(ThrownItemProjectile entity) {
        return IronsSpellbooks.id("empty");
    }
}
