package io.redspace.ironsspellbooks.particle;

import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import com.mojang.math.Axis;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class BlastwaveParticle extends MeshParticle {
    private static final float DEGREES_90 = Mth.PI / 2f;
    private final float targetSize;
    private final SpriteSet sprites;

    BlastwaveParticle(ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet spriteSet, double xd, double yd, double zd, BlastwaveParticleOptions options) {
        super(pLevel, pX, pY, pZ, 0, 0, 0, spriteSet.first());

        this.xd = xd;
        this.yd = yd;
        this.zd = zd;

        this.targetSize = options.getScale();
        this.quadSize = 1;
        this.lifetime = 8;
        this.gravity = .1f;
        sprites = spriteSet;

        float f = random.nextFloat() * 0.14F + 0.85F;
        this.rCol = options.getColor().x() * f;
        this.gCol = options.getColor().y() * f;
        this.bCol = options.getColor().z() * f;
        this.friction = 1;
    }

    @Override
    public float getQuadSize(float partialTick) {
        var f = (partialTick + this.age) / (float) this.lifetime;
        return this.quadSize * Mth.lerp(1 - (1 - f) * (1 - f), targetSize * .75f, targetSize);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            //IronsSpellbooks.LOGGER.debug("Removing shockwave particle {}/{} ({}/{})", age, lifetime, getQuadSize(0), targetSize);
            this.remove();
        } else {
            this.setSpriteFromAge(sprites);
            this.move(this.xd, this.yd, this.zd);
            this.yd *= .85f;
            this.xd *= .94f;
            this.zd *= .94f;
        }
    }

    @Override
    public void extract(QuadParticleRenderState state, Camera camera, float partialTick) {
        this.alpha = 1.0F - Mth.clamp((this.age + partialTick) / (float) this.lifetime, 0, 1F);
        Vec3 cameraPosition = camera.position();
        float relativeX = (float) (Mth.lerp(partialTick, this.xo, this.x) - cameraPosition.x());
        float relativeY = (float) (Mth.lerp(partialTick, this.yo, this.y) - cameraPosition.y()) + .08f;
        float relativeZ = (float) (Mth.lerp(partialTick, this.zo, this.z) - cameraPosition.z());
        this.extractGroundQuad(state, new Quaternionf().rotateX(-DEGREES_90), relativeX, relativeY, relativeZ, partialTick);
        this.extractGroundQuad(state, new Quaternionf().rotateY(-(float) Math.PI).rotateX(DEGREES_90), relativeX, relativeY, relativeZ, partialTick);
    }
    @NotNull
    @Override
    public Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    @Override
    protected int getLightCoords(float pPartialTick) {
        return LightCoordsUtil.FULL_BRIGHT;
    }

    public static class Provider implements ParticleProvider<BlastwaveParticleOptions> {
        private final SpriteSet sprite;

        public Provider(SpriteSet pSprite) {
            this.sprite = pSprite;
        }

        public Particle createParticle(@NotNull BlastwaveParticleOptions options, @NotNull ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, RandomSource random) {
            BlastwaveParticle shriekparticle = new BlastwaveParticle(pLevel, pX, pY, pZ, sprite, pXSpeed, pYSpeed, pZSpeed, options);
            shriekparticle.setSpriteFromAge(this.sprite);
            shriekparticle.setAlpha(1.0F);
            return shriekparticle;
        }
    }

}
