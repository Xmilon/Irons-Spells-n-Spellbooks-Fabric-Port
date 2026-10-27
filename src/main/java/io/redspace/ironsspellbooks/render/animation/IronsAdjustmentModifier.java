package io.redspace.ironsspellbooks.render.animation;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranimcore.animation.AnimationController;
import com.zigythebird.playeranimcore.animation.layered.modifier.AdjustmentModifier;
import com.zigythebird.playeranimcore.math.Vec3f;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.function.Supplier;

/** Applies casting aim in PAL's bone coordinates, with the active cast's native fades. */
public class IronsAdjustmentModifier extends AdjustmentModifier {
    private final Supplier<PlayerAnimationController> controller;

    public IronsAdjustmentModifier(Avatar avatar, Supplier<PlayerAnimationController> controller) {
        super((name, data) -> adjustment(avatar, controller.get(), name, data.getPartialTick()));
        this.controller = controller;
    }

    @Override
    protected AnimationController getController() {
        return controller.get();
    }

    @Override
    public void fadeOut(int ticks) {
        if (instructedFadeout == 0) {
            super.fadeOut(ticks);
        }
    }

    public void resetFadeOut() {
        super.fadeOut(0);
    }

    private static Optional<PartModifier> adjustment(Avatar avatar, PlayerAnimationController controller, String name, float partialTick) {
        float yaw = Mth.lerp(partialTick, avatar.yHeadRotO - avatar.yBodyRotO, avatar.yHeadRot - avatar.yBodyRot);
        if (name.equals("head")) {
            if (controller != null && controller.get3DTransform("head").rotation.lengthSquared() != 0) {
                return Optional.of(new PartModifier(new Vec3f(0, yaw * Mth.DEG_TO_RAD, 0), Vec3f.ZERO));
            }
        } else if (name.equals("right_arm") || name.equals("left_arm")) {
            float x = Mth.wrapDegrees(Mth.lerp(partialTick, avatar.xRotO, avatar.getXRot()) * 0.65f) * Mth.DEG_TO_RAD;
            float y = Mth.wrapDegrees(yaw * 0.65f) * Mth.DEG_TO_RAD;
            Vec3f offset = Vec3f.ZERO;
            if (controller != null) {
                var position = controller.get3DTransform(name).position;
                offset = armPositionOffset(new Vec3f(position.x, position.y, position.z), x, y);
            }
            return Optional.of(new PartModifier(new Vec3f(x, y, 0), offset));
        }
        return Optional.empty();
    }

    static Vec3f armPositionOffset(Vec3f position, float pitch, float yaw) {
        // PAL reverses non-body position Y relative to Player Animator's coordinates.
        Vec3 original = new Vec3(position.x(), -position.y(), position.z());
        Vec3 rotated = original.xRot(-pitch).yRot(yaw);
        return new Vec3f((float) (rotated.x - original.x), (float) (original.y - rotated.y), (float) (rotated.z - original.z));
    }
}
