package io.redspace.ironsspellbooks.entity.spells.magma_ball;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.spells.acid_orb.AcidOrbRenderer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;

import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class MagmaBallRenderer extends EntityRenderer<FireBomb, MagmaBallRenderer.State> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/fireball/magma.png");
    private static final Identifier SWIRL_TEXTURES[] = {
            Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/fireball/swirl_0.png"),
            Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/fireball/swirl_1.png"),
            Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/fireball/swirl_2.png"),
            Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/fireball/swirl_3.png"),
            Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/fireball/swirl_4.png"),
            Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/fireball/swirl_5.png"),
            Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/fireball/swirl_6.png"),
            Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/fireball/swirl_7.png"),
            Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/fireball/swirl_8.png"),
            Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/fireball/swirl_9.png"),
            Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/fireball/swirl_10.png")
    };

    private final ModelPart orb;
    private final ModelPart swirl;

    public MagmaBallRenderer(Context context) {
        super(context);
        ModelPart modelpart = context.bakeLayer(AcidOrbRenderer.MODEL_LAYER_LOCATION);
        this.orb = modelpart.getChild("orb");
        this.swirl = modelpart.getChild("swirl");
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(FireBomb entity, State state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.centerHeight = entity.getBoundingBox().getYsize() * .5;
        Vec3 motion = entity.getDeltaMovement();
        state.pitch = -((float) (Mth.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        state.yaw = -((float) (Mth.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 90.0F);
        state.texture = getTextureLocation(entity);
        state.swirlTexture = getSwirlTextureLocation(entity);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        try {
            poseStack.translate(0, state.centerHeight, 0);

            poseStack.rotateDegrees(Axis.YP, state.yaw);
            poseStack.rotateDegrees(Axis.XP, state.pitch);

            float f = state.ageInTicks;
            float swirlX = Mth.cos(.08f * f) * 130;
            float swirlY = Mth.sin(.08f * f) * 130;
            float swirlZ = Mth.cos(.08f * f + 5464) * 130;
            poseStack.rotateDegrees(Axis.XP, swirlX);
            poseStack.rotateDegrees(Axis.YP, swirlY);
            poseStack.rotateDegrees(Axis.ZP, swirlZ);
            collector.submitModelPart(this.orb, poseStack, RenderTypes.entityCutout(state.texture), LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, null);

            poseStack.rotateDegrees(Axis.XP, swirlX);
            poseStack.rotateDegrees(Axis.YP, swirlY);
            poseStack.rotateDegrees(Axis.ZP, swirlZ);
            poseStack.scale(1.15f, 1.15f, 1.15f);
            collector.submitModelPart(this.swirl, poseStack, RenderTypes.entityCutout(state.swirlTexture), state.lightCoords, OverlayTexture.NO_OVERLAY, null);

        } finally {
            poseStack.popPose();
        }
        super.submit(state, poseStack, collector, camera);
    }

    public Identifier getTextureLocation(FireBomb entity) {
        return TEXTURE;
    }

    private Identifier getSwirlTextureLocation(FireBomb entity) {
        int frame = (entity.tickCount) % SWIRL_TEXTURES.length;
        return SWIRL_TEXTURES[frame];
    }

    public static class State extends EntityRenderState {
        public double centerHeight;
        public float pitch;
        public float yaw;
        public Identifier texture;
        public Identifier swirlTexture;
    }
}
