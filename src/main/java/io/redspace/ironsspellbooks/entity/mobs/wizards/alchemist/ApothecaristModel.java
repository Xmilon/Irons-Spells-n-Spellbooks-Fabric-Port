package io.redspace.ironsspellbooks.entity.mobs.wizards.alchemist;

import com.geckolib.renderer.base.GeoRenderState;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;

import com.google.common.collect.Streams;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.joml.Vector3f;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.GeoBone;

import java.util.stream.Stream;

public class ApothecaristModel extends AbstractSpellCastingMobModel {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/apothecarist.png");
    public static final Identifier MODEL = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "geo/piglin_casting_mob.geo.json");
    private static final float tilt = 10 * Mth.DEG_TO_RAD;
    private static final Vector3f forward = new Vector3f(0, 0, Mth.sin(tilt) * -12);

    @Override
    public Identifier getModelResource(GeoRenderState state) {
        var object = mob(state);
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        var object = mob(state);
        return TEXTURE;
    }


    @Override
    public void animateBones(AbstractSpellCastingMob entity, float partialTick, TransformStack transformStack) {
        this.transformStack = transformStack;
        String leftEar = "left_ear";
        String rightEar = "right_ear";
        String head = PartNames.HEAD;
        String body = PartNames.BODY;
        String torso = "torso";
        String rightArm = PartNames.RIGHT_ARM;
        String leftArm = PartNames.LEFT_ARM;
        String rightLeg = PartNames.RIGHT_LEG;
        String leftLeg = PartNames.LEFT_LEG;

        //Limb Offsets
        transformStack.pushPosition(head, forward);
        transformStack.pushPosition(rightArm, forward);
        transformStack.pushPosition(leftArm, forward);
        transformStack.pushPosition(torso, forward);
        transformStack.pushRotation(torso, -tilt, 0, 0);
        transformStack.pushPosition(rightLeg, forward);
        transformStack.pushPosition(leftLeg, new Vector3f(0, 0, 1));
        //Potion throw animation
        if (entity.swingTime > 0) {
            float rot = Mth.lerp((entity.swingTime - partialTick) / 10f, 0, Mth.PI);
            transformStack.pushRotation(rightArm, rot, 0, 0);
        }
        //Ear animations
        if (leftEar != null && rightEar != null) {
            var walkanimation = this.getLimbSwing(entity, entity.walkAnimation, partialTick);
            float r = Mth.cos(walkanimation.y * 0.6662F + (float) Math.PI) * 2.0F * walkanimation.x * 0.5F;
            r *= .3f;
            r += Mth.PI * .08f;
            transformStack.pushRotation(leftEar, 0, 0, -r);
            transformStack.pushRotation(rightEar, 0, 0, r);

        }
        super.animateBones(entity, partialTick, transformStack);
    }
}
