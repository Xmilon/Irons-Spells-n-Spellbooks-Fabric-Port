package io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss;

import com.geckolib.renderer.base.GeoRenderState;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import io.redspace.ironsspellbooks.util.DefaultBipedBoneIdents;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.GeoBone;

public class FireBossModel extends AbstractSpellCastingMobModel {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/fire_boss/tyros.png");
    public static final Identifier TEXTURE_SOUL_MODE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/fire_boss/tyros_soul_mode.png");
    public static final Identifier MODEL = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "geo/tyros.geo.json");
    private static final float tilt = 15 * Mth.DEG_TO_RAD;
    private static final Vector3f forward = new Vector3f(0, 0, Mth.sin(tilt) * -12);

    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        var object = mob(state);
        if (object instanceof FireBossEntity fireBossEntity && fireBossEntity.isSoulMode()) {
            return TEXTURE_SOUL_MODE;
        }
        return TEXTURE;
    }

    @Override
    public Identifier getModelResource(GeoRenderState state) {
        var object = mob(state);
        return MODEL;
    }

    //fixme: this really doesnt work when multiple entities exist
    int lastTick;

    @Override
    public void animateBones(AbstractSpellCastingMob entity, float partialTick, TransformStack transformStack) {
        this.transformStack = transformStack;
        if (Minecraft.getInstance().isPaused()) {
            return;
        }
        if (entity instanceof FireBossEntity fireBossEntity) {
            handleParticles(fireBossEntity);
            Vector2f limbSwing = getLimbSwing(entity, entity.walkAnimation, partialTick);
            if (entity.isAnimating()) {
                fireBossEntity.isAnimatingDampener = Mth.lerp(.15f * partialTick, fireBossEntity.isAnimatingDampener, 0);
            } else {
                fireBossEntity.isAnimatingDampener = Mth.lerp(.05f * partialTick, fireBossEntity.isAnimatingDampener, 1);
            }
            if (entity.getMainHandItem().is(ItemRegistry.HELLRAZOR) || entity.getMainHandItem().is(ItemRegistry.DECREPIT_SCYTHE)) {
                String rightArm = PartNames.RIGHT_ARM;
                String rightHand = DefaultBipedBoneIdents.RIGHT_HAND_BONE_IDENT;
                Vector3f armPose = new Vector3f(-30, -30, 10);
                armPose.mul(Mth.DEG_TO_RAD * fireBossEntity.isAnimatingDampener);
                transformStack.pushRotation(rightArm, armPose);

                Vector3f scythePos = new Vector3f(-5, 0, -48);
                scythePos.mul(Mth.DEG_TO_RAD * fireBossEntity.isAnimatingDampener);
                transformStack.pushRotation(rightHand, scythePos);

                if (!entity.isAnimating()) {
                    float walkDampener = (Mth.cos(limbSwing.y() * 0.6662F + (float) Math.PI) * 2.0F * limbSwing.x() * 0.5F) * -.75f;
                    transformStack.pushRotation(rightArm, walkDampener, 0, 0);
                }
            }
            if (fireBossEntity.isHalfHealthAttacking()) {
                String rightArm = PartNames.RIGHT_ARM;
                String leftArm = PartNames.LEFT_ARM;
                String rightLeg = PartNames.RIGHT_LEG;
                String leftLeg = PartNames.LEFT_LEG;
                float f = (fireBossEntity.tickCount + partialTick);
                bobBone(rightArm, f * 3, -4);
                bobBone(leftArm, f * 3, 4);
                bobBone(rightLeg, f, -1.5f);
                bobBone(leftLeg, (f + 90), 1.5f);
            }
        }
        super.animateBones(entity, partialTick, transformStack);
    }

    public void handleParticles(FireBossEntity entity) {
        String particleEmitter = "particle_emitter";
        String body = "body";
        String offhand = DefaultBipedBoneIdents.LEFT_HAND_BONE_IDENT;

        if (entity.clientDaggerParticles) {
            transformStack.listenBoneWorldPosition(offhand, pos -> {
                if (!entity.clientDaggerParticles) {
                    return;
                }
                for (int i = 0; i < 15; i++) {
                    Vec3 random = Utils.getRandomVec3(0.25);
                    entity.level().addParticle(ParticleHelper.FIERY_SPARKS, pos.x, pos.y, pos.z, random.x, random.y, random.z);
                }
                for (int i = 0; i < 15; i++) {
                    Vec3 random = Utils.getRandomVec3(0.25);
                    entity.level().addParticle(ParticleHelper.EMBERS, pos.x, pos.y, pos.z, random.x, random.y, random.z);
                }
                for (int i = 0; i < 5; i++) {
                    Vec3 random = Utils.getRandomVec3(0.08);
                    entity.level().addParticle(ParticleRegistry.EMBEROUS_ASH_PARTICLE.get(), pos.x + random.x, pos.y + random.y, pos.z + random.z, random.x, random.y, random.z);
                }
                entity.clientDaggerParticles = false;
            });
        }
        if (entity.isSpawning()) {
            transformStack.listenBoneWorldPosition(body, pos -> {
                if (lastTick != entity.tickCount) {
                    int particles = entity.getSpawnWalkPercent(0) > .7f ? 0 : (int) (10 * entity.getSpawnWalkPercent(0));
                    lastTick = entity.tickCount;
                    for (int i = 0; i < particles; i++) {
                        Vec3 random = Utils.getRandomVec3(0.5);
                        entity.level().addParticle(ParticleRegistry.EMBEROUS_ASH_PARTICLE.get(), pos.x + random.x, pos.y + 1 + random.y * 2, pos.z + random.z, 200, 0, 0);
                    }
                }
            });
        }
        if (entity.isSoulMode()) {
            transformStack.listenBoneWorldPosition(particleEmitter, pos -> {
                if (lastTick != entity.tickCount) {
                    lastTick = entity.tickCount;
                    Vec3 entityMotion = entity.getDeltaMovement().add(0, entity.getGravity(), 0);
                    Vec3 headPos = pos.add(entityMotion.x * 3, entityMotion.y * 3, entityMotion.z * 3).add(0, 0.2 * entity.getScale(), 0);
                    Vec3 random = Utils.getRandomVec3(0.25);
                    entity.level().addParticle(ParticleHelper.FIRE, headPos.x + random.x, headPos.y + random.y, headPos.z + random.z, entityMotion.x * .5, entityMotion.y * .5, entityMotion.z * .5);
                }
            });
        }
    }

    @Override
    protected Vector2f getLimbSwing(AbstractSpellCastingMob entity, WalkAnimationState walkAnimationState, float partialTick) {
        Vector2f swing = super.getLimbSwing(entity, walkAnimationState, partialTick);
        swing.mul(0.6f, 1f);
        return swing;
    }
}
