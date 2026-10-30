package io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.fire_orb;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.spells.acid_orb.AcidOrbRenderer;
import io.redspace.ironsspellbooks.entity.spells.fireball.FireballRenderer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class OminousFireOrbRenderer extends EntityRenderer<OminousFireOrbEntity, OminousFireOrbRenderer.State> {
    protected final ModelPart fireball;
    protected final ModelPart outline;

    public static final Identifier BASE_TEXTURE = IronsSpellbooks.id("textures/entity/fireball/fireball_core_soul.png");
    public static Identifier[] SWIRL_TEXTURES = {
            IronsSpellbooks.id("textures/entity/fireball/soul_swirl_0.png"),
            IronsSpellbooks.id("textures/entity/fireball/soul_swirl_1.png"),
            IronsSpellbooks.id("textures/entity/fireball/soul_swirl_2.png"),
            IronsSpellbooks.id("textures/entity/fireball/soul_swirl_3.png"),
            IronsSpellbooks.id("textures/entity/fireball/soul_swirl_4.png"),
            IronsSpellbooks.id("textures/entity/fireball/soul_swirl_5.png"),
            IronsSpellbooks.id("textures/entity/fireball/soul_swirl_6.png"),
            IronsSpellbooks.id("textures/entity/fireball/soul_swirl_7.png"),
            IronsSpellbooks.id("textures/entity/fireball/soul_swirl_8.png"),
            IronsSpellbooks.id("textures/entity/fireball/soul_swirl_9.png"),
            IronsSpellbooks.id("textures/entity/fireball/soul_swirl_10.png")
    };

    public OminousFireOrbRenderer(EntityRendererProvider.Context context) {
        super(context);
        ModelPart modelpart = context.bakeLayer(FireballRenderer.MODEL_LAYER_LOCATION);
        this.fireball = modelpart.getChild("body");
        this.outline = context.bakeLayer(AcidOrbRenderer.MODEL_LAYER_LOCATION).getChild("swirl");
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(OminousFireOrbEntity entity, State state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.fuse = entity.getFuse();
        state.chargeTime = entity.getChargeTime();
        state.fuseProgress = entity.getFuseProgressTicks() + partialTick;
        state.fuseProgressTicks = entity.getFuseProgressTicks();
        state.tickCount = entity.tickCount;
        state.swirlTexture = SWIRL_TEXTURES[entity.tickCount % SWIRL_TEXTURES.length];
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        super.submit(state, poseStack, collector, camera);
        float f = state.ageInTicks;

        int fuse = state.fuse;
        int chargeTime = state.chargeTime;

        float fusePercent = fuse > 0 ? Mth.clamp(state.fuseProgress / fuse, 0f, 1f) : 0f;
        float chargePercent = chargeTime > 0 ? Mth.clamp(f / chargeTime, 0, 1f) : 0f;

        poseStack.pushPose();
        try {
            float scale = fuse > 0 ? Mth.lerp(fusePercent, 1f, 1.85f) : 1f;
            poseStack.translate(0, 2 + Mth.sin(f * .2f) * .1f, 0);
            poseStack.scale(scale, scale, scale);
            poseStack.scale(1.2f, 1.2f, 1.2f);
            float swirlX = Mth.cos(.08f * f) * 180;
            float swirlY = Mth.sin(.08f * f) * 180;
            float swirlZ = Mth.cos(.08f * f + 5464) * 180;
            poseStack.rotateDegrees(Axis.XP, swirlX);
            poseStack.rotateDegrees(Axis.YP, swirlY);
            poseStack.rotateDegrees(Axis.ZP, swirlZ);

            if (state.fuseProgressTicks > 0) {
                collector.submitModelPart(fireball, poseStack, RenderTypes.entityCutout(BASE_TEXTURE), LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, null);
            }
            if (fuse > 0 || chargeTime > 0) {
                float percent = state.tickCount >= chargeTime ? fusePercent : 1f - chargePercent;
                float intensity = Mth.lerp(percent, .5f, 1.25f);
                float outlineScale = Mth.lerp(percent, 0.8f, 2.5f) + Mth.sin(f * intensity) * .4f * intensity;
                poseStack.scale(outlineScale, outlineScale, outlineScale);
                poseStack.rotateDegrees(Axis.XP, swirlX);
                poseStack.rotateDegrees(Axis.YP, swirlY);
                poseStack.rotateDegrees(Axis.ZP, swirlZ);
                collector.submitModelPart(outline, poseStack, RenderTypes.entityCutout(state.swirlTexture), LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, null);
            }
        } finally {
            poseStack.popPose();
        }
    }

    public static class State extends EntityRenderState {
        public int fuse;
        public int chargeTime;
        public float fuseProgress;
        public int fuseProgressTicks;
        public int tickCount;
        public Identifier swirlTexture = SWIRL_TEXTURES[0];
    }

    public Identifier getTextureLocation(OminousFireOrbEntity entity) {
        return BASE_TEXTURE;
    }
}
