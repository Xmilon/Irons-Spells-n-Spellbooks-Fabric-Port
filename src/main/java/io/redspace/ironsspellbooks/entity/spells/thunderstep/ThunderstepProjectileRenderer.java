package io.redspace.ironsspellbooks.entity.spells.thunderstep;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import io.redspace.ironsspellbooks.render.EntityBackedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class ThunderstepProjectileRenderer extends EntityBackedRenderer<ThunderstepProjectile> {

    public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "ball_lightning_model"), "main");
    private static final Identifier[] SWIRL_TEXTURES = {
            IronsSpellbooks.id("textures/entity/ball_lightning/ball_lightning_0.png"),
            IronsSpellbooks.id("textures/entity/ball_lightning/ball_lightning_1.png"),
            IronsSpellbooks.id("textures/entity/ball_lightning/ball_lightning_2.png"),
            IronsSpellbooks.id("textures/entity/ball_lightning/ball_lightning_3.png"),
            IronsSpellbooks.id("textures/entity/ball_lightning/ball_lightning_4.png")
    };

    private final ModelPart orb;

    public ThunderstepProjectileRenderer(Context context) {
        super(context);
        ModelPart modelpart = context.bakeLayer(MODEL_LAYER_LOCATION);
        this.orb = modelpart.getChild("orb");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("orb", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 8, 8);
    }

    @Override
    protected void submit(ThunderstepProjectile entity, float partialTicks, int light, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0, entity.getBoundingBox().getYsize() * .5f, 0);

        Pose pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();
        poseStack.scale(0.2f, 0.2f, 0.2f);
        for (int i = 0; i < 3; i++) {
            poseStack.pushPose();
            float r = 0.25f;
            float g = 0.8f;
            float b = 1.0f;
            r = Mth.clamp(r + r * i, 0, 1f);
            g = Mth.clamp(g + g * i, 0, 1f);
            b = Mth.clamp(b + b * i, 0, 1f);
            float f = entity.tickCount + partialTicks + i * 777;
            float swirlX = Mth.cos(.065f * f) * 180;
            float swirlY = Mth.sin(.065f * f) * 180;
            float swirlZ = Mth.cos(.065f * f + 5464) * 180;
            float scalePerLayer = 0.2f;
            poseStack.rotateDegrees(Axis.XP, swirlX * (int) Math.pow(-1, i));
            poseStack.rotateDegrees(Axis.YP, swirlY * (int) Math.pow(-1, i));
            poseStack.rotateDegrees(Axis.ZP, swirlZ * (int) Math.pow(-1, i));
            float scale = 2f - i * scalePerLayer;
            poseStack.scale(scale, scale, scale);
            collector.submitModelPart(this.orb, poseStack, RenderHelper.CustomerRenderType.magic(getSwirlTextureLocation(entity, i * i)), LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, null, RenderHelper.colorf(r, g, b));
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    public Identifier getTextureLocation(ThunderstepProjectile entity) {
        return SWIRL_TEXTURES[0];
    }

    private Identifier getSwirlTextureLocation(ThunderstepProjectile entity, int offset) {
        int frame = (entity.tickCount + offset) % SWIRL_TEXTURES.length;
        return SWIRL_TEXTURES[frame];
    }
}
