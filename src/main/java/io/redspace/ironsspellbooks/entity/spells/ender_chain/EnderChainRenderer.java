package io.redspace.ironsspellbooks.entity.spells.ender_chain;

import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.render.RenderHelper;
import io.redspace.ironsspellbooks.render.SpellRenderingHelper;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import io.redspace.ironsspellbooks.render.EntityBackedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.entity.PartEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class EnderChainRenderer extends EntityBackedRenderer<EnderChain> {
    public static final Identifier CHAIN_TEXTURE = IronsSpellbooks.id("textures/entity/ender_chain.png");

    public EnderChainRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0f;
    }

    @Override
    protected void submit(@NotNull EnderChain entity, float partialTick, int packedLight, @NotNull PoseStack poseStack, @NotNull SubmitNodeCollector collector, @NotNull CameraRenderState camera) {
        PartEntity<?>[] parts = entity.getParts();
        if (parts == null || parts.length == 0) {
            return;
        }
        PartEntity<?> last = parts[parts.length - 1];
        Vec3 lastWorldpos = new Vec3(Mth.lerp(partialTick, last.xo, last.getX()), Mth.lerp(partialTick, last.yo, last.getY()), Mth.lerp(partialTick, last.zo, last.getZ()));
        Vec3 targetPos = lastWorldpos.subtract(entity.position());
        float warmup = Mth.clamp((entity.warmup + partialTick) / EnderChain.VISUAL_WARMUP_TIME, 0, 1f);
        renderChainBetween(Vec3.ZERO, targetPos, poseStack, collector, warmup);
    }

    public static void renderChainBetween(Vec3 start, Vec3 to, PoseStack poseStack, SubmitNodeCollector collector) {
        renderChainBetween(start, to, poseStack, collector, 1f);
    }

    public static void renderChainBetween(Vec3 start, Vec3 to, PoseStack poseStack, SubmitNodeCollector collector, float warmupPercent) {
        poseStack.pushPose();
        poseStack.translate(start.x, start.y, start.z);

        Vec3 delta = to.subtract(start);
        float distance = (float) delta.length();
        if (distance < 1.0E-4f) {
            poseStack.popPose();
            return;
        }
        Vec3 direction = delta.normalize();
        Quaternionf rotation = new Quaternionf().rotationTo(
                new Vector3f(0, 0, 1),
                new Vector3f((float) direction.x, (float) direction.y, (float) direction.z)
        );
        poseStack.rotate(rotation);

        Vec3 origin = Vec3.ZERO;
        Vec3 destination = new Vec3(0, 0, distance);
        if (warmupPercent < 1) {
            origin = destination.scale(1 - warmupPercent);
        }
        final Vec3 from = origin;
        collector.submitCustomGeometry(poseStack, RenderHelper.CustomerRenderType.magicNoCull(CHAIN_TEXTURE), (pose, consumer) -> {
            SpellRenderingHelper.drawQuad(from, destination, 1, 0, pose, consumer, 255, 255, 255, 255, 0, distance);
            SpellRenderingHelper.drawQuad(from, destination, 0, 1, pose, consumer, 255, 255, 255, 255, 0, distance);
        });

        poseStack.popPose();
    }

    public Identifier getTextureLocation(EnderChain entity) {
        return CHAIN_TEXTURE;
    }


}
