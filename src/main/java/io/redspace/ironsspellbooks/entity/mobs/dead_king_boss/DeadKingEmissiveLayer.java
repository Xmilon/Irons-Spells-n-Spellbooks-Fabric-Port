package io.redspace.ironsspellbooks.entity.mobs.dead_king_boss;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.layer.GeoRenderLayer;

public class DeadKingEmissiveLayer extends GeoRenderLayer<AbstractSpellCastingMob> {
    public static final Identifier TEXTURE_NORMAL = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/dead_king/dead_king_glowing.png");
    public static final Identifier TEXTURE_ENRAGED = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/dead_king/dead_king_enraged_glowing.png");

    public DeadKingEmissiveLayer(GeoEntityRenderer renderer) {
        super(renderer);
    }

    public static Identifier currentTexture(AbstractSpellCastingMob entity) {
        return entity instanceof DeadKingBoss boss && boss.isPhase(DeadKingBoss.Phases.FinalPhase) ? TEXTURE_ENRAGED : TEXTURE_NORMAL;
    }

    public static Identifier currentModel(AbstractSpellCastingMob deadKingBoss) {
        return DeadKingModel.MODEL;
    }

    public static RenderType renderType(Identifier resourceLocation) {
        return RenderType.energySwirl(resourceLocation, 0, 0);
    }


    @Override
    public void render(PoseStack poseStack, AbstractSpellCastingMob animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (animatable instanceof DeadKingCorpseEntity || animatable.isInvisible())
            return;
        var model = this.getGeoModel().getBakedModel(currentModel(animatable));

        poseStack.pushPose();
        renderType = renderType(currentTexture(animatable));
        VertexConsumer vertexconsumer = bufferSource.getBuffer(renderType);
        this.getRenderer().actuallyRender(poseStack, animatable, model, renderType, bufferSource, vertexconsumer, true, partialTick,
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,-1);
        poseStack.popPose();
    }
}
