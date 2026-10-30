package io.redspace.ironsspellbooks.entity.spells.devour_jaw;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.model.effects.EvokerFangsModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import io.redspace.ironsspellbooks.render.EntityBackedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class DevourJawRenderer extends EntityBackedRenderer<DevourJaw> {

    private final DevourJawModel model;

    public DevourJawRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new DevourJawModel(pContext.bakeLayer(ModelLayers.EVOKER_FANGS));
    }

    protected void submit(DevourJaw entity, float partialTicks, int light, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (entity.tickCount < entity.waitTime)
            return;
        float f = entity.tickCount + partialTicks;
        poseStack.pushPose();
        poseStack.rotateDegrees(Axis.YP, -entity.getYRot());
        poseStack.scale(-1, -1, 1);
        poseStack.scale(1.85f, 1.85f, 1.85f);
        // the shared model parts are posed when drawn, not when submitted
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(getTextureLocation(entity)), (pose, vertexconsumer) -> {
            PoseStack drawStack = new PoseStack();
            drawStack.last().set(pose);
            this.model.setupAnim(entity, f);
            this.model.root.render(drawStack, vertexconsumer, light, OverlayTexture.NO_OVERLAY, -1);
        });
        poseStack.popPose();
    }

    public Identifier getTextureLocation(DevourJaw pEntity) {
        return IronsSpellbooks.id("textures/entity/devour_jaw.png");
    }

    static class DevourJawModel {
        private final ModelPart root;
        private final ModelPart base;
        private final ModelPart upperJaw;
        private final ModelPart lowerJaw;

        public DevourJawModel(ModelPart pRoot) {
            this.root = pRoot;
            this.base = pRoot.getChild("base");
            this.upperJaw = this.base.getChild("upper_jaw");
            this.lowerJaw = this.base.getChild("lower_jaw");
        }

        public void setupAnim(DevourJaw entity, float time) {
            time -= entity.waitTime;
            float interval = entity.warmupTime - entity.waitTime;

            float f = Mth.clamp(time / interval, 0, 1);
            f = 1 - f * f * f * f;
            this.upperJaw.zRot = (float) Math.PI - f * 0.35F * (float) Math.PI;
            this.lowerJaw.zRot = (float) Math.PI + f * 0.35F * (float) Math.PI;

            float f2 = (time / interval);
            f2 = .5f * Mth.cos(.5f * Mth.PI * (f2 - 1)) + .5f;
            f2 *= f2;
            // jaws are children of base in 26.x, so moving base moves all three
            this.base.y = -18F * f2 + 16f;
        }
    }
}
