package io.redspace.ironsspellbooks.entity.spells.acid_orb;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import io.redspace.ironsspellbooks.render.EntityBackedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class AcidOrbRenderer extends EntityBackedRenderer<AcidOrb> {

    public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "acid_orb_model"), "main");
    private static Identifier ORB_TEXTURE = IronsSpellbooks.id("textures/entity/acid_orb/acid_orb.png");
    private static Identifier SWIRL_TEXTURES[] = {
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_0.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_1.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_2.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_3.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_4.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_5.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_6.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_7.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_8.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_9.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_10.png")
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
    protected void submit(AcidOrb entity, float partialTicks, int light, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0, entity.getBoundingBox().getYsize() * .5f, 0);

        Pose pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();
        Vec3 motion = entity.getDeltaMovement();
        float xRot = -((float) (Mth.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        float yRot = -((float) (Mth.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 90.0F);
        poseStack.rotateDegrees(Axis.YP, yRot);
        poseStack.rotateDegrees(Axis.XP, xRot);
        collector.submitModelPart(this.orb, poseStack, RenderTypes.entityCutout(getTextureLocation(entity)), light, OverlayTexture.NO_OVERLAY, null);

        float f = entity.tickCount + partialTicks;
        float swirlX = Mth.cos(.08f * f) * 180;
        float swirlY = Mth.sin(.08f * f) * 180;
        float swirlZ = Mth.cos(.08f * f + 5464) * 180;
        poseStack.rotateDegrees(Axis.XP, swirlX);
        poseStack.rotateDegrees(Axis.YP, swirlY);
        poseStack.rotateDegrees(Axis.ZP, swirlZ);
        poseStack.scale(1.15f, 1.15f, 1.15f);
        collector.submitModelPart(this.swirl, poseStack, RenderTypes.entityCutout(getSwirlTextureLocation(entity)), light, OverlayTexture.NO_OVERLAY, null);


        poseStack.popPose();
    }

    public Identifier getTextureLocation(AcidOrb entity) {
        return ORB_TEXTURE;
    }

    private Identifier getSwirlTextureLocation(AcidOrb entity) {
        int frame = (entity.tickCount / 2) % SWIRL_TEXTURES.length;
        return SWIRL_TEXTURES[frame];
    }
}
