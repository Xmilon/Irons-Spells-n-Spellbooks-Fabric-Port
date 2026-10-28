package io.redspace.ironsspellbooks.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.particle.SingleQuadParticle.Layer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParticleMeshRenderState extends QuadParticleRenderState {
    private record Vertex(float x, float y, float z, float u, float v, int color, int light) { }
    private final Map<Layer, List<Vertex>> meshes = new HashMap<>();

    public void addVertex(Layer layer, float x, float y, float z, float u, float v, int color, int light) {
        meshes.computeIfAbsent(layer, ignored -> new ArrayList<>()).add(new Vertex(x, y, z, u, v, color, light));
    }

    @Override
    public void buildLayer(Layer layer, VertexConsumer consumer) {
        super.buildLayer(layer, consumer);
        var vertices = meshes.getOrDefault(layer, List.of());
        if (vertices.size() % 4 != 0) throw new IllegalStateException("Particle meshes require complete quads");
        for (var vertex : vertices) {
            consumer.addVertex(vertex.x, vertex.y, vertex.z).setUv(vertex.u, vertex.v)
                    .setColor(vertex.color).setLight(vertex.light);
        }
    }

    @Override
    public Set<Layer> layers() {
        var layers = new HashSet<>(super.layers());
        layers.addAll(meshes.keySet());
        return layers;
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() && meshes.isEmpty();
    }

    @Override
    public void clear() {
        super.clear();
        meshes.clear();
    }

    @Override
    public void submit(SubmitNodeCollector collector, CameraRenderState camera) {
        if (!isEmpty()) collector.submitQuadParticleGroup(this);
    }
}
