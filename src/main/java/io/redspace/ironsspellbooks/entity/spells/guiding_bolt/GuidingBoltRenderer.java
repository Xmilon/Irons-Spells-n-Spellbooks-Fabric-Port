package io.redspace.ironsspellbooks.entity.spells.guiding_bolt;

import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

public class GuidingBoltRenderer extends EntityBackedRenderer<GuidingBoltProjectile> {
    public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "guiding_bolt_model"), "main");
    private static final Identifier BASE_TEXTURE = IronsSpellbooks.id("textures/entity/guiding_bolt/guiding_bolt.png");
    private static final Identifier[] FIRE_TEXTURES = {
            IronsSpellbooks.id("textures/entity/guiding_bolt/fire_1.png"),
            IronsSpellbooks.id("textures/entity/guiding_bolt/fire_2.png"),
            IronsSpellbooks.id("textures/entity/guiding_bolt/fire_3.png"),
            IronsSpellbooks.id("textures/entity/guiding_bolt/fire_4.png"),
            IronsSpellbooks.id("textures/entity/guiding_bolt/fire_5.png"),
            IronsSpellbooks.id("textures/entity/guiding_bolt/fire_6.png"),
            IronsSpellbooks.id("textures/entity/guiding_bolt/fire_7.png")
    };


    protected final ModelPart body;
    protected final ModelPart outline;

    public GuidingBoltRenderer(Context context) {
        super(context);
        ModelPart modelpart = context.bakeLayer(MODEL_LAYER_LOCATION);
        this.body = modelpart.getChild("body");
        this.outline = modelpart.getChild("outline");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -1.5F, -5.0F, 3.0F, 3.0F, 5.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("outline", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 16.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 48, 24);
    }

    @Override
    protected void submit(GuidingBoltProjectile entity, float partialTicks, int light, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        Vec3 motion = entity.getDeltaMovement();
        float xRot = -((float) (Mth.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        float yRot = -((float) (Mth.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 90.0F);
        poseStack.rotateDegrees(Axis.YP, yRot);
        poseStack.rotateDegrees(Axis.XP, xRot);
        collector.submitModelPart(this.body, poseStack, RenderTypes.energySwirl(getTextureLocation(entity), 0, 0), LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, null);

        poseStack.scale(0.4f, 0.4f, 0.4f);
        collector.submitModelPart(this.outline, poseStack, RenderTypes.energySwirl(getFireTextureLocation(entity), 0, 0), LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, null);


        poseStack.popPose();
    }

    public Identifier getTextureLocation(GuidingBoltProjectile entity) {
        return BASE_TEXTURE;
    }

    public Identifier getFireTextureLocation(Projectile entity) {
        int frame = (entity.tickCount) % FIRE_TEXTURES.length;
        return FIRE_TEXTURES[frame];
    }
}
