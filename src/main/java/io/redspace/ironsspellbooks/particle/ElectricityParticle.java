package io.redspace.ironsspellbooks.particle;

import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.core.particles.SimpleParticleType;

public class ElectricityParticle extends SingleQuadParticle {
    private final SpriteSet sprites;

    public ElectricityParticle(ClientLevel level, double xCoord, double yCoord, double zCoord, SpriteSet spriteSet, double xd, double yd, double zd) {

        super(level, xCoord, yCoord, zCoord, xd, yd, zd, spriteSet.first());


        this.friction = .77f;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.quadSize *= 1f;
        this.scale(1.5f);
        this.lifetime = 5 + (int) (Math.random() * 15);
        sprites = spriteSet;
        this.gravity = 0.0F;
        this.setSpriteFromAge(spriteSet);

        this.rCol = 1f;
        this.gCol = 1f;
        this.bCol = 1f;

    }

    @Override
    public void tick() {
        super.tick();
        float xj = (this.random.nextFloat() / 12.0F * (float) (this.random.nextBoolean() ? 1 : -1));
        float yj = (this.random.nextFloat() / 12.0F * (float) (this.random.nextBoolean() ? 1 : -1));
        float zj = (this.random.nextFloat() / 12.0F * (float) (this.random.nextBoolean() ? 1 : -1));
        setPos(x + xj, y + yj, z + zj);
        randomlyAnimate();
    }

    private void randomlyAnimate() {
        setSprite(sprites.get(Utils.random));
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
            return new ElectricityParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }
    }

    @Override
    protected int getLightCoords(float p_107249_) {
        return LightCoordsUtil.FULL_BRIGHT;
    }
}
