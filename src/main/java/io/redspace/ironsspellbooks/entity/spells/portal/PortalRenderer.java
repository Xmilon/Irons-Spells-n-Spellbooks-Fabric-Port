package io.redspace.ironsspellbooks.entity.spells.portal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.render.RenderHelper;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class PortalRenderer<T extends Entity> extends EntityRenderer<T, PortalRenderer.PortalRenderState> {
    public static class PortalRenderState extends EntityRenderState {
        public float yaw;
        public int animationTick;
        public float partialTick;
        public PortalType portalType;
    }

    /**
     * Holder class to resolve texture location of portal sprites given the following format:
     * <br>
     *
     * @param baseLocation The parent resource location of the name of the portal. Not actually a texture file.
     *                     <br>
     *                     "Child" texture files must follow the format <code>/base_[round/square]</code>, and <code>/base_[round/square]_color</code>.
     *                     <br> Supporting square and color is optional.
     *                     <br> Start from assets root (<code>textures/entity/...</code>)
     *                     <br> Do not include <code>.png</code>
     * @param hasSquare    Whether a square variant exists
     * @param hasColor     Whether grayscale variants round and square exist for tinting
     */
    public record PortalType(Identifier baseLocation, boolean hasSquare, boolean hasColor) {
        public Identifier getTextureLocation(boolean square, int color) {
            Identifier location = baseLocation;
            if (hasSquare && square) {
                location = location.withSuffix("_square");
            } else {
                location = location.withSuffix("_round");
            }
            if (hasColor && color != -1) {
                location = location.withSuffix("_color");
            }
            location = location.withSuffix(".png");
            return location;
        }

        public Identifier getTextureLocation(boolean square) {
            return getTextureLocation(square, -1);
        }

        public Identifier getTextureLocation() {
            return getTextureLocation(false, -1);
        }
    }

    public static final PortalType NORMAL = new PortalType(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/portal/portal"), true, true);
    public static final PortalType POCKET_DIMENSION = new PortalType(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/portal/pocket_dimension_portal"), true, false);
    public static final PortalType BLOOD = new PortalType(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/portal/blood_portal"), false, false);

    private static final Identifier ROUND_PORTAL = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/portal/portal_round.png");

    private final PortalType portalType;
    public PortalRenderer(Context context, PortalType portalType) {
        super(context);
        this.portalType = portalType;
    }
    public PortalRenderer(Context context) {
        this(context, NORMAL);
    }

    @Override
    public PortalRenderState createRenderState() {
        return new PortalRenderState();
    }

    @Override
    public void extractRenderState(T entity, PortalRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.yaw = entity.getYRot();
        state.animationTick = entity.tickCount;
        state.partialTick = partialTick;
        state.portalType = getPortalType(entity);
    }

    @Override
    public void submit(PortalRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.rotateDegrees(Axis.YP, -state.yaw);

        renderPortal(state.portalType, poseStack, collector, state.animationTick, state.partialTick, true, -1);

        poseStack.popPose();
        super.submit(state, poseStack, collector, camera);
    }

    protected PortalType getPortalType(T entity) {
        return portalType;
    }

    public static void renderPortal(PoseStack poseStack, SubmitNodeCollector collector, int animationTick, float partialTicks, boolean round, int color) {
        renderPortal(NORMAL, poseStack, collector, animationTick, partialTicks, round, color);
    }

    /**
     * Portals are no longer binary, use type-sensitive {@link PortalRenderer#renderPortal(PortalType, PoseStack, SubmitNodeCollector, int, float, boolean, int)}
     */
    @Deprecated(forRemoval = true)
    public static void renderPortal(PoseStack poseStack, SubmitNodeCollector collector, int animationTick, float partialTicks, boolean round, boolean eldritch, int color) {
        PortalType type = eldritch ? PortalRenderer.POCKET_DIMENSION : PortalRenderer.NORMAL;
        renderPortal(type, poseStack, collector, animationTick, partialTicks, round, color);
    }

    public static void renderPortal(PortalType portalType, PoseStack poseStack, SubmitNodeCollector collector, int animationTick, float partialTicks, boolean round, int color) {
        poseStack.pushPose();
        poseStack.scale(0.0625f, 0.0625f, 0.0625f);
        Identifier texture = portalType.getTextureLocation(!round, color);
        int anim = (animationTick / ticksPerFrame) % frameCount;
        float uvMin = anim / (float) frameCount;
        float uvMax = (anim + 1) / (float) frameCount;
        collector.submitCustomGeometry(poseStack, RenderHelper.CustomerRenderType.darkGlow(texture), (pose, consumer) -> {
            vertex(pose.pose(), pose.normal(), consumer, -8, 0, 0, uvMin, 0, color);
            vertex(pose.pose(), pose.normal(), consumer, 8, 0, 0, uvMax, 0, color);
            vertex(pose.pose(), pose.normal(), consumer, 8, 32, 0, uvMax, 1f, color);
            vertex(pose.pose(), pose.normal(), consumer, -8, 32, 0, uvMin, 1f, color);
        });

        poseStack.popPose();
    }

    public static void renderPortal(PoseStack poseStack, SubmitNodeCollector collector, int animationTick, float partialTicks, boolean round) {
        renderPortal(poseStack, collector, animationTick, partialTicks, round, -1);
    }

    static int frameCount = 10;
    static int ticksPerFrame = 2;

    public static void vertex(Matrix4f pMatrix, Matrix3f pNormals, VertexConsumer pVertexBuilder, float pOffsetX, float pOffsetY, float pOffsetZ, float pTextureX, float pTextureY, int color) {
        int r = 255;
        int g = 255;
        int b = 255;
        if (color != -1) {
            r = (color & 0xFF0000) >> 16;
            g = (color & 0x00FF00) >> 8;
            b = color & 0x0000FF;
        }
        pVertexBuilder.addVertex(pMatrix, pOffsetX, pOffsetY, pOffsetZ).setColor(r, g, b, 100).setUv(pTextureX, pTextureY).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal((float) 0, (float) 0, (float) 1);
    }

    public Identifier getTextureLocation(T entity) {
        return ROUND_PORTAL;
    }


}
