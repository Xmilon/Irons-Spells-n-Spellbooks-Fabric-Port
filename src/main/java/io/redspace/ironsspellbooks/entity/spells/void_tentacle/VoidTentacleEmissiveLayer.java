package io.redspace.ironsspellbooks.entity.spells.void_tentacle;

import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.render.IronsGeoEntityRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class VoidTentacleEmissiveLayer extends GeoRenderLayer<VoidTentacle, Void, EntityRenderState> {
    public static final Identifier TEXTURE = IronsSpellbooks.id("textures/entity/void_tentacle/void_tentacle_emissive.png");

    public VoidTentacleEmissiveLayer(GeoRenderer<VoidTentacle, Void, EntityRenderState> renderer) {
        super(renderer);
    }

    // unverified: redraws the top level bones at the pose GeckoLib leaves in the pass, tinted by a per-frame pulse.
    @Override
    public void submitRenderTask(RenderPassInfo<EntityRenderState> info, SubmitNodeCollector collector) {
        if (!info.willRender()) {
            return;
        }
        VoidTentacle animatable = IronsGeoEntityRenderer.entityOf(info.renderState());
        float partialTick = IronsGeoEntityRenderer.partialTickOf(info.renderState());
        float f = Mth.sin((float) ((animatable.tickCount + partialTick + ((animatable.getX() + animatable.getZ()) * 500)) * .15f)) * .5f + .5f;
        int color = Utils.packRGB(new Vector3f(f, f, f)) | 0xFF000000;
        collector.submitCustomGeometry(info.poseStack(), RenderTypes.eyes(TEXTURE), (pose, vertexConsumer) -> {
            var drawStack = new PoseStack();
            drawStack.last().set(pose);
            for (var bone : info.model().topLevelBones()) {
                bone.render(info, drawStack, vertexConsumer, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, color);
            }
        });
    }
}
