package io.redspace.ironsspellbooks.render;
import java.util.*;
import java.lang.reflect.Proxy;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import com.mojang.blaze3d.vertex.VertexConsumer;
public class GuiQuadSnapshotCheck {
    public static void main(String[] args) {
        var vertices = new ArrayList<GuiQuadRenderState.Vertex>();
        vertices.add(new GuiQuadRenderState.Vertex(-0.5f, -0.5f, 0, 0x80FF00FF, 0, 0, 0, 0, 1));
        vertices.add(new GuiQuadRenderState.Vertex(10.5f, -0.5f, 0, 0x80FF00FF, 1, 0, 0, 0, 1));
        vertices.add(new GuiQuadRenderState.Vertex(10.5f, 10.5f, 0, 0x80FF00FF, 1, 1, 0, 0, 1));
        vertices.add(new GuiQuadRenderState.Vertex(-0.5f, 10.5f, 0, 0x80FF00FF, 0, 1, 0, 0, 1));
        var scissor = new ScreenRectangle(0, 0, 8, 8);
        var state = new GuiQuadRenderState(null, null, vertices, true, true, 123, 456, scissor);
        vertices.clear();
        assert state.vertices().size() == 4 : "Geometry retained caller's mutable list";
        assert state.bounds().equals(scissor) : "Bounds did not intersect clipping rectangle";
        try { state.vertices().clear(); throw new AssertionError("Snapshot mutable"); }
        catch (UnsupportedOperationException expected) { }
        var calls = new ArrayList<String>();
        VertexConsumer recorder = (VertexConsumer) Proxy.newProxyInstance(VertexConsumer.class.getClassLoader(),
            new Class<?>[] {VertexConsumer.class}, (proxy, method, values) -> {
                calls.add(method.getName());
                return method.getReturnType() == VertexConsumer.class ? proxy : null;
            });
        state.buildVertices(recorder);
        assert Collections.frequency(calls, "addVertex") == 4;
        assert Collections.frequency(calls, "setUv") == 4;
        assert Collections.frequency(calls, "setNormal") == 4;
        assert Collections.frequency(calls, "setLight") == 4;
        assert Collections.frequency(calls, "setOverlay") == 4;
        var unclipped = new GuiQuadRenderState(null, null, state.vertices(), false, false, null, null, null);
        assert unclipped.bounds().equals(new ScreenRectangle(-1, -1, 12, 12)) : "Subpixel bounds not rounded outward";
        calls.clear();
        unclipped.buildVertices(recorder);
        assert Collections.frequency(calls, "addVertex") == 4;
        assert !calls.contains("setUv") && !calls.contains("setNormal") && !calls.contains("setLight") && !calls.contains("setOverlay");
        System.out.println("GUI geometry snapshot, clipping, subpixel bounds and attribute checks passed");
    }
}