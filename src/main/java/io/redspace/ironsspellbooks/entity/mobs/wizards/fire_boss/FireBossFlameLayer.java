package io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss;

import com.geckolib.cache.model.GeoBone;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.PerBoneRender;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.render.IronsGeoEntityRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.model.geom.PartNames;

import java.util.function.BiConsumer;

public class FireBossFlameLayer extends GeoRenderLayer<AbstractSpellCastingMob, Void, EntityRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/fire_boss/tyros_flame.png");
    private static final Identifier TEXTURE_OMINOUS = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/fire_boss/ominous_tyros_flame.png");
    static int frameCount = 8;
    static int ticksPerFrame = 1;

    public FireBossFlameLayer(GeoRenderer<AbstractSpellCastingMob, Void, EntityRenderState> renderer) {
        super(renderer);
    }

    // GeckoLib 5 hands a per-bone task a pose stack that is already at the bone pivot (translating to it again floated the flame a head above the boss).
    @Override
    public void addPerBoneRender(RenderPassInfo<EntityRenderState> info, BiConsumer<GeoBone, PerBoneRender<EntityRenderState>> consumer) {
        if (!(IronsGeoEntityRenderer.entityOf(info.renderState()) instanceof FireBossEntity boss) || !boss.isSoulMode()) {
            return;
        }
        info.model().getBone(PartNames.HEAD).ifPresent(head -> consumer.accept(head, (renderInfo, bone, collector) -> {
            var poseStack = renderInfo.poseStack();
            poseStack.pushPose();
            poseStack.rotateDegrees(Axis.YP, 45f);
            poseStack.scale(1 / 2f, 1 / 2f, 1 / 2f);
            int anim = (boss.tickCount / ticksPerFrame) % frameCount;
            float uvMin = anim / (float) frameCount;
            float uvMax = (anim + 1) / (float) frameCount;
            float halfsqrt2 = 0.7071f;
            for (int i = 0; i < 4; i++) {
                poseStack.rotateDegrees(Axis.YP, 90f);
                collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(boss.isOminous() ? TEXTURE_OMINOUS : TEXTURE), (pose, consumer2) -> {
                    consumer2.addVertex(pose.pose(), 0, 0, -halfsqrt2).setColor(255, 255, 255, 255).setUv(0f, uvMax).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
                    consumer2.addVertex(pose.pose(), 0, 1, -halfsqrt2).setColor(255, 255, 255, 255).setUv(0f, uvMin).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
                    consumer2.addVertex(pose.pose(), 0, 1, halfsqrt2).setColor(255, 255, 255, 255).setUv(1f, uvMin).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
                    consumer2.addVertex(pose.pose(), 0, 0, halfsqrt2).setColor(255, 255, 255, 255).setUv(1f, uvMax).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightCoordsUtil.FULL_BRIGHT).setNormal(0f, 1f, 0f);
                });
            }
            poseStack.popPose();
        }));
    }
}
