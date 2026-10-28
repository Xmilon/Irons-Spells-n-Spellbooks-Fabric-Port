package io.redspace.ironsspellbooks.render;

import io.redspace.ironsspellbooks.entity.spells.electrocute.ElectrocuteRenderer.ElectrocuteRenderState;
import io.redspace.ironsspellbooks.entity.spells.icicle.IcicleRenderer.IcicleRenderState;
import io.redspace.ironsspellbooks.entity.spells.portal.PortalRenderer.PortalRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class SpellRenderSnapshotCheck {
    public static void main(String[] args) throws Exception {
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
                ElectrocuteRenderState.class, PortalRenderState.class)) {
            for (var stateField : type.getDeclaredFields()) {
                assert !Entity.class.isAssignableFrom(stateField.getType()) : "Live entity retained in " + type;
            }
        }
        System.out.println("Spell render snapshots: cache isolation, reuse, and entity-free state passed");
    }
}
