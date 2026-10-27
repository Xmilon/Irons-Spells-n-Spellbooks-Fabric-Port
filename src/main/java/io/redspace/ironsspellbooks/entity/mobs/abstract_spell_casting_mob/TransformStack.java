package io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob;

import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.util.Mth;

/**
 * Adds procedural offsets onto the bone snapshots of one GeckoLib render pass. Bones are addressed by name;
 * missing bones are ignored.
 */
public class TransformStack {
    private final BoneSnapshots snapshots;
    private final RenderPassInfo<?> pass;

    public TransformStack(BoneSnapshots snapshots, RenderPassInfo<?> pass) {
        this.snapshots = snapshots;
        this.pass = pass;
    }

    /**
     * Calls back once this pass has placed the bone, with its world position.
     * unverified: assumes GeckoLib reports world position as the listener's third vector.
     */
    public void listenBoneWorldPosition(String bone, java.util.function.Consumer<net.minecraft.world.phys.Vec3> listener) {
        pass.addBonePositionListener(bone, (modelPos, localPos, worldPos) -> listener.accept(worldPos));
    }

    public BoneSnapshots snapshots() {
        return snapshots;
    }

    public void pushPosition(String bone, float x, float y, float z) {
        snapshots.ifPresent(bone, snapshot -> snapshot.setTranslation(snapshot.getTranslateX() + x, snapshot.getTranslateY() + y, snapshot.getTranslateZ() + z));
    }

    public void pushPosition(String bone, org.joml.Vector3f v) {
        pushPosition(bone, v.x, v.y, v.z);
    }

    public void pushRotation(String bone, org.joml.Vector3f v) {
        pushRotation(bone, v.x, v.y, v.z);
    }

    public void pushRotation(String bone, float x, float y, float z) {
        snapshots.ifPresent(bone, snapshot -> snapshot.setRotation(wrapRadians(snapshot.getRotX() + x), wrapRadians(snapshot.getRotY() + y), wrapRadians(snapshot.getRotZ() + z)));
    }

    public void pushRotationDegrees(String bone, float x, float y, float z) {
        pushRotation(bone, x * Mth.DEG_TO_RAD, y * Mth.DEG_TO_RAD, z * Mth.DEG_TO_RAD);
    }

    public void setRotX(String bone, float value) {
        snapshots.ifPresent(bone, snapshot -> snapshot.setRotX(value));
    }

    public void setRotY(String bone, float value) {
        snapshots.ifPresent(bone, snapshot -> snapshot.setRotY(value));
    }

    public void setRotZ(String bone, float value) {
        snapshots.ifPresent(bone, snapshot -> snapshot.setRotZ(value));
    }

    public static float wrapRadians(float pValue) {
        float twoPi = 6.2831f;
        float pi = 3.14155f;
        float f = pValue % twoPi;
        if (f >= pi) {
            f -= twoPi;
        }

        if (f < -pi) {
            f += twoPi;
        }

        return f;
    }
}
