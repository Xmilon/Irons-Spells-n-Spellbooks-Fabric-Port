package io.redspace.ironsspellbooks.entity.spells.void_tentacle;

import com.geckolib.renderer.base.GeoRenderState;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;
import io.redspace.ironsspellbooks.render.ExtractedProceduralBoneAnimation;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.GeoModel;

import java.util.List;

public class VoidTentacleModel extends GeoModel<VoidTentacle> implements ExtractedProceduralBoneAnimation {
    public static final Identifier modelResource = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "void_tentacle");
    public static final Identifier textureResource = IronsSpellbooks.id("textures/entity/void_tentacle/void_tentacle.png");
    public static final Identifier animationResource = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "void_tentacle_animations");
    public static final DataTicket<TentaclePose> TENTACLE_POSE = DataTicket.create("irons_spellbooks_tentacle_pose", TentaclePose.class);
    private static final List<String> BONES = List.of("root", "segment_1", "segment_2", "segment_3", "segment_4");

    public record TentaclePose(float phase, float age, float tween) {}

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
    public void addAdditionalStateData(VoidTentacle animatable, Object relatedObject, GeoRenderState state) {
        super.addAdditionalStateData(animatable, relatedObject, state);
        float seed = (float) (animatable.getX() * animatable.getZ()) % 173;
        float age = animatable.tickCount + state.getPartialTick();
        float phase = (seed + age) * .55f;
        int ticks = animatable.tickCount;
        float tween = Mth.clamp(ticks < 15 ? (ticks - 5) / 10f : ticks > 240 ? 1 - (ticks - 240) / 50f : 1f, 0, 1);
        state.addGeckolibData(TENTACLE_POSE, new TentaclePose(phase, age, tween));
    }

    @Override
    public void animateBones(GeoRenderState state, TransformStack transformStack) {
        TentaclePose pose = state.getGeckolibData(TENTACLE_POSE);
        for (int i = 1; i < BONES.size(); i++) {
            float intensity = 3f - i * .2f;
            float x = intensity * Mth.DEG_TO_RAD * shittyNoise(pose.phase() + 100 + i);
            float z = intensity * Mth.DEG_TO_RAD * shittyNoise(pose.phase() + i);
            transformStack.snapshots().ifPresent(BONES.get(i), snapshot -> snapshot.setRotation(
                    Mth.lerp(pose.tween(), snapshot.getRotX(), x), 0,
                    Mth.lerp(pose.tween(), snapshot.getRotZ(), z)));
        }
        float yaw = Mth.DEG_TO_RAD * (shittyNoise(pose.phase() + 150) * .25f + pose.age());
        transformStack.snapshots().ifPresent("root", snapshot -> snapshot.setRotation(0, yaw, 0));
    }

    private static float shittyNoise(float f) {
        return (Mth.sin(f * .1f) + Mth.sin(f * .25f) + 2 * Mth.sin(f * .333f) + 3 * Mth.sin(f * .5f) + 4 * Mth.sin(f)) * 1.5f;
    }
}
