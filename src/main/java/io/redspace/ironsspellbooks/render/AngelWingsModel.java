package io.redspace.ironsspellbooks.render;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class AngelWingsModel extends Model<AngelWingsModel.Pose> {
    private final ModelPart rightWing;
    private final ModelPart leftWing;

    public static final String MAIN = "main";
    public static final String ANGEL_WINGS = "angel_wings";
    public static final ModelLayerLocation ANGEL_WINGS_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, ANGEL_WINGS), MAIN);

    public record Pose(boolean visible, float xRot, float yRot, float zRot, float yOffset, boolean baby) {
        public static final Pose HIDDEN = new Pose(false, 0, 0, 0, 0, false);
    }

    public AngelWingsModel(ModelPart pRoot) {
        super(pRoot, texture -> RenderTypes.energySwirl(texture, 0, 0));
        this.leftWing = pRoot.getChild("left_wing");
        this.rightWing = pRoot.getChild("right_wing");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        CubeDeformation cubedeformation = new CubeDeformation(1.0F);
        partdefinition.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(22, 0).addBox(-10.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, cubedeformation), PartPose.offsetAndRotation(5.0F, 0.0F, 0.0F, 0.2617994F, 0.0F, -0.2617994F));
        partdefinition.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(22, 0).mirror().addBox(0.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, cubedeformation), PartPose.offsetAndRotation(-5.0F, 0.0F, 0.0F, 0.2617994F, 0.0F, 0.2617994F));
        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    public static Pose targetPose(boolean fallFlying, boolean crouching, Vec3 movement, boolean baby) {
        float f = 0.2617994F;
        float f1 = -0.2617994F;
        float f2 = 0.0F;
        float f3 = 0.0F;
        if (fallFlying) {
            float f4 = 1.0F;
            Vec3 vec3 = movement;
            if (vec3.y < 0.0D) {
                Vec3 vec31 = vec3.normalize();
                f4 = 1.0F - (float) Math.pow(-vec31.y, 1.5D);
            }

            f = f4 * 0.34906584F + (1.0F - f4) * f;
            f1 = f4 * (-(float) Math.PI / 2F) + (1.0F - f4) * f1;
        } else if (crouching) {
            f = 0.6981317F;
            f1 = (-(float) Math.PI / 4F);
            f2 = 3.0F;
            f3 = 0.08726646F;
        }

        return new Pose(true, f, f3, f1, f2, baby);
    }

    public static Pose smooth(Pose previous, Pose target) {
        return new Pose(target.visible(),
                previous.xRot() + (target.xRot() - previous.xRot()) * .1f,
                previous.yRot() + (target.yRot() - previous.yRot()) * .1f,
                previous.zRot() + (target.zRot() - previous.zRot()) * .1f,
                target.yOffset(), target.baby());
    }

    @Override
    public void setupAnim(Pose pose) {
        super.setupAnim(pose);
        this.leftWing.y = pose.yOffset();
        this.leftWing.xRot = pose.xRot();
        this.leftWing.yRot = pose.yRot();
        this.leftWing.zRot = pose.zRot();

        this.rightWing.yRot = -this.leftWing.yRot;
        this.rightWing.y = this.leftWing.y;
        this.rightWing.xRot = this.leftWing.xRot;
        this.rightWing.zRot = -this.leftWing.zRot;
    }
}
