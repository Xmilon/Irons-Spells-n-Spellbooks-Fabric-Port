package io.redspace.ironsspellbooks.entity.spells.void_tentacle;

import com.geckolib.renderer.base.GeoRenderState;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.model.GeoModel;

import java.util.List;

public class VoidTentacleModel extends GeoModel<VoidTentacle> {
    public static final Identifier modelResource = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "void_tentacle");
    public static final Identifier textureResource = IronsSpellbooks.id("textures/entity/void_tentacle/void_tentacle.png");
    public static final Identifier animationResource = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "void_tentacle_animations");

    @Override
    public Identifier getModelResource(GeoRenderState state) {
        return modelResource;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        return textureResource;
    }

    @Override
    public Identifier getAnimationResource(VoidTentacle animatable) {
        return animationResource;
    }

    @Override
    public void setCustomAnimations(VoidTentacle animatable, long instanceId, AnimationTest<VoidTentacle> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        float seed = (float) (animatable.getX() * animatable.getZ()) % 173;
        float speed = .55f;
        float f = (float) (seed + animatable.tickCount + animationState.getPartialTick()) * speed;
        List<GeoBone> bones = List.of(
                this.getAnimationProcessor().getBone("root"),
                this.getAnimationProcessor().getBone("segment_1"),
                this.getAnimationProcessor().getBone("segment_2"),
                this.getAnimationProcessor().getBone("segment_3"),
                this.getAnimationProcessor().getBone("segment_4")
        );

        int age = animatable.tickCount;
        float tween = Mth.clamp(age < 15 ? (age - 5) / 10f : age > 240 ? 1 - (age - 240) / 50f : 1f, 0, 1);
        for (int i = 0; i < bones.size(); i++) {
            var bone = bones.get(i);
            float intensity = 3f - i * .2f;
            bone.updateRotation(Mth.lerp(tween, bone.getRotX(), intensity * Mth.DEG_TO_RAD * shittyNoise(f + 100 + i)), 0, Mth.lerp(tween, bone.getRotZ(), intensity * Mth.DEG_TO_RAD * shittyNoise(f + i)));
        }
        this.getAnimationProcessor().getBone("root").updateRotation(0, Mth.DEG_TO_RAD * (shittyNoise(f + 150) * .25f + animatable.tickCount + animationState.getPartialTick()), 0);
    }

    private static float shittyNoise(float f) {
        return (Mth.sin(f * .1f) + Mth.sin(f * .25f) + 2 * Mth.sin(f * .333f) + 3 * Mth.sin(f * .5f) + 4 * Mth.sin(f)) * 1.5f;
    }
}
