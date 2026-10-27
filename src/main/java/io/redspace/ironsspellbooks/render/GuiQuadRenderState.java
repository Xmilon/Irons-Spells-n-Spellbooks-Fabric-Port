package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Owns the captured values so later builder or GUI pose changes cannot alter a submitted quad. */
record GuiQuadRenderState(RenderPipeline pipeline, TextureSetup textureSetup, List<Vertex> vertices,
                          boolean hasUv, boolean hasNormal, @Nullable Integer light, @Nullable Integer overlay,
                          @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds)
        implements GuiElementRenderState {
    GuiQuadRenderState(RenderPipeline pipeline, TextureSetup textureSetup, List<Vertex> vertices,
                       boolean hasUv, boolean hasNormal, @Nullable Integer light, @Nullable Integer overlay,
                       @Nullable ScreenRectangle scissorArea) {
        this(pipeline, textureSetup, List.copyOf(vertices), hasUv, hasNormal, light, overlay, scissorArea,
                bounds(vertices, scissorArea));
    }

    GuiQuadRenderState {
        vertices = List.copyOf(vertices);
    }

    private static ScreenRectangle bounds(List<Vertex> vertices, @Nullable ScreenRectangle scissor) {
        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;
        for (Vertex vertex : vertices) {
            minX = Math.min(minX, vertex.x);
            minY = Math.min(minY, vertex.y);
            maxX = Math.max(maxX, vertex.x);
            maxY = Math.max(maxY, vertex.y);
        }
        int x = (int) Math.floor(minX);
        int y = (int) Math.floor(minY);
        ScreenRectangle bounds = new ScreenRectangle(x, y, (int) Math.ceil(maxX) - x, (int) Math.ceil(maxY) - y);
        return scissor == null ? bounds : bounds.intersection(scissor);
    }

    @Override
    public void buildVertices(VertexConsumer consumer) {
        for (Vertex vertex : vertices) {
            consumer.addVertex(vertex.x, vertex.y, vertex.z).setColor(vertex.color);
            if (hasUv) {
                consumer.setUv(vertex.u, vertex.v);
            }
            if (hasNormal) {
                consumer.setNormal(vertex.normalX, vertex.normalY, vertex.normalZ);
            }
            if (light != null) {
                consumer.setLight(light);
            }
            if (overlay != null) {
                consumer.setOverlay(overlay);
            }
        }
    }

    record Vertex(float x, float y, float z, int color, float u, float v,
                  float normalX, float normalY, float normalZ) {
    }
}
