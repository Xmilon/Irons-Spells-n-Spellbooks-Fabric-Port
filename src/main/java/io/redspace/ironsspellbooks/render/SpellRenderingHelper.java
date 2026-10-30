package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.spells.CastingMobAimingData;
import io.redspace.ironsspellbooks.spells.blood.RayOfSiphoningSpell;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class SpellRenderingHelper {
    public static final Identifier SOLID = IronsSpellbooks.id("textures/entity/ray/solid.png");
    public static final Identifier BEACON = IronsSpellbooks.id("textures/entity/ray/beacon_beam.png");
    public static final Identifier STRAIGHT_GLOW = IronsSpellbooks.id("textures/entity/ray/ribbon_glow.png");
    public static final Identifier TWISTING_GLOW = IronsSpellbooks.id("textures/entity/ray/twisting_glow.png");

    public record RayState(float eyeHeight, float distance, Vec3 direction, float age) {}

    public static RayState extractRay(LivingEntity entity, String spellId, float partialTick) {
        if (!SpellRegistry.RAY_OF_SIPHONING_SPELL.get().getSpellId().equals(spellId)) return null;
        Vec3 end;
        if (entity instanceof Mob mob && MagicData.getPlayerMagicData(mob).getAdditionalCastData() instanceof CastingMobAimingData aiming) {
            end = aiming.getAimPosition(partialTick);
        } else {
            end = Utils.raycastForEntity(entity.level(), entity, RayOfSiphoningSpell.getRange(0), true).getLocation();
        }
        return new RayState(entity.getEyeHeight() * .8f, (float) entity.getEyePosition().distanceTo(end),
                entity.getLookAngle().normalize(), entity.tickCount + partialTick);
    }

    public static void submitRay(RayState state, PoseStack poseStack, SubmitNodeCollector collector) {
        if (state == null) return;
        poseStack.pushPose();
        poseStack.translate(0, state.eyeHeight(), 0);
        Vec3 direction = state.direction();
        float yRot = (float) Mth.atan2(direction.z, direction.x) - 1.5707f;
        float xRot = (float) Mth.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z));
        poseStack.rotate(Axis.YP, -yRot);
        poseStack.rotate(Axis.XP, -xRot);
        collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(BEACON, true), (pose, vertices) ->
                drawRay(state, pose, vertices, true));
        collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(TWISTING_GLOW), (pose, vertices) ->
                drawRay(state, pose, vertices, false));
        poseStack.popPose();
    }

    private static void drawRay(RayState state, PoseStack.Pose pose, VertexConsumer vertices, boolean inner) {
        float deltaUV = -state.age() % 10;
        float max = Mth.frac(deltaUV * .2f - (float) Mth.floor(deltaUV * .1f));
        float min = -1 + max;
        Vec3 start = Vec3.ZERO;
        for (float length = 1; length <= state.distance(); length += .5f) {
            Vec3 wiggle = new Vec3(Mth.sin(state.age() * .8f) * .02f,
                    Mth.sin(state.age() * .8f + 100) * .02f, Mth.cos(state.age() * .8f) * .02f);
            Vec3 end = new Vec3(0, 0, Math.min(length, state.distance())).add(wiggle);
            if (inner) {
                drawHull(start, end, .12f, .12f, pose, vertices, 178, 0, 0, 255, min, max);
            } else {
                drawQuad(start, end, .48f, 0, pose, vertices, 178, 0, 0, 255, min, max);
                drawQuad(start, end, 0, .48f, pose, vertices, 178, 0, 0, 255, min, max);
            }
            start = end;
        }
    }
    public static void drawHull(Vec3 from, Vec3 to, float width, float height, PoseStack.Pose pose, VertexConsumer consumer, int r, int g, int b, int a, float uvMin, float uvMax) {
        //Bottom
        drawQuad(from.subtract(0, height * .5f, 0), to.subtract(0, height * .5f, 0), width, 0, pose, consumer, r, g, b, a, uvMin, uvMax);
        //Top
        drawQuad(from.add(0, height * .5f, 0), to.add(0, height * .5f, 0), width, 0, pose, consumer, r, g, b, a, uvMin, uvMax);
        //Left
        drawQuad(from.subtract(width * .5f, 0, 0), to.subtract(width * .5f, 0, 0), 0, height, pose, consumer, r, g, b, a, uvMin, uvMax);
        //Right
        drawQuad(from.add(width * .5f, 0, 0), to.add(width * .5f, 0, 0), 0, height, pose, consumer, r, g, b, a, uvMin, uvMax);
    }

    public static void drawQuad(Vec3 from, Vec3 to, float width, float height, PoseStack.Pose pose, VertexConsumer consumer, int r, int g, int b, int a, float uvMin, float uvMax) {
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

        float halfWidth = width * .5f;
        float halfHeight = height * .5f;

        consumer.addVertex(poseMatrix, (float) from.x - halfWidth, (float) from.y - halfHeight, (float) from.z).setColor(r, g, b, a).setUv(0f, uvMin).setOverlay(OverlayTexture.NO_OVERLAY).setLight(240).setNormal(0f, 1f, 0f);
        consumer.addVertex(poseMatrix, (float) from.x + halfWidth, (float) from.y + halfHeight, (float) from.z).setColor(r, g, b, a).setUv(1f, uvMin).setOverlay(OverlayTexture.NO_OVERLAY).setLight(240).setNormal(0f, 1f, 0f);
        consumer.addVertex(poseMatrix, (float) to.x + halfWidth, (float) to.y + halfHeight, (float) to.z).setColor(r, g, b, a).setUv(1f, uvMax).setOverlay(OverlayTexture.NO_OVERLAY).setLight(240).setNormal(0f, 1f, 0f);
        consumer.addVertex(poseMatrix, (float) to.x - halfWidth, (float) to.y - halfHeight, (float) to.z).setColor(r, g, b, a).setUv(0f, uvMax).setOverlay(OverlayTexture.NO_OVERLAY).setLight(240).setNormal(0f, 1f, 0f);

    }
}