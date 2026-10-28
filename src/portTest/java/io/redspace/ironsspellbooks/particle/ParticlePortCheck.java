package io.redspace.ironsspellbooks.particle;

import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.SingleQuadParticle.Layer;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public final class ParticlePortCheck {
    // Geometry-only fixtures override UVs; no GPU or texture upload is needed.
    private static final SpriteSet SPRITES = new SpriteSet() {
        public TextureAtlasSprite first() { return null; }
        public TextureAtlasSprite get(int age, int lifetime) { return null; }
        public TextureAtlasSprite get(RandomSource random) { return null; }
    };

    public static void main(String[] args) {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
        checkGroundQuads();
        checkMeshes();
        checkDestinations();
        checkBlockSnapshots();
        assert ParticleRenderLayers.ADDITIVE.oitPipelineSet() != null : "Additive particles lack OIT";
        System.out.println("Particle snapshots, ground quads, short beams, destination codecs and block ownership passed");
    }

    private static void checkGroundQuads() {
        var camera = new Camera();
        for (float scale : new float[]{0, .1f, 4, -4}) {
            var particle = new GroundPulse(scale);
            var state = new ParticleMeshRenderState();
            particle.extract(state, camera, .5f);
            var before = vertices(state, Layer.TRANSLUCENT);
            assert before.size() == 8 : "Ground pulse lost a face";
            for (var vertex : before) assert Math.abs(vertex[1] - .08f) < 1e-5 : "Ground offset changed";
            for (var vertex : before.subList(0, 4)) {
                if (vertex[0] > 0) assert vertex[3] == 0 : "Ground texture mirrored at positive X";
                if (vertex[0] < 0) assert vertex[3] == 1 : "Ground texture mirrored at negative X";
            }
            particle.setPos(100, 200, 300);
            assert sameVertices(before, vertices(state, Layer.TRANSLUCENT)) : "Live particle changed captured quads";
            state.clear();
            assert state.isEmpty() && vertices(state, Layer.TRANSLUCENT).isEmpty() : "Clear retained particle geometry";
        }
    }

    private static void checkMeshes() {
        for (double length : new double[]{0, .1, 3, 20}) {
            var ray = new Ray(length);
            var state = new ParticleMeshRenderState();
            ray.extractMesh(state, new Camera(), .5f);
            var before = vertices(state, Layer.TRANSLUCENT);
            assert (length == 0) == before.isEmpty() : "Short ray was lost or zero ray was drawn";
            assert before.size() % 4 == 0;
            ray.setPos(100, 100, 100);
            assert sameVertices(before, vertices(state, Layer.TRANSLUCENT));
        }
        var zap = new Zap();
        var mesh = new ParticleMeshRenderState();
        zap.extractMesh(mesh, new Camera(), .5f);
        var before = vertices(mesh, ParticleRenderLayers.ADDITIVE);
        assert !before.isEmpty() && before.size() % 4 == 0;
        zap.setPos(100, 100, 100);
        assert sameVertices(before, vertices(mesh, ParticleRenderLayers.ADDITIVE)) : "Zap retained a live particle";
        // Both native quads and custom meshes must reach the same native particle renderer.
        mesh.add(Layer.OPAQUE, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 1, -1, LightCoordsUtil.FULL_BRIGHT);
        assert vertices(mesh, Layer.OPAQUE).size() == 4 && mesh.layers().size() == 2;
        var submissions = new java.util.concurrent.atomic.AtomicInteger();
        var collector = (SubmitNodeCollector) Proxy.newProxyInstance(
                SubmitNodeCollector.class.getClassLoader(), new Class<?>[]{SubmitNodeCollector.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("submitQuadParticleGroup")) {
                        assert args[0] == mesh;
                        submissions.incrementAndGet();
                    }
                    return null;
                });
        mesh.submit(collector, null);
        assert submissions.get() == 1 : "Custom meshes did not reach the native renderer";
        mesh.clear();
        mesh.submit(collector, null);
        assert submissions.get() == 1 : "Empty state was submitted";
        assert mesh.isEmpty() && vertices(mesh, Layer.OPAQUE).isEmpty()
                && vertices(mesh, ParticleRenderLayers.ADDITIVE).isEmpty();
    }


    private static void checkDestinations() {
        var destination = new Vec3(-12.34, .26, 19.91);
        var zap = new ZapParticleOption(destination);
        var json = ZapParticleOption.MAP_CODEC.codec().encodeStart(JsonOps.INSTANCE, zap).getOrThrow();
        var zapCopy = ZapParticleOption.MAP_CODEC.codec().parse(JsonOps.INSTANCE, json).getOrThrow();
        assert zapCopy.getDestination().distanceTo(new Vec3(-12.3, .3, 19.9)) < 1e-6;
        var bytes = Unpooled.buffer();
        try {
            ZapParticleOption.STREAM_CODEC.encode(bytes, zap);
            assert bytes.readableBytes() == 12 : "Zap packet layout changed";
            assert ZapParticleOption.STREAM_CODEC.decode(bytes).getDestination().equals(zapCopy.getDestination());
        } finally { bytes.release(); }
        var ray = new SoulfireRayParticleOptions(destination);
        json = SoulfireRayParticleOptions.MAP_CODEC.codec().encodeStart(JsonOps.INSTANCE, ray).getOrThrow();
        assert SoulfireRayParticleOptions.MAP_CODEC.codec().parse(JsonOps.INSTANCE, json).getOrThrow()
                .getDestination().distanceTo(new Vec3(-12.3, .3, 19.9)) < 1e-6;
        bytes = Unpooled.buffer();
        try {
            SoulfireRayParticleOptions.STREAM_CODEC.encode(bytes, ray);
            assert bytes.readableBytes() == 24 : "Soulfire packet layout changed";
            assert SoulfireRayParticleOptions.STREAM_CODEC.decode(bytes).getDestination().equals(destination);
        } finally { bytes.release(); }
        for (String invalid : List.of("[]", "[1,2]", "[1,2,3,4]")) {
            var malformed = JsonParser.parseString("{\"destination\":" + invalid + "}");
            assert ZapParticleOption.MAP_CODEC.codec().parse(JsonOps.INSTANCE, malformed).error().isPresent();
            assert SoulfireRayParticleOptions.MAP_CODEC.codec().parse(JsonOps.INSTANCE, malformed).error().isPresent();
        }
        var shock = new ShockwaveParticleOptions(new Vector3f(.2f, .4f, .6f), 4, true);
        var encoded = ShockwaveParticleOptions.CODEC.codec().encodeStart(JsonOps.INSTANCE, shock).getOrThrow();
        var decoded = ShockwaveParticleOptions.CODEC.codec().parse(JsonOps.INSTANCE, encoded).getOrThrow();
        assert decoded.color().equals(shock.color()) && decoded.getScale() == 4 && decoded.isFullbright();
    }

    private static void checkBlockSnapshots() {
        var blocks = new ArrayList<FallingBlockParticle.BlockInstance>();
        blocks.add(new FallingBlockParticle.BlockInstance(new Vec3(1, 2, 3), new BlockModelRenderState()));
        var state = new FallingBlockParticle.RenderState(blocks);
        blocks.clear();
        assert state.blocks().size() == 1 : "Falling block state retained the queue";
        try { state.blocks().clear(); throw new AssertionError("Mutable block snapshot"); }
        catch (UnsupportedOperationException expected) { }
    }

    private static List<float[]> vertices(ParticleMeshRenderState state, Layer layer) {
        var values = new ArrayList<float[]>();
        VertexConsumer consumer = (VertexConsumer) Proxy.newProxyInstance(VertexConsumer.class.getClassLoader(),
                new Class<?>[]{VertexConsumer.class}, (proxy, method, args) -> {
                    if (method.getName().equals("addVertex")) {
                        var vertex = new float[]{(float) args[0], (float) args[1], (float) args[2], 0, 0};
                        for (float value : vertex) assert Float.isFinite(value) : "Non-finite particle geometry";
                        values.add(vertex);
                    }
                    if (method.getName().equals("setUv")) {
                        values.getLast()[3] = (float) args[0];
                        values.getLast()[4] = (float) args[1];
                    }
                    return method.getReturnType() == VertexConsumer.class ? proxy : null;
                });
        state.buildLayer(layer, consumer);
        return values;
    }

    private static boolean sameVertices(List<float[]> a, List<float[]> b) {
        if (a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); i++) if (!java.util.Arrays.equals(a.get(i), b.get(i))) return false;
        return true;
    }

    private static class GroundPulse extends ShockwaveParticle {
        GroundPulse(float scale) { super(null, 0, 0, 0, 0, 0, 0, SPRITES,
                new ShockwaveParticleOptions(new Vector3f(1), scale, true)); }
        protected float getU0() { return 0; }
        protected float getU1() { return 1; }
        protected float getV0() { return 0; }
        protected float getV1() { return 1; }
    }

    private static class Ray extends SoulfireRayParticle {
        Ray(double length) { super(null, 0, 0, 0, 0, 0, 0,
                new SoulfireRayParticleOptions(new Vec3(0, 0, length)), SPRITES); }
        protected float getU0() { return 0; }
        protected float getU1() { return 1; }
        protected float getV0() { return 0; }
        protected float getV1() { return 1; }
    }

    private static class Zap extends ZapParticle {
        Zap() { super(null, 0, 0, 0, 0, 0, 0, new ZapParticleOption(new Vec3(1, 2, 3)), SPRITES); }
        protected float getU0() { return 0; }
        protected float getU1() { return 1; }
        protected float getV0() { return 0; }
        protected float getV1() { return 1; }
    }
}
