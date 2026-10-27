package io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.spells.fireball.FireballRenderer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.layer.GeoRenderLayer;

@OnlyIn(Dist.CLIENT)
public class FireBossFireballChargeLayer extends GeoRenderLayer<AbstractSpellCastingMob> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/fire_boss/tyros_flame.png");
    protected final ModelPart fireball;

    public FireBossFireballChargeLayer(GeoEntityRenderer entityRendererIn, EntityRendererProvider.Context context) {
        super(entityRendererIn);
        ModelPart modelpart = context.bakeLayer(FireballRenderer.MODEL_LAYER_LOCATION);
        this.fireball = modelpart.getChild("body");
    }

    @Override
    public void render(PoseStack poseStack, AbstractSpellCastingMob animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (animatable instanceof FireBossEntity fireBoss) {
            if (fireBoss.isHalfHealthAttacking()) {
                var tick = FireBossEntity.HALF_HEALTH_ANIM_DURATION - fireBoss.halfHealthTimer;
                if (tick > FireBossEntity.HALF_HEALTH_JUMP_TIMESTAMP && tick < FireBossEntity.HALF_HEALTH_CAST_TIMESTAMP) {
                    poseStack.pushPose();
                    poseStack.rotate(Axis.YP.rotationDegrees(fireBoss.yHeadRot));
                    poseStack.translate(0, fireBoss.getBoundingBox().getYsize() * 1.25, 0);
                    float scale = Mth.lerp(tick / (float) FireBossEntity.HALF_HEALTH_ANIM_DURATION, 1f, 1.5f);
                    poseStack.scale(scale, scale, scale);
                    VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(FireballRenderer.BASE_TEXTURE));

                    float f = animatable.tickCount + partialTick;
                    float swirlX = Mth.cos(.08f * f) * 180;
                    float swirlY = Mth.sin(.08f * f) * 180;
                    float swirlZ = Mth.cos(.08f * f + 5464) * 180;
                    poseStack.rotate(Axis.XP.rotationDegrees(swirlX));
                    poseStack.rotate(Axis.YP.rotationDegrees(swirlY));
                    poseStack.rotate(Axis.ZP.rotationDegrees(swirlZ));

                    fireball.render(poseStack, consumer, packedLight, packedOverlay, -1);
                    poseStack.popPose();
                }
            }
        }

    }
}
