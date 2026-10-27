package io.redspace.ironsspellbooks.entity.spells.ender_chain;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import io.redspace.ironsspellbooks.render.EntityBackedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ArcaneShackleRenderer extends EntityBackedRenderer<ArcaneShackleProjectile> {
    public ArcaneShackleRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0f;
    }

    @Override
    protected void submit(@NotNull ArcaneShackleProjectile entity, float partialTick, int packedLight, @NotNull PoseStack poseStack, @NotNull SubmitNodeCollector collector, @NotNull CameraRenderState camera) {
        poseStack.pushPose();
        for (int i = 0; i < 3; i++) {
            poseStack.rotateDegrees(Axis.YP, (entity.tickCount + partialTick) * 8);
            poseStack.pushPose();
            float scale = (1 + i * .4f) * 0.5f;
            poseStack.scale(scale, scale, scale);
            float radius = 0.5f;
            poseStack.rotateDegrees(Axis.ZP, 40 * (i - 1));
            renderChainRing(poseStack, collector, radius);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    public static void renderChainRing(@NotNull PoseStack poseStack, @NotNull SubmitNodeCollector collector, float radius) {
        Vec3[] corners = new Vec3[]{new Vec3(1, 0, 1), new Vec3(-1, 0, 1), new Vec3(-1, 0, -1), new Vec3(1, 0, -1)};
        for (int i = 0; i < 4; i++) {
            EnderChainRenderer.renderChainBetween(corners[i].scale(radius), corners[(i + 1) % 4].scale(radius), poseStack, collector);
        }
    }

    public Identifier getTextureLocation(ArcaneShackleProjectile entity) {
        return EnderChainRenderer.CHAIN_TEXTURE;
    }


}
