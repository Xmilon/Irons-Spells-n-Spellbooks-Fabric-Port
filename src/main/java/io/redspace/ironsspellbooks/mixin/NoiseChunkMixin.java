package io.redspace.ironsspellbooks.mixin;

import io.redspace.ironsspellbooks.worldgen.IExtendedNoiseChunk;
import io.redspace.ironsspellbooks.worldgen.NukedAquifer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseChunk.class)
public class NoiseChunkMixin implements IExtendedNoiseChunk {
    @Unique
    AquifierNuke irons_spellbooks$aquifierNuke = null;

    @Unique
    NukedAquifer irons_spellbooks$nukedAquifer = null;

    @Unique
    BlockState irons_spellbooks$defaultBlockState;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void irons_spellbooks$captureDefaultBlockstate(RandomState random, Beardifier beardifier, NoiseGeneratorSettings noiseGeneratorSettings, Aquifer.FluidPicker fluidPicker, Blender blendifier, DensityVolume volume, CallbackInfo ci) {
        this.irons_spellbooks$defaultBlockState = noiseGeneratorSettings.defaultBlock();
    }

    @Inject(method = "aquifer", at = @At("RETURN"), cancellable = true)
    private void irons_spellbooks$cancelAquifierGeneration(CallbackInfoReturnable<Aquifer> cir) {
        if (irons_spellbooks$aquifierNuke == null) {
            return;
        }
        if (irons_spellbooks$nukedAquifer == null) {
            irons_spellbooks$nukedAquifer = new NukedAquifer(cir.getReturnValue(), irons_spellbooks$aquifierNuke, irons_spellbooks$defaultBlockState);
        }
        cir.setReturnValue(irons_spellbooks$nukedAquifer);
    }

    @Override
    public AquifierNuke irons_spellbooks$getAquifierStatus() {
        return irons_spellbooks$aquifierNuke;
    }

    @Override
    public void irons_spellbooks$setAquifierStatus(AquifierNuke nuke) {
        this.irons_spellbooks$aquifierNuke = nuke;
        this.irons_spellbooks$nukedAquifer = null;
    }
}
