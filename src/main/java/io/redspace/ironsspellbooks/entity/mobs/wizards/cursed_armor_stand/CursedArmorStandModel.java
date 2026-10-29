package io.redspace.ironsspellbooks.entity.mobs.wizards.cursed_armor_stand;

import com.geckolib.renderer.base.GeoRenderState;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import io.redspace.ironsspellbooks.util.DefaultBipedBoneIdents;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.GeoBone;

import static io.redspace.ironsspellbooks.entity.mobs.wizards.cursed_armor_stand.CursedArmorStandEntity.JIGGLE_TIME;

public class CursedArmorStandModel extends AbstractSpellCastingMobModel {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/cultist.png");
    public static final Identifier MODEL = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "armor_stand");

    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        var object = mob(state);
        return ArmorStandRenderer.DEFAULT_SKIN_LOCATION;
    }

    public static double[] rightArmPos = new double[]{0, 0, 0};

    @Override
    public void animateBones(AbstractSpellCastingMob entity, float partialTick, TransformStack transformStack) {
        this.transformStack = transformStack;
        if (entity instanceof CursedArmorStandEntity cursedArmorStandEntity && cursedArmorStandEntity.isArmorStandFrozen()) {
            var pose = cursedArmorStandEntity.getArmorstandPose();
            String head = PartNames.HEAD;
            String body = PartNames.BODY;
            String torso = "torso";
            String rightArm = PartNames.RIGHT_ARM;
            String rightHand = DefaultBipedBoneIdents.RIGHT_HAND_BONE_IDENT;
            String leftArm = PartNames.LEFT_ARM;
            String rightLeg = PartNames.RIGHT_LEG;
            String leftLeg = PartNames.LEFT_LEG;
            switch (pose) {
                case DEFAULT -> {
                    transformStack.pushRotation(leftArm, 10 * Mth.DEG_TO_RAD, 0, -10 * Mth.DEG_TO_RAD);
                    transformStack.pushRotation(rightArm, 15 * Mth.DEG_TO_RAD, 0, 10 * Mth.DEG_TO_RAD);
                    transformStack.pushRotation(leftLeg, -1 * Mth.DEG_TO_RAD, 0, -1 * Mth.DEG_TO_RAD);
                    transformStack.pushRotation(rightLeg, 1 * Mth.DEG_TO_RAD, 0, 1 * Mth.DEG_TO_RAD);
                }
                case KNEELING -> {
                    transformStack.pushPosition(rightLeg, 0, 2, -3);
                    transformStack.pushRotation(rightLeg, -52.5f * Mth.DEG_TO_RAD, 0, 0);
                    transformStack.pushPosition(leftLeg, 1 * Mth.DEG_TO_RAD, 6, -4);
                    transformStack.pushRotation(rightArm, 90 * Mth.DEG_TO_RAD, 45 * Mth.DEG_TO_RAD, 90 * Mth.DEG_TO_RAD);
                    transformStack.pushRotation(leftArm, 45 * Mth.DEG_TO_RAD, 0, 0);
                    transformStack.pushRotation(head, -45 * Mth.DEG_TO_RAD, 0, 0);
                    transformStack.pushRotation(torso, -10 * Mth.DEG_TO_RAD, 0, 0);
                    transformStack.pushPosition(body, 0, -6, 0);
                }
                case HEROIC -> {
                    transformStack.pushRotation(rightArm, 132.5f * Mth.DEG_TO_RAD, 0, 0);
                    transformStack.pushRotation(leftArm, -20f * Mth.DEG_TO_RAD, 0, 0);
                    transformStack.pushRotation(leftLeg, -12f * Mth.DEG_TO_RAD, 0, 0);
                    transformStack.pushPosition(rightLeg, 0, 0, -2);
                    transformStack.pushRotation(head, 25 * Mth.DEG_TO_RAD, 0, 0);
                }
                case STOIC -> {
                    transformStack.pushRotationDegrees(rightArm, 80, 35, 0);
                    transformStack.pushRotationDegrees(leftArm, 80, -35, 0);
                    transformStack.pushRotationDegrees(rightHand, 0, 90, 0);
                    transformStack.pushRotationDegrees(rightLeg, 0, -1, 0);
                    transformStack.pushRotationDegrees(leftLeg, 0, 1, 0);
                }
            }
            if (cursedArmorStandEntity.helmetJiggle > 0) {
                float f = elastic(1f - ((cursedArmorStandEntity.helmetJiggle - partialTick) / JIGGLE_TIME));
                transformStack.pushRotation(DefaultBipedBoneIdents.HEAD_ARMOR_BONE_IDENT, 0, f, 0);
            }
            if (cursedArmorStandEntity.chestJiggle > 0) {
                float f = elastic(1 - (cursedArmorStandEntity.chestJiggle - partialTick) / JIGGLE_TIME);
                transformStack.pushRotation(DefaultBipedBoneIdents.BODY_ARMOR_BONE_IDENT, 0, f, 0);
                transformStack.pushRotation(DefaultBipedBoneIdents.RIGHT_ARM_ARMOR_BONE_IDENT, 0, f * .75f, 0);
                transformStack.pushRotation(DefaultBipedBoneIdents.LEFT_ARM_ARMOR_BONE_IDENT, 0, -f * .75f, 0);
            }
            if (cursedArmorStandEntity.legJiggle > 0) {
                float f = elastic(1 - (cursedArmorStandEntity.legJiggle - partialTick) / JIGGLE_TIME);
                transformStack.pushRotation(DefaultBipedBoneIdents.RIGHT_LEG_ARMOR_BONE_IDENT, 0, -f * .75f, 0);
                transformStack.pushRotation(DefaultBipedBoneIdents.LEFT_LEG_ARMOR_BONE_IDENT, 0, f * .75f, 0);
                transformStack.pushRotation(DefaultBipedBoneIdents.RIGHT_LEG_ARMOR_BONE_2_IDENT, 0, -f * .75f, 0);
                transformStack.pushRotation(DefaultBipedBoneIdents.LEFT_LEG_ARMOR_BONE_2_IDENT, 0, f * .75f, 0);
            }
            if (cursedArmorStandEntity.bootJiggle > 0) {
                float f = elastic(1 - (cursedArmorStandEntity.bootJiggle - partialTick) / JIGGLE_TIME);
                transformStack.pushRotation(DefaultBipedBoneIdents.RIGHT_FOOT_ARMOR_BONE_IDENT, 0, -f * .75f, 0);
                transformStack.pushRotation(DefaultBipedBoneIdents.LEFT_FOOT_ARMOR_BONE_IDENT, 0, f * .75f, 0);
                transformStack.pushRotation(DefaultBipedBoneIdents.RIGHT_FOOT_ARMOR_BONE_2_IDENT, 0, -f * .75f, 0);
                transformStack.pushRotation(DefaultBipedBoneIdents.LEFT_FOOT_ARMOR_BONE_2_IDENT, 0, f * .75f, 0);
            }
        } else {
            super.animateBones(entity, partialTick, transformStack);
        }
    }

    private float elastic(float f) {
        float x = (float) (Math.pow(2, -10 * f) * Mth.cos((10 * f - 0.75f) * 2 * .6f * Mth.PI / 3)) * .2f;
        if (Math.abs(x) < 0.001) {
            return 0;
        }
        return x;
    }

    @Override
    public Identifier getModelResource(GeoRenderState state) {
        var object = mob(state);
        return MODEL;
    }
}
