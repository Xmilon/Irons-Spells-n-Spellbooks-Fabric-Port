package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironsspellbooks.block.portal_frame.PortalFrameRenderer;
import io.redspace.ironsspellbooks.block.portal_frame.PortalFrameRenderer.PortalFrameRenderState;
import io.redspace.ironsspellbooks.entity.spells.electrocute.ElectrocuteRenderer.ElectrocuteRenderState;
import io.redspace.ironsspellbooks.entity.spells.icicle.IcicleRenderer.IcicleRenderState;
import io.redspace.ironsspellbooks.entity.spells.portal.PortalRenderer.PortalRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Proxy;

public class SpellRenderSnapshotCheck {
    public static void main(String[] args) throws Exception {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
        var beam = new ElectrocuteRenderState();
        var cache = new ArrayList<>(List.of(Vec3.ZERO, new Vec3(1, 2, 3)));
        beam.captureBeam(20, -35, .4f, cache);
        var field = ElectrocuteRenderState.class.getDeclaredField("segments");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        var captured = (List<Vec3>) field.get(beam);
        cache.clear();
        assert captured.equals(List.of(Vec3.ZERO, new Vec3(1, 2, 3))) : "Beam retained the live cache";
        assert beam.yaw == 20 && beam.pitch == -35 && beam.eyeOffset == .4f;
        try {
            captured.clear();
            throw new AssertionError("Beam snapshot is mutable");
        } catch (UnsupportedOperationException expected) { }

        // Reusing a state after losing the owner must erase the previous beam.
        beam.captureBeam(0, 0, 0, List.of());
        assert ((List<?>) field.get(beam)).isEmpty() : "Stale beam after owner disappeared";
        assert beam.yaw == 0 && beam.pitch == 0 && beam.eyeOffset == 0;
        assert captured.size() == 2 : "Later extraction changed already captured geometry";

        for (var type : List.of(EntityRenderState.class, IcicleRenderState.class,
                ElectrocuteRenderState.class, PortalRenderState.class, PortalFrameRenderState.class)) {
            for (var stateField : type.getDeclaredFields()) {
                assert !Entity.class.isAssignableFrom(stateField.getType()) : "Live entity retained in " + type;
                assert !net.minecraft.world.level.block.entity.BlockEntity.class.isAssignableFrom(stateField.getType())
                        : "Live block entity retained in " + type;
            }
        }
        checkPortalFrame();
        System.out.println("Spell render snapshots: cache isolation, reuse, and entity-free state passed");
    }

    private static void checkPortalFrame() {
        var geometry = new ArrayList<SubmitNodeCollector.CustomGeometryRenderer>();
        SubmitNodeCollector collector = (SubmitNodeCollector) Proxy.newProxyInstance(
                SubmitNodeCollector.class.getClassLoader(), new Class<?>[]{SubmitNodeCollector.class},
                (proxy, method, values) -> {
                    if (method.getName().equals("submitCustomGeometry")) {
                        geometry.add((SubmitNodeCollector.CustomGeometryRenderer) values[2]);
                    }
                    return null;
                });
        var renderer = new PortalFrameRenderer(null);
        var state = new PortalFrameRenderState();
        renderer.submit(state, new PoseStack(), collector, null);
        assert geometry.isEmpty() : "Disconnected portal was drawn";
        state.visible = true;
        state.portalType = io.redspace.ironsspellbooks.entity.spells.portal.PortalRenderer.NORMAL;
        state.animationTick = 4;
        state.color = 0x123456;
        for (var direction : List.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)) {
            state.direction = direction;
            var poses = new PoseStack();
            renderer.submit(state, poses, collector, null);
            assert poses.last().pose().equals(new org.joml.Matrix4f()) : "Portal leaked a transform";
        }
        assert geometry.size() == 4 : "Portal frame did not submit once per direction";
        state.animationTick = 18;
        state.color = 0xFFFFFF;
        var colors = new ArrayList<List<Object>>();
        var uvs = new ArrayList<List<Object>>();
        VertexConsumer consumer = (VertexConsumer) Proxy.newProxyInstance(VertexConsumer.class.getClassLoader(),
                new Class<?>[]{VertexConsumer.class}, (proxy, method, values) -> {
                    if (method.getName().equals("setColor")) colors.add(List.of(values));
                    if (method.getName().equals("setUv")) uvs.add(List.of(values));
                    return method.getReturnType() == VertexConsumer.class ? proxy : null;
                });
        geometry.getFirst().render(new PoseStack().last(), consumer);
        assert colors.size() == 4 && colors.stream().allMatch(c -> c.equals(List.of(18, 52, 86, 100)))
                : "Deferred portal tint changed after submission";
        assert uvs.equals(List.of(List.of(.2f, 0f), List.of(.3f, 0f), List.of(.3f, 1f), List.of(.2f, 1f)))
                : "Deferred portal animation changed after submission";
    }
}
