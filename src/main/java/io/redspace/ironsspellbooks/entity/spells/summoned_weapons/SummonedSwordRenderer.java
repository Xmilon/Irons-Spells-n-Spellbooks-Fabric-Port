package io.redspace.ironsspellbooks.entity.spells.summoned_weapons;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.RenderPassInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironsspellbooks.render.RenderHelper;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.model.GeoModel;
import io.redspace.ironsspellbooks.render.IronsGeoEntityRenderer;

import java.util.function.Supplier;

public class SummonedSwordRenderer extends IronsGeoEntityRenderer<SummonedWeaponEntity> {
    public SummonedSwordRenderer(EntityRendererProvider.Context renderManager, Supplier<GeoModel<SummonedWeaponEntity>> model) {
        super(renderManager, model.get());
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<EntityRenderState> info) {
        var entity = entity(info);
        var poseStack = info.poseStack();
        float partialTicks = info.renderState().getGeckolibData(PARTIAL_TICK);
        super.adjustRenderPose(info);
        poseStack.translate(0, entity.getBbHeight() * .5f, 0);
    }

    @Override
    public RenderType getRenderType(EntityRenderState state, Identifier texture) {
        var animatable = entity(state);
        float partialTick = state.getGeckolibData(PARTIAL_TICK);
        return RenderHelper.CustomerRenderType.magic(texture);
    }

    @Override
    public int getRenderColor(SummonedWeaponEntity animatable, Void relatedObject, float partialTick) {
        return 0xFFC0C0C0;
    }
}
