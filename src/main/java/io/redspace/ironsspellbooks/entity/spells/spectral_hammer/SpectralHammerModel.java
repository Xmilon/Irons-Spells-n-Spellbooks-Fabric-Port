package io.redspace.ironsspellbooks.entity.spells.spectral_hammer;

import com.geckolib.renderer.base.GeoRenderState;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.spells.shield.ShieldRenderer;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;

public class SpectralHammerModel extends GeoModel<SpectralHammer> {
    public static final Identifier modelResource = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "spectral_hammer");
    public static final Identifier textureResource = ShieldRenderer.SPECTRAL_OVERLAY_TEXTURE;
    public static final Identifier animationResource = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "spectral_hammer");

    @Override
    public Identifier getModelResource(GeoRenderState state) {
        return modelResource;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        return textureResource;
    }

    @Override
    public Identifier getAnimationResource(SpectralHammer animatable) {
        return animationResource;
    }
}
