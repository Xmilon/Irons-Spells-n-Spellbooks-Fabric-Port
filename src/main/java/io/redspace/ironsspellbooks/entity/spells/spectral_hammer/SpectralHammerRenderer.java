package io.redspace.ironsspellbooks.entity.spells.spectral_hammer;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.geckolib.renderer.base.RenderPassInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironsspellbooks.render.GeoLivingEntityRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import com.geckolib.cache.model.BakedGeoModel;

public class SpectralHammerRenderer extends GeoLivingEntityRenderer<SpectralHammer> {
    public SpectralHammerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SpectralHammerModel());
        this.shadowRadius = 0.3f;
    }

    @Override
    public Identifier getTextureLocation(SpectralHammer animatable) {
        return SpectralHammerModel.textureResource;
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<EntityRenderState> info) {
        var animatable = entity(info);
        var poseStack = info.poseStack();
        float partialTick = info.renderState().getGeckolibData(PARTIAL_TICK);
        poseStack.scale(2, 2, 2);
        super.adjustRenderPose(info);
    }

    @Override
    public RenderType getRenderType(EntityRenderState state, Identifier texture) {
        var animatable = entity(state);
        float partialTick = state.getGeckolibData(PARTIAL_TICK);
        Vec2 vec2 = getEnergySwirlOffset(animatable, partialTick);
        return RenderTypes.energySwirl(texture, vec2.x, vec2.y);
    }

    private static float shittyNoise(float f) {
        return (float) (Math.sin(f / 4) + 2 * Math.sin(f / 3) + 3 * Math.sin(f / 2) + 4 * Math.sin(f)) * .25f;
    }

    public static Vec2 getEnergySwirlOffset(SpectralHammer entity, float partialTicks, int offset) {
        float f = (entity.tickCount + partialTicks) * .02f;
        return new Vec2(shittyNoise(1.2f * f + offset), shittyNoise(f + 456 + offset));
    }

    public static Vec2 getEnergySwirlOffset(SpectralHammer entity, float partialTicks) {
        return getEnergySwirlOffset(entity, partialTicks, 0);
    }

}
