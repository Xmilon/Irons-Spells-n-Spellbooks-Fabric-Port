package io.redspace.ironsspellbooks.entity.spells.blood_slash;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BloodSlashRenderer extends EntityRenderer<BloodSlashProjectile> {
    //    private static final Identifier[] TEXTURES = {
//            irons_spellbooks.id("textures/entity/blood_slash/blood_slash_1.png"),
//            irons_spellbooks.id("textures/entity/blood_slash/blood_slash_2.png"),
//            irons_spellbooks.id("textures/entity/blood_slash/blood_slash_3.png"),
//            irons_spellbooks.id("textures/entity/blood_slash/blood_slash_4.png"),
//            irons_spellbooks.id("textures/entity/blood_slash/blood_slash_5.png"),
//            irons_spellbooks.id("textures/entity/blood_slash/blood_slash_6.png")
//    };
    private static Identifier TEXTURE = IronsSpellbooks.id("textures/entity/blood_slash/blood_slash_large.png");
    private static Identifier TEXTURES[] = {
            Identifier.withDefaultNamespace("textures/particle/sweep_0.png"),
            Identifier.withDefaultNamespace("textures/particle/sweep_1.png"),
            Identifier.withDefaultNamespace("textures/particle/sweep_2.png"),
            Identifier.withDefaultNamespace("textures/particle/sweep_3.png"),
            Identifier.withDefaultNamespace("textures/particle/sweep_4.png"),
            Identifier.withDefaultNamespace("textures/particle/sweep_5.png"),
            Identifier.withDefaultNamespace("textures/particle/sweep_6.png"),
            Identifier.withDefaultNamespace("textures/particle/sweep_7.png")
    };

    public BloodSlashRenderer(Context context) {
        super(context);
    }

    @Override
    public void render(BloodSlashProjectile entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        poseStack.pushPose();

        Pose pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();
        poseStack.rotate(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot())));
        poseStack.rotate(Axis.XP.rotationDegrees(-Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
        entity.animationTime++;
        poseStack.rotate(Axis.ZP.rotationDegrees(((entity.animationSeed % 30) - 15) * (float) Math.sin(entity.animationTime * .015)));

        //VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));

        float oldWith = (float) entity.oldBB.getXsize();
        float width = entity.getBbWidth();
        width = oldWith + (width - oldWith) * Math.min(partialTicks, 1);
        //float halfWidth = width * .5f;
        //old color: 125, 0, 10

        //drawSlash(pose, bufferSource, light, width, 2);

        poseStack.rotate(Axis.YP.rotationDegrees(-15));
        poseStack.rotate(Axis.ZP.rotationDegrees(-10));
        drawSlash(pose, entity, bufferSource, light, width, 4);

        poseStack.rotate(Axis.YP.rotationDegrees(30));
        poseStack.rotate(Axis.ZP.rotationDegrees(20));
        drawSlash(pose, entity, bufferSource, light, width, 0);

        poseStack.popPose();

        super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
    }

    private void drawSlash(Pose pose, BloodSlashProjectile entity, MultiBufferSource bufferSource, int light, float width, int offset) {
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity, offset)));
        float halfWidth = width * .5f;
        //old color: 125, 0, 10
        consumer.addVertex(poseMatrix, -halfWidth, -.1f, -halfWidth).setColor(90, 0, 10, 255).setUv(0f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0f, 1f, 0f);
        consumer.addVertex(poseMatrix, halfWidth, -.1f, -halfWidth).setColor(90, 0, 10, 255).setUv(1f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0f, 1f, 0f);
        consumer.addVertex(poseMatrix, halfWidth, -.1f, halfWidth).setColor(90, 0, 10, 255).setUv(1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0f, 1f, 0f);
        consumer.addVertex(poseMatrix, -halfWidth, -.1f, halfWidth).setColor(90, 0, 10, 255).setUv(0f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0f, 1f, 0f);
    }

    @Override
    public Identifier getTextureLocation(BloodSlashProjectile entity) {
        int frame = (entity.animationTime / 4) % TEXTURES.length;
        return TEXTURES[frame];
        //return TEXTURE;
    }

    private Identifier getTextureLocation(BloodSlashProjectile entity, int offset) {
        int frame = (entity.animationTime / 6 + offset) % TEXTURES.length;
        return TEXTURES[frame];
    }
}
