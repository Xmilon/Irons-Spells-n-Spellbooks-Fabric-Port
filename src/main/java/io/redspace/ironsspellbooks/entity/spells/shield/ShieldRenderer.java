package io.redspace.ironsspellbooks.entity.spells.shield;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.render.RenderHelper;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec2;

public class ShieldRenderer extends EntityRenderer<ShieldEntity, ShieldRenderer.ShieldRenderState> {
    public static final Identifier SPECTRAL_OVERLAY_TEXTURE = Identifier.fromNamespaceAndPath(
            IronsSpellbooks.MODID, "textures/entity/shield/shield_overlay.png");
    private static final Identifier TRIM_TEXTURE = Identifier.fromNamespaceAndPath(
            IronsSpellbooks.MODID, "textures/entity/shield/shield_trim.png");
    private static final int COLOR = RenderHelper.colorf(.65f, .65f, .65f);
    private final ShieldModel model;
    private final ShieldTrimModel trimModel;

    public ShieldRenderer(Context context) {
        super(context);
        model = new ShieldModel(context.bakeLayer(ShieldModel.LAYER_LOCATION));
        trimModel = new ShieldTrimModel(context.bakeLayer(ShieldTrimModel.LAYER_LOCATION));
    }

    @Override
    public ShieldRenderState createRenderState() {
        return new ShieldRenderState();
    }

    @Override
    public void extractRenderState(ShieldEntity entity, ShieldRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.yaw = entity.getYRot();
        state.pitch = entity.getXRot();
        state.width = entity.width * .65f;
        Vec2 offset = getEnergySwirlOffset(entity, partialTick);
        state.swirlU = offset.x;
        state.swirlV = offset.y;
    }

    @Override
    public void submit(ShieldRenderState state, PoseStack poseStack, SubmitNodeCollector collector,
                       CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.rotateDegrees(Axis.YP, -state.yaw);
        poseStack.rotateDegrees(Axis.XP, state.pitch);
        poseStack.scale(state.width, state.width, state.width);
        collector.submitModel(model, state, poseStack,
                RenderHelper.CustomerRenderType.magicSwirl(SPECTRAL_OVERLAY_TEXTURE, state.swirlU, state.swirlV),
                LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, COLOR);
        collector.submitModel(trimModel, state, poseStack, RenderTypes.energySwirl(TRIM_TEXTURE, 0, 0),
                LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, COLOR);
        poseStack.popPose();
        super.submit(state, poseStack, collector, camera);
    }

    private static float noise(float f) {
        return (float) (Math.sin(f / 4) + 2 * Math.sin(f / 3) + 3 * Math.sin(f / 2) + 4 * Math.sin(f)) * .25f;
    }

    public static Vec2 getEnergySwirlOffset(ShieldEntity entity, float partialTicks, int offset) {
        float f = (entity.tickCount + partialTicks) * .02f;
        return new Vec2(noise(1.2f * f + offset), noise(f + 456 + offset));
    }

    public static Vec2 getEnergySwirlOffset(ShieldEntity entity, float partialTicks) {
        return getEnergySwirlOffset(entity, partialTicks, 0);
    }

    public static class ShieldRenderState extends EntityRenderState {
        public float yaw;
        public float pitch;
        public float width;
        public float swirlU;
        public float swirlV;
    }
}
