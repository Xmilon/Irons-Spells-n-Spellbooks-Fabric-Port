package io.redspace.ironsspellbooks.entity.spells.wisp;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import com.geckolib.renderer.GeoEntityRenderer;

public class WispRenderer extends GeoEntityRenderer<WispEntity> {
    public static final Identifier textureLocation = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/wisp/wisp.png");

    public WispRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WispModel());
        this.shadowRadius = 0.3f;
    }

    @Override
    public Identifier getTextureLocation(WispEntity animatable) {
        return textureLocation;
    }

    @Override
    public RenderType getRenderType(WispEntity animatable, Identifier texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderTypes.energySwirl(texture, 0, 0);
    }
}
