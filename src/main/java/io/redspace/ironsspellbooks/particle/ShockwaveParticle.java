package io.redspace.ironsspellbooks.particle;

import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import com.mojang.math.Axis;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;

import net.minecraft.util.LightCoordsUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class ShockwaveParticle extends MeshParticle {
    private static final float DEGREES_90 = Mth.PI / 2f;
    private final float targetSize;
    private final boolean isFullbright;

    ShockwaveParticle(ClientLevel pLevel, double pX, double pY, double pZ, double xd, double yd, double zd, SpriteSet spriteSet, ShockwaveParticleOptions options) {
        super(pLevel, pX, pY, pZ, 0, 0, 0, spriteSet.first());

        this.xd = xd;
        this.yd = yd;
        this.zd = zd;

        this.targetSize = options.getScale();
        this.quadSize = 0;
        this.lifetime = Math.max(1, targetSize < 0 ? (int) (targetSize * -20) : (int) (Math.abs(targetSize) * 2.5));
        this.gravity = .1f;

        float f = random.nextFloat() * 0.14F + 0.85F;
        this.rCol = options.color().x() * f;
        this.gCol = options.color().y() * f;
        this.bCol = options.color().z() * f;
        this.friction = 1;
        this.isFullbright = options.isFullbright();
    }

    @Override
    public float getQuadSize(float partialTick) {
        var f = (partialTick + this.age) / (float) this.lifetime;
        if (targetSize < 0) {
            return Mth.lerp((1 - f) * (1 - f), 0, -targetSize);
        } else {
            return Mth.lerp(1 - (1 - f) * (1 - f), 0, targetSize);
        }
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
            this.move(this.xd, this.yd, this.zd);
            this.yd *= .85f;
            this.xd *= .94f;
            this.zd *= .94f;
        }

    }

    static final int MAX_PARTICLES = 30;

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
        if (isFullbright) {
            return LightCoordsUtil.FULL_BRIGHT;
        }
        BlockPos blockpos = BlockPos.containing(this.x, this.y, this.z).above();
        return this.level.hasChunkAt(blockpos) ? LightCoordsUtil.getLightCoords(this.level, blockpos) : 0;
    }

    public static class Provider implements ParticleProvider<ShockwaveParticleOptions> {
        private final SpriteSet sprite;

        public Provider(SpriteSet pSprite) {
            this.sprite = pSprite;
        }

        public Particle createParticle(@NotNull ShockwaveParticleOptions options, @NotNull ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, RandomSource random) {
            ShockwaveParticle shriekparticle = new ShockwaveParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprite, options);
            shriekparticle.setSprite(this.sprite.get(random));
            shriekparticle.setAlpha(1.0F);
            return shriekparticle;
        }
    }

}
