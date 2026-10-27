package io.redspace.ironslib.internal.client.gui_block_entity_renderer;
import io.redspace.ironslib.internal.client.gui_block_entity_renderer.GuiBlockEntityRenderState;
import org.joml.Quaternionf;
import org.joml.Vector3f;

class PipSnapshotCheck {
    public static void main(String[] args) {
        var translation = new Vector3f(1, 2, 3);
        var pivot = new Vector3f(4, 5, 6);
        var rotation = new Quaternionf();
        var camera = new Quaternionf();
        var state = new GuiBlockEntityRenderState(null, translation, pivot, rotation, camera, 0, 0, 64, 64, 1, null);
        translation.set(9, 9, 9);
        pivot.set(8, 8, 8);
        rotation.rotateY(1);
        camera.rotateX(1);
        assert state.translation().x() == 1 && state.translation().y() == 2 && state.translation().z() == 3;
        assert state.rotationPivotOffset().x() == 4 && state.rotationPivotOffset().y() == 5 && state.rotationPivotOffset().z() == 6;
        assert state.rotation().equals(new Quaternionf());
        assert state.overrideCameraAngle().equals(new Quaternionf());
        var noCamera = new GuiBlockEntityRenderState(null, translation, pivot, rotation, null, 0, 0, 64, 64, 1, null);
        assert noCamera.overrideCameraAngle() == null;
        System.out.println("PIP snapshot checks passed");
    }
}
