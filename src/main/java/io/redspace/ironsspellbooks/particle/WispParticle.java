package io.redspace.ironsspellbooks.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.core.particles.SimpleParticleType;

public class WispParticle extends SingleQuadParticle {
    private final SpriteSet sprites;

    public WispParticle(ClientLevel level, double xCoord, double yCoord, double zCoord, SpriteSet spriteSet, double xd, double yd, double zd) {

        super(level, xCoord, yCoord, zCoord, xd, yd, zd, spriteSet.first());

        //this.friction = 1.0f;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.quadSize *= 1f;
        this.scale(2.5f);
        this.lifetime = 5 + (int) (Math.random() * 10);
        sprites = spriteSet;
        this.gravity = 0F;
        this.setSpriteFromAge(spriteSet);

        this.rCol = 1f;
        this.gCol = 1f;
        this.bCol = 1f;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public Layer getLayer() {
        return Layer.OPAQUE;
    }
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz, net.minecraft.util.RandomSource random) {
            return new WispParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }

    @Override
    protected int getLightCoords(float pPartialTick) {
        return LightCoordsUtil.FULL_BRIGHT;
    }
}
