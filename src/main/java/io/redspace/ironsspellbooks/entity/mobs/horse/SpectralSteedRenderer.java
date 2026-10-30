package io.redspace.ironsspellbooks.entity.mobs.horse;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.SummonedHorse;
import net.minecraft.client.model.animal.equine.HorseModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.HorseRenderState;
import net.minecraft.resources.Identifier;

public class SpectralSteedRenderer extends AbstractHorseRenderer<SummonedHorse, HorseRenderState, HorseModel> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/horse/spectral_steed.png");

    public SpectralSteedRenderer(EntityRendererProvider.Context context) {
        super(context, new HorseModel(context.bakeLayer(ModelLayers.HORSE)), new HorseModel(context.bakeLayer(ModelLayers.HORSE_BABY)));
    }

    @Override
    public HorseRenderState createRenderState() {
        return new HorseRenderState();
    }

    @Override
    public Identifier getTextureLocation(HorseRenderState state) {
        return TEXTURE;
    }
}
