package io.redspace.ironsspellbooks.entity.spells.acid_orb;

import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
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

public class AcidOrbRenderer extends EntityRenderer<AcidOrb, AcidOrbRenderer.State> {

    public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "acid_orb_model"), "main");
    private static final Identifier ORB_TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/acid_orb/acid_orb.png");
    private static final Identifier SWIRL_TEXTURES[] = {
            Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/acid_orb/swirl_0.png"),
            Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/acid_orb/swirl_1.png"),
            Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/acid_orb/swirl_2.png"),
            Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/acid_orb/swirl_3.png"),
            Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/acid_orb/swirl_4.png"),
            Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/acid_orb/swirl_5.png"),
            Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/acid_orb/swirl_6.png"),
            Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/acid_orb/swirl_7.png"),
            Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/acid_orb/swirl_8.png"),
            Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/acid_orb/swirl_9.png"),
            Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/acid_orb/swirl_10.png")
    };
    private final ModelPart orb;
    private final ModelPart swirl;

    public AcidOrbRenderer(Context context) {
        super(context);
        ModelPart modelpart = context.bakeLayer(MODEL_LAYER_LOCATION);
        this.orb = modelpart.getChild("orb");
        this.swirl = modelpart.getChild("swirl");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("orb", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("swirl", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 8, 8);
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(AcidOrb entity, State state, float partialTick) {
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
            collector.submitModelPart(this.orb, poseStack, RenderTypes.entityCutout(state.texture), state.lightCoords, OverlayTexture.NO_OVERLAY, null);

            float f = state.ageInTicks;
            float swirlX = Mth.cos(.08f * f) * 180;
            float swirlY = Mth.sin(.08f * f) * 180;
            float swirlZ = Mth.cos(.08f * f + 5464) * 180;
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

    public Identifier getTextureLocation(AcidOrb entity) {
        return ORB_TEXTURE;
    }

    private Identifier getSwirlTextureLocation(AcidOrb entity) {
        int frame = (entity.tickCount / 2) % SWIRL_TEXTURES.length;
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
