package io.redspace.ironsspellbooks.entity.spells.wisp;

import net.minecraft.client.renderer.entity.state.EntityRenderState;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import io.redspace.ironsspellbooks.render.IronsGeoEntityRenderer;

public class WispRenderer extends IronsGeoEntityRenderer<WispEntity> {
    public static final Identifier textureLocation = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/wisp/wisp.png");

    public WispRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WispModel());
        this.shadowRadius = 0.3f;
    }

    @Override
    public RenderType getRenderType(EntityRenderState state, Identifier texture) {
        return RenderTypes.energySwirl(texture, 0, 0);
    }
}
