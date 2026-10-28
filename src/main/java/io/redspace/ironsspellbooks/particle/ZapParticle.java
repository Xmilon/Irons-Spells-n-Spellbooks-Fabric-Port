package io.redspace.ironsspellbooks.particle;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;

import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class ZapParticle extends MeshParticle {

    Vec3 destination;

    ZapParticle(ClientLevel pLevel, double pX, double pY, double pZ, double xd, double yd, double zd, ZapParticleOption options, SpriteSet sprites) {
        super(pLevel, pX, pY, pZ, 0, 0, 0, sprites.first());
        this.setSize(1, 1);
        this.quadSize = 1f;
        this.destination = options.getDestination();
        this.lifetime = this.random.nextIntBetweenInclusive(3, 8);
        this.rCol = 1;
        this.gCol = 1;
        this.bCol = 1;
    }

    @Override
    public void tick() {
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    public Vector3f randomVector3f(RandomSource random, float scale) {
        return new Vector3f(
                (2f * random.nextFloat() - 1f) * scale,
                (2f * random.nextFloat() - 1f) * scale,
                (2f * random.nextFloat() - 1f) * scale
        );
    }

    private void setRGBA(float r, float g, float b, float a) {
        this.rCol = r * a;
        this.gCol = g * a;
        this.bCol = b * a;
        this.alpha = 1;
    }


    @Override
    public void extractMesh(ParticleMeshRenderState consumer, Camera camera, float partialTick) {
        Vec3 vec3 = camera.position();
        float f = (float) (Mth.lerp((double) partialTick, this.xo, this.x) - vec3.x());
        float f1 = (float) (Mth.lerp((double) partialTick, this.yo, this.y) - vec3.y());
        float f2 = (float) (Mth.lerp((double) partialTick, this.zo, this.z) - vec3.z());

        Vector3f start = new Vector3f(0, 0, 0);
        Vector3f end = new Vector3f((float) (destination.x - this.x), (float) (destination.y - this.y), (float) (destination.z - this.z));
        RandomSource randomSource = RandomSource.create((age + lifetime) * 3456798L);

        int segments = randomSource.nextIntBetweenInclusive(1, 3);
        end.mul(1f / segments);
        for (int i = 0; i < segments; i++) {
            Vector3f wiggle = randomVector3f(randomSource, .2f);
            end.add(wiggle);

            drawLightningBeam(consumer, partialTick, f, f1, f2, start, end, .6f, randomSource);

            start = new Vector3f(end.x, end.y, end.z);
            end.sub(wiggle);
            end.add(end);
        }
    }

    private void drawLightningBeam(ParticleMeshRenderState consumer, float partialTick, float f, float f1, float f2, Vector3f start, Vector3f end, float chanceToBranch, RandomSource randomSource) {
        Vector3f d = new Vector3f(end.x() - start.x(), end.y() - start.y(), end.z() - start.z());
        if (d.lengthSquared() < 1.0e-12f) return;
        d.normalize();
        Vec2 heading = new Vec2((float) Math.asin(-d.y()), (float) -Mth.atan2(d.x(), d.z()));
        //quaternion.mul(Vector3f.XP.rotation((float) Math.asin(-d.y())));
        //quaternion.mul(Vector3f.YP.rotation((float) Mth.atan2(d.x(), d.z())));
        setRGBA(1, 1, 1, 1);
        tube(consumer, partialTick, f, f1, f2, heading, start, end, .06f);

        setRGBA(.25f, .7f, 1, .3f);
        tube(consumer, partialTick, f, f1, f2, heading, start, end, .11f);

        setRGBA(.25f, .7f, 1, .15f);
        tube(consumer, partialTick, f, f1, f2, heading, start, end, .25f);

        if (randomSource.nextFloat() < chanceToBranch) {
            Vector3f branch = randomVector3f(randomSource, .5f);
            branch.add(end); // branch relative to end position
            drawLightningBeam(consumer, partialTick, f, f1, f2, start, branch, chanceToBranch * .5f, randomSource);
        }
    }

    private void tube(ParticleMeshRenderState consumer, float partialTick, float f, float f1, float f2, Vec2 heading, Vector3f start, Vector3f end, float width) {
        float h = width * .5f;

        Vector3f[] left = new Vector3f[]{
                new Vector3f(-h * Mth.cos(heading.y) + start.x(), -h + start.y(), start.z() - h * Mth.sin(heading.y)),
                new Vector3f(-h * Mth.cos(heading.y) + start.x(), h + start.y(), start.z() - h * Mth.sin(heading.y)),
                new Vector3f(-h * Mth.cos(heading.y) + end.x(), h + end.y(), end.z() - h * Mth.sin(heading.y)),
                new Vector3f(-h * Mth.cos(heading.y) + end.x(), -h + end.y(), end.z() - h * Mth.sin(heading.y))
        };
        Vector3f[] right = new Vector3f[]{
                new Vector3f(h * Mth.cos(heading.y) + end.x(), -h + end.y(), end.z() + h * Mth.sin(heading.y)),
                new Vector3f(h * Mth.cos(heading.y) + end.x(), h + end.y(), end.z() + h * Mth.sin(heading.y)),
                new Vector3f(h * Mth.cos(heading.y) + start.x(), h + start.y(), start.z() + h * Mth.sin(heading.y)),
                new Vector3f(h * Mth.cos(heading.y) + start.x(), -h + start.y(), start.z() + h * Mth.sin(heading.y))
        };
        Vector3f[] top = new Vector3f[]{
                new Vector3f(h * Mth.cos(heading.y) + start.x(), -h + start.y(), start.z() + h * Mth.sin(heading.y)),
                new Vector3f(-h * Mth.cos(heading.y) + start.x(), -h + start.y(), start.z() - h * Mth.sin(heading.y)),
                new Vector3f(-h * Mth.cos(heading.y) + end.x(), -h + end.y(), end.z() - h * Mth.sin(heading.y)),
                new Vector3f(h * Mth.cos(heading.y) + end.x(), -h + end.y(), end.z() + h * Mth.sin(heading.y))
        };
        Vector3f[] bottom = new Vector3f[]{
                new Vector3f(h * Mth.cos(heading.y) + end.x(), h + end.y(), end.z() + h * Mth.sin(heading.y)),
                new Vector3f(-h * Mth.cos(heading.y) + end.x(), h + end.y(), end.z() - h * Mth.sin(heading.y)),
                new Vector3f(-h * Mth.cos(heading.y) + start.x(), h + start.y(), start.z() - h * Mth.sin(heading.y)),
                new Vector3f(h * Mth.cos(heading.y) + start.x(), h + start.y(), start.z() + h * Mth.sin(heading.y))
        };

        quad(consumer, partialTick, f, f1, f2, left);
        quad(consumer, partialTick, f, f1, f2, right);
        quad(consumer, partialTick, f, f1, f2, top);
        quad(consumer, partialTick, f, f1, f2, bottom);
    }

    private void makeCornerVertex(ParticleMeshRenderState pConsumer, Vector3f pVec3f, float p_233996_, float p_233997_, int p_233998_) {
        pConsumer.addVertex(getLayer(), pVec3f.x(), pVec3f.y(), pVec3f.z(), p_233996_, p_233997_, ARGB.colorFromFloat(this.alpha, this.rCol, this.gCol, this.bCol), p_233998_);
    }

    private void quad(ParticleMeshRenderState pConsumer, float partialTick, float f, float f1, float f2, Vector3f[] avector3f) {
        float f3 = this.getQuadSize(partialTick);

        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
//            vector3f.transform(quaternion);
            vector3f.mul(f3);
            //vector3f.mul(8);
            vector3f.add(f, f1, f2);
        }

        int j = this.getLightCoords(partialTick);
        this.makeCornerVertex(pConsumer, avector3f[0], this.getU1(), this.getV1(), j);
        this.makeCornerVertex(pConsumer, avector3f[1], this.getU1(), this.getV0(), j);
        this.makeCornerVertex(pConsumer, avector3f[2], this.getU0(), this.getV0(), j);
        this.makeCornerVertex(pConsumer, avector3f[3], this.getU0(), this.getV1(), j);
    }

    @NotNull
    @Override
    public Layer getLayer() {
        return ParticleRenderLayers.ADDITIVE;
    }


    @Override
    protected int getLightCoords(float pPartialTick) {
        return LightCoordsUtil.FULL_BRIGHT;
    }

    public static class Provider implements ParticleProvider<ZapParticleOption> {
        private final SpriteSet sprite;

        public Provider(SpriteSet pSprite) {
            this.sprite = pSprite;
        }

        public Particle createParticle(@NotNull ZapParticleOption options, @NotNull ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, RandomSource random) {
            var particle = new ZapParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, options, this.sprite);
            particle.setSprite(this.sprite.get(random));
            particle.setAlpha(1.0F);
            return particle;
        }
    }

}
