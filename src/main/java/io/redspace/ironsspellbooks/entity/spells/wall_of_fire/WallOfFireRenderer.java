package io.redspace.ironsspellbooks.entity.spells.wall_of_fire;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import io.redspace.ironsspellbooks.render.EntityBackedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class WallOfFireRenderer extends EntityBackedRenderer<WallOfFireEntity> {

    private static Identifier TEXTURE = Identifier.withDefaultNamespace("textures/block/fire_0.png");

    //private static Identifier TEXTURE = Identifier.fromNamespaceAndPath("textures/entity/creeper/creeper_armor.png");
    public WallOfFireRenderer(Context context) {
        super(context);
    }

    @Override
    protected void submit(WallOfFireEntity entity, float partialTicks, int light, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {

        float height = 3;
        Vec3 origin = entity.position();

        for (int i = 0; i < entity.subEntities.length - 1; i++) {
            Vec3 start = entity.subEntities[i].position().subtract(origin);
            Vec3 end = entity.subEntities[i + 1].position().subtract(origin);
            int frameCount = 32;
            int frame = (entity.tickCount + i * 87) % frameCount;
            float uvPerFrame = (1 / (float) frameCount);
            float uvY = frame * uvPerFrame;
            poseStack.pushPose();
            collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(TEXTURE), (pose, consumer) -> {
                consumer.addVertex(pose.pose(), (float) start.x, (float) start.y, (float) start.z).setColor(255, 255, 255, 255).setUv(0f, uvY + uvPerFrame).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
                consumer.addVertex(pose.pose(), (float) start.x, (float) start.y + height, (float) start.z).setColor(255, 255, 255, 255).setUv(0f, uvY).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
                consumer.addVertex(pose.pose(), (float) end.x, (float) end.y + height, (float) end.z).setColor(255, 255, 255, 255).setUv(1f, uvY).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
                consumer.addVertex(pose.pose(), (float) end.x, (float) end.y, (float) end.z).setColor(255, 255, 255, 255).setUv(1f, uvY + uvPerFrame).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
            });
            poseStack.rotateDegrees(Axis.ZP, 180);
            collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(TEXTURE), (pose, consumer) -> {
                consumer.addVertex(pose.pose(), (float) start.x, (float) start.y, (float) start.z).setColor(255, 255, 255, 255).setUv(0f, uvY + uvPerFrame).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
                consumer.addVertex(pose.pose(), (float) start.x, (float) start.y + height, (float) start.z).setColor(255, 255, 255, 255).setUv(0f, uvY).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
                consumer.addVertex(pose.pose(), (float) end.x, (float) end.y + height, (float) end.z).setColor(255, 255, 255, 255).setUv(1f, uvY).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
                consumer.addVertex(pose.pose(), (float) end.x, (float) end.y, (float) end.z).setColor(255, 255, 255, 255).setUv(1f, uvY + uvPerFrame).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
            });

            poseStack.popPose();
        }
    }


    public Identifier getTextureLocation(WallOfFireEntity entity) {
        return TEXTURE;
    }
}
