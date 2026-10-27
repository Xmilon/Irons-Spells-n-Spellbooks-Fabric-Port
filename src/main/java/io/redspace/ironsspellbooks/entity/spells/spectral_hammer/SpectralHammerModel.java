package io.redspace.ironsspellbooks.entity.spells.spectral_hammer;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.spells.shield.ShieldRenderer;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;

public class SpectralHammerModel extends GeoModel<SpectralHammer> {
    public static final Identifier modelResource = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "geo/spectral_hammer.geo.json");
    public static final Identifier textureResource = ShieldRenderer.SPECTRAL_OVERLAY_TEXTURE;
    public static final Identifier animationResource = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "animations/spectral_hammer.animation.json");

    @Override
    public Identifier getModelResource(SpectralHammer object) {
        return modelResource;
    }

    @Override
    public Identifier getTextureResource(SpectralHammer object) {
        return textureResource;
    }

    @Override
    public Identifier getAnimationResource(SpectralHammer animatable) {
        return animationResource;
    }
}
