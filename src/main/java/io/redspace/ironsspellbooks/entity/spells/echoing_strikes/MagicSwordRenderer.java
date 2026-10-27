package io.redspace.ironsspellbooks.entity.spells.echoing_strikes;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.RenderPassInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.render.RenderHelper;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.model.GeoModel;
import io.redspace.ironsspellbooks.render.IronsGeoEntityRenderer;

import java.util.function.Supplier;

public class MagicSwordRenderer<T extends Entity & GeoEntity> extends IronsGeoEntityRenderer<T> {
    public MagicSwordRenderer(EntityRendererProvider.Context renderManager, Supplier<GeoModel<T>> model) {
        super(renderManager, model.get());
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<EntityRenderState> info) {
        var entity = entity(info);
        var poseStack = info.poseStack();
        float partialTicks = info.renderState().getGeckolibData(PARTIAL_TICK);
        super.adjustRenderPose(info);
        poseStack.translate(0, entity.getBbHeight() * .5f, 0);
        poseStack.rotateDegrees(Axis.YP, Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()));
        poseStack.rotateDegrees(Axis.XP, -Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()));
    }

    @Override
    public RenderType getRenderType(EntityRenderState state, Identifier texture) {
        var animatable = entity(state);
        float partialTick = state.getGeckolibData(PARTIAL_TICK);
        return RenderHelper.CustomerRenderType.magic(texture);
    }

    @Override
    public int getRenderColor(T animatable, Void relatedObject, float partialTick) {
        return 0xFFC0C0C0;
    }
}
