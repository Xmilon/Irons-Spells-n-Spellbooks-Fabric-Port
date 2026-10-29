package io.redspace.ironsspellbooks.entity.mobs.ice_spider;

import com.geckolib.renderer.base.GeoRenderState;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import com.geckolib.animatable.GeoReplacedEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.DefaultedEntityGeoModel;

import java.util.Objects;

public class IceSpiderModel extends DefaultedEntityGeoModel<IceSpiderEntity> implements io.redspace.ironsspellbooks.render.ProceduralBoneAnimation<IceSpiderEntity> {
    static final String[] SIDES = {"right", "left"};
    static final String[] LEGS = {"Fore", "ForeMiddle", "BackMiddle", "Back"};
    static final String SHOULDER = "Shoulder";
    static final String LEG = "Leg";

    static final float OFFSET_PER_LEG = 35 * Mth.DEG_TO_RAD;

    public IceSpiderModel() {
        super(IronsSpellbooks.id("spellcastingmob"));
    }

    protected TransformStack transformStack;

    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/ice_spider/ice_spider.png");
    public static final Identifier MODEL = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "ice_spider");
    public static final Identifier ANIMATION = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "ice_spider");

    @Override
    public Identifier getModelResource(GeoRenderState state) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(IceSpiderEntity animatable) {
        return ANIMATION;
    }

    @Override
    public void animateBones(IceSpiderEntity entity, float partialTick, TransformStack transformStack) {
        this.transformStack = transformStack;
        //        TransformStack transformStack = animationState.getData(STACK_TICKET);
//        assert transformStack != null;
        transformStack.pushPosition("torso",
                (float) IceSpiderEntity.TORSO_OFFSET.x,
                (float) IceSpiderEntity.TORSO_OFFSET.y * entity.getCrouchHeightMultiplier(partialTick),
                (float) IceSpiderEntity.TORSO_OFFSET.z);

        Vec3 normal = Utils.lerp(partialTick, entity.lastNormal, entity.normal);
        Quaternionf normalRotation = Utils.rotationBetweenVectors(normal.toVector3f(), new Vector3f(0, 1, 0));
        Vector3f headRotation = new Vector3f(
                Mth.lerp(partialTick, entity.xRotO, entity.getXRot()) * Mth.DEG_TO_RAD,
                Mth.lerp(partialTick,
                        Mth.wrapDegrees(entity.yHeadRotO - entity.yBodyRotO) * Mth.DEG_TO_RAD,
                        Mth.wrapDegrees(entity.yHeadRot - entity.yBodyRot) * Mth.DEG_TO_RAD
                ), 0);
        normalRotation.invert().transform(headRotation); // undo body rotation and apply to head rotation to normalize
        var head = "head";
        transformStack.pushRotation(head, -headRotation.x, -headRotation.y, -headRotation.z);

        Vector2f limbSwingVec = getLimbSwing(entity, entity.walkAnimation, partialTick);
        float limbSwing = limbSwingVec.y;
        float limbSwingAmount = limbSwingVec.x;

        float f = 0.5f;
        float yRange = 20 * Mth.DEG_TO_RAD * f;
        float zRange = 12 * Mth.DEG_TO_RAD * f;
        float speed = 2 * .05f / f;


        float primaryY = legY(limbSwing, speed, 0) * yRange * limbSwingAmount;
        float secondaryY = legY(limbSwing, speed, Mth.PI) * yRange * limbSwingAmount;
        float primaryZ = legZ(limbSwing, speed, Mth.PI) * zRange * limbSwingAmount;
        float secondaryZ = legZ(limbSwing, speed, 0) * zRange * limbSwingAmount;

        for (int i = 0; i < SIDES.length; i++) {
            for (int j = 0; j < LEGS.length; j++) {
                int sideSign = Mth.sign(i - 0.5);
                float baseY = 0;//(j - 1.5f) * OFFSET_PER_LEG * sideSign;
                float baseZ = Mth.lerp(entity.crouchTweenPercent(partialTick), 10, 0) * Mth.DEG_TO_RAD;
                String shoulderBone = String.format("%s%s%s", SIDES[i], LEGS[j], SHOULDER);
                String legBone = String.format("%s%s%s", SIDES[i], LEGS[j], LEG);
                boolean primary = j % 2 == 0;
                try {
                    transformStack.pushRotation(shoulderBone, 0, ((primary ? primaryY : secondaryY) + baseY) * sideSign, 0);
                    transformStack.pushRotation(legBone, 0, 0, ((primary ? primaryZ : secondaryZ) + baseZ) * -sideSign);
                } catch (Exception e) {
                    IronsSpellbooks.LOGGER.error("beep");
                }
            }
        }
    }

    private float legY(float limbSwing, float speedFactor, float offset) {
        float f = offset - Mth.HALF_PI;

        return Mth.sin((limbSwing * Mth.TWO_PI) * speedFactor + f +
                (Mth.sin(limbSwing * Mth.TWO_PI * speedFactor + f)) * 0.5f);
    }

    private float legZ(float limbSwing, float speedFactor, float offset) {
        float f = Mth.sin((limbSwing * Mth.TWO_PI) * speedFactor + offset);
        f = f * f * f;
        return Math.max(f, 0);
    }

    /**
     * @param walkAnimationState
     * @return x: amount, y: speed
     */
    protected Vector2f getLimbSwing(AbstractSpellCastingMob entity, WalkAnimationState walkAnimationState, float partialTick) {
        float limbSwingAmount = 0;
        float limbSwingSpeed = 0;
        if (entity.isAlive()) {
            limbSwingAmount = walkAnimationState.speed(partialTick);
            limbSwingSpeed = walkAnimationState.position(partialTick);
            if (entity.isBaby()) {
                limbSwingSpeed *= 3.0F;
            }

            if (limbSwingAmount > 1.0F) {
                limbSwingAmount = 1.0F;
            }
        }
        return new Vector2f(limbSwingAmount, limbSwingSpeed);
    }
}
