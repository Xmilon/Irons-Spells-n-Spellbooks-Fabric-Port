package io.redspace.ironsspellbooks.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class FierySmokeParticle extends SingleQuadParticle {
    private final SpriteSet sprites;
    private final boolean mirrored;

    public FierySmokeParticle(ClientLevel level, double xCoord, double yCoord, double zCoord, SpriteSet spriteSet, double xd, double yd, double zd) {
        super(level, xCoord, yCoord, zCoord, xd, yd, zd, spriteSet.first());
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.scale(this.random.nextFloat() * 2.5f + 1.5f);
        this.lifetime = 10 + (int) (Math.random() * 30);
        sprites = spriteSet;
        this.setSpriteFromAge(spriteSet);
        this.gravity = -0.0025F;
        this.mirrored = this.random.nextBoolean();
    }

    private float cameraAlphaFactor(float min, float range) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null/* || !minecraft.options.getCameraType().isFirstPerson()*/) {
            return 1f;
        }
        float factor = (float) ((minecraft.getEntityRenderDispatcher().camera.position().distanceToSqr(this.x, this.y, this.z) - 1) / range);
        return Mth.clamp(factor, min, 1.0F);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        this.alpha = Mth.clampedLerp(1, 0, (age - lifetime + 8) / 8f) * cameraAlphaFactor(.5f, 5);
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            move(xd, yd, zd);
            this.xd += this.random.nextFloat() / 500.0F * (float) (this.random.nextBoolean() ? 1 : -1);
            this.yd += this.random.nextFloat() / 100.0F - this.gravity;
            this.zd += this.random.nextFloat() / 500.0F * (float) (this.random.nextBoolean() ? 1 : -1);
            this.setSpriteFromAge(this.sprites);
            this.scale(1.023f);
        }
    }

    @Override
    protected float getU0() {
        return mirrored ? super.getU1() : super.getU0();
    }

    @Override
    protected float getU1() {
        return mirrored ? super.getU0() : super.getU1();
    }

    @Override
    public Layer getLayer() {
        return Layer.TRANSLUCENT;
    }
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz, net.minecraft.util.RandomSource random) {
            return new FierySmokeParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }

    @Override
    public int getLightCoords(float p_107564_) {
        return LightCoordsUtil.FULL_BRIGHT;
    }
}
