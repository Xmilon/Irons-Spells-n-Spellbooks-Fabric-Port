package io.redspace.ironsspellbooks.entity.spells.fiery_dagger;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.RenderPassInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.render.RenderHelper;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import com.geckolib.cache.model.BakedGeoModel;
import io.redspace.ironsspellbooks.render.IronsGeoEntityRenderer;

public class FieryDaggerRenderer extends IronsGeoEntityRenderer<FieryDaggerEntity> {
    public FieryDaggerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FieryDaggerModel());
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<EntityRenderState> info) {
        var entity = entity(info);
        var poseStack = info.poseStack();
        float partialTicks = info.renderState().getGeckolibData(PARTIAL_TICK);
        super.adjustRenderPose(info);
        Vec3 motion = entity.deltaMovementOld.add(entity.getDeltaMovement().subtract(entity.deltaMovementOld).scale(partialTicks));
        float xRot = ((float) (Mth.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        float yRot = -((float) (Mth.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) - 90.0F);
        poseStack.translate(0, entity.getBbHeight() * .5f, 0);
        poseStack.rotateDegrees(Axis.YP, yRot);
        poseStack.rotateDegrees(Axis.XP, xRot);
    }



    @Override
    public RenderType getRenderType(EntityRenderState state, Identifier texture) {
        return RenderHelper.CustomerRenderType.magic(FieryDaggerModel.TEXTURE);
    }

    @Override
    public int getRenderColor(FieryDaggerEntity animatable, Void relatedObject, float partialTick) {
        return 0xFFC0C0C0;
    }
}
