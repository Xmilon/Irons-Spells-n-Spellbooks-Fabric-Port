package io.redspace.ironsspellbooks.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import io.redspace.ironsspellbooks.render.IExtendedSimpleTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SimpleTexture.class)
public class SimpleTextureMixin implements IExtendedSimpleTexture {
    @Unique
    boolean irons_spellbooks$isRectangular;

    @Inject(method = "loadContents", at = @At("RETURN"))
    void irons_spellbooks$captureImageData(net.minecraft.server.packs.resources.ResourceManager resourceManager, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<net.minecraft.client.renderer.texture.TextureContents> cir) {
        NativeImage image = cir.getReturnValue().image();
        irons_spellbooks$isRectangular = image.getHeight() != image.getWidth();
    }

    @Override
    public boolean irons_spellbooks$isRectangular() {
        return irons_spellbooks$isRectangular;
    }
}
