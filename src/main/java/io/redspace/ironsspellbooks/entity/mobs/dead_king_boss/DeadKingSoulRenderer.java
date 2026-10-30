package io.redspace.ironsspellbooks.entity.mobs.dead_king_boss;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.render.RenderHelper;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class DeadKingSoulRenderer extends EntityRenderer<DeadKingSoulEntity, DeadKingSoulRenderer.State> {
    public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "dead_king_orb_model"), "main");
    public static final ModelLayerLocation CROWN_CUBE_LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "dead_king_soul_crown_cube"), "main");
    private static final Identifier TEXTURE = IronsSpellbooks.id("textures/entity/magic_missile/magic_missile.png");
    private static final Identifier CROWN_TEXTURE = IronsSpellbooks.id("textures/entity/dead_king/dead_king_crown.png");
    private final ModelPart body;
    private final ModelPart crownCube;

    public DeadKingSoulRenderer(Context context) {
        super(context);
        ModelPart modelpart = context.bakeLayer(MODEL_LAYER_LOCATION);
        this.body = modelpart.getChild("body");
        this.crownCube = context.bakeLayer(CROWN_CUBE_LAYER_LOCATION).getChild("cube");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(4.0F, 4.0F, 4.0F, -8.0F, -8.0F, -8.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 48, 24);
    }

    public static LayerDefinition createCrownCubeLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 48, 48);
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(DeadKingSoulEntity entity, State state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.bbHeight = entity.getBbHeight();
        state.id = entity.getId();
        state.tickCount = entity.tickCount;
        Vec3 motion = entity.getDeltaMovement();
        state.xRot = -((float) (Mth.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        state.yRot = -((float) (Mth.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 90.0F);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        float partialTicks = state.ageInTicks - state.tickCount;
        poseStack.pushPose();
        poseStack.translate(0, state.bbHeight * 1.5, 0);
        poseStack.scale(1.5f, -1.5f, 1.5f);
        poseStack.rotateDegrees(Axis.YP, state.ageInTicks);
        poseStack.rotateDegrees(Axis.XP, Mth.sin(state.ageInTicks / 100f) * 6f);
        collector.submitModelPart(this.crownCube, poseStack, RenderTypes.entityCutout(CROWN_TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, null);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0, state.bbHeight * .5f, 0);
        poseStack.rotateDegrees(Axis.YP, state.yRot);
        poseStack.rotateDegrees(Axis.XP, state.xRot);
        poseStack.scale(0.75f, 0.75f, 0.75f);
        float f = state.tickCount + state.id * 31 + partialTicks;

        float noise = ((Mth.sin(f * .37f) + Mth.cos(f * .09f)) / 3 + 2) * .25f;
        float noise1 = ((Mth.cos(f * .065f) + Mth.sin(f * .09f)) / 3) * .15f;
        float noise2 = ((Mth.sin(f * .05f) + Mth.cos(f * .07f)) / 3) * .15f;
        float noise3 = ((Mth.sin(f * .08f) + Mth.cos(f * .05f)) / 3) * .15f;
        poseStack.translate(noise1, noise2, noise3);

        renderOrb(poseStack, collector, 1f, noise, 0.25f, 255, 255, 255);
        renderOrb(poseStack, collector, 0.6f, noise, 1f, 0x2a, 0xcf, 0xc9);
        renderOrb(poseStack, collector, 0.4f, noise, 1.5f, 0x2a, 0xcf, 0xc9);

        poseStack.popPose();
        super.submit(state, poseStack, collector, camera);
    }

    private void renderOrb(PoseStack poseStack, SubmitNodeCollector collector, float intensity, float jitter, float scale, int r, int g, int b) {
        poseStack.pushPose();
        scale += jitter;
        poseStack.scale(scale, scale, scale);
        collector.submitModelPart(this.body, poseStack, renderType(TEXTURE), LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, null, RenderHelper.colorf(r * intensity / 255f, g * intensity / 255f, b * intensity / 255f, intensity));
        poseStack.popPose();
    }

    public RenderType renderType(Identifier TEXTURE) {
        return RenderTypes.lightning();
    }

    public static class State extends EntityRenderState {
        public float bbHeight;
        public int id;
        public int tickCount;
        public float xRot;
        public float yRot;
    }
}
