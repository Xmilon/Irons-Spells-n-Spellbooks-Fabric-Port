package io.redspace.ironsspellbooks.entity.spells.root;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.RenderPassInfo;


import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironsspellbooks.render.GeoLivingEntityRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.Nullable;
import com.geckolib.cache.model.BakedGeoModel;

public class RootRenderer extends GeoLivingEntityRenderer<RootEntity> {
    public RootRenderer(EntityRendererProvider.Context context) {
        super(context, new RootModel());
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<EntityRenderState> info) {
        var animatable = entity(info);
        var poseStack = info.poseStack();
        float partialTick = info.renderState().getGeckolibData(PARTIAL_TICK);
        var rooted = animatable.getFirstPassenger();

        if (rooted != null) {
            float scale = rooted.getBbWidth() / 0.6f; //.6 is the default player bb width
            poseStack.scale(scale, scale, scale);
        }
        super.adjustRenderPose(info);
    }
}
