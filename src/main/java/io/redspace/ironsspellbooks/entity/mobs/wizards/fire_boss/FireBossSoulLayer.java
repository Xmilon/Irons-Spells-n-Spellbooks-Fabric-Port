package io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.render.RenderHelper;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.layer.GeoRenderLayer;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class FireBossSoulLayer extends GeoRenderLayer<AbstractSpellCastingMob> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/keeper/keeper_ghost.png");

    public FireBossSoulLayer(GeoEntityRenderer entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, AbstractSpellCastingMob animatable, BakedGeoModel bakedModel, RenderType renderType2, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        int i = animatable.hurtTime + animatable.deathTime;
        if (i <= 0) {
            return;
        }
        float alpha = (animatable.deathTime > 0) ? i / 120f : i / (float) animatable.hurtDuration * 2;

        float f = (float) animatable.tickCount + partialTick;
        var renderType = RenderType.energySwirl(TEXTURE, f * 0.02F % 1.0F, f * 0.01F % 1.0F);

        VertexConsumer vertexconsumer = bufferSource.getBuffer(renderType);
        poseStack.pushPose();
        var bones = bakedModel.topLevelBones();
        setArmorVisible(bones, false);
        this.getRenderer().actuallyRender(poseStack, animatable, bakedModel, renderType, bufferSource, vertexconsumer, true, partialTick,
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, RenderHelper.colorf(.15f * alpha, .02f * alpha, 0.0f * alpha));
        setArmorVisible(bones, true);

        poseStack.popPose();
    }

    private void setArmorVisible(List<GeoBone> bones, boolean visible) {
        for (GeoBone bone : bones) {
            if (bone != null) {
                if (bone.getName().startsWith("armor")) {
                    bone.setHidden(!visible);
                }
                setArmorVisible(bone.getChildBones(), visible);
            }
        }
    }
}
