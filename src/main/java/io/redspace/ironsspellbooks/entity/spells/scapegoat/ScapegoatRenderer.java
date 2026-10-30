package io.redspace.ironsspellbooks.entity.spells.scapegoat;


import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironsspellbooks.render.GeoLivingEntityRenderer;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class ScapegoatRenderer extends GeoLivingEntityRenderer<ScapegoatEntity> {
    public ScapegoatRenderer(EntityRendererProvider.Context context) {
        super(context, new ScapegoatModel());
        this.shadowRadius = 0.7f;
        this.shadowStrength = 0.4f;
    }

    @Override
    public void extractRenderState(ScapegoatEntity entity, EntityRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.lightCoords = LightCoordsUtil.FULL_BRIGHT;
    }
}
