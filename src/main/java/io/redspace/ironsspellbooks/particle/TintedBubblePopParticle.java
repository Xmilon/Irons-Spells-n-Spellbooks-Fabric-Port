package io.redspace.ironsspellbooks.particle;

import io.redspace.ironsspellbooks.block.alchemist_cauldron.AlchemistCauldronTile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
public class TintedBubblePopParticle extends SingleQuadParticle {
    private final SpriteSet sprites;

    protected TintedBubblePopParticle(
            ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites, BlockPos cauldronPos
    ) {
        super(level, x, y, z, sprites.first());
        this.sprites = sprites;
        this.lifetime = 4;
        this.gravity = 0.008F;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.setSpriteFromAge(sprites);
        applyCauldronTint(level, cauldronPos);
    }

    private void applyCauldronTint(ClientLevel level, BlockPos cauldronPos) {
        int color = 0xFFFFFFFF;
        float scale = 2.5f;
        this.rCol = Mth.clamp(ARGB.red(color) * scale, 0, 255) / 255.0F;
        this.gCol = Mth.clamp(ARGB.green(color) * scale, 0, 255) / 255.0F;
        this.bCol = Mth.clamp(ARGB.blue(color) * scale, 0, 255) / 255.0F;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.yd = this.yd - (double) this.gravity;
            this.move(this.xd, this.yd, this.zd);
            this.setSpriteFromAge(this.sprites);
        }
    }

    @Override
    public @NotNull Layer getLayer() {
        return Layer.OPAQUE;
    }
    public static class Provider implements ParticleProvider<TintedBubblePopParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(
                @NotNull TintedBubblePopParticleOptions options,
                @NotNull ClientLevel level,
                double x,
                double y,
                double z,
                double xSpeed,
                double ySpeed,
                double zSpeed
        , net.minecraft.util.RandomSource random) {
            return new TintedBubblePopParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites, options.cauldronPos());
        }
    }
}
