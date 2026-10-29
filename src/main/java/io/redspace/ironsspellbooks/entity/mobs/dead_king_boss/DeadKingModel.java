package io.redspace.ironsspellbooks.entity.mobs.dead_king_boss;

import com.geckolib.renderer.base.GeoRenderState;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;


import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobModel;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.cache.model.GeoBone;

public class DeadKingModel extends AbstractSpellCastingMobModel {
    public static final Identifier TEXTURE_NORMAL = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/dead_king/dead_king.png");
    public static final Identifier TEXTURE_CORPSE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/dead_king/dead_king_resting.png");
    public static final Identifier TEXTURE_ENRAGED = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/dead_king/dead_king_enraged.png");
    public static final Identifier TEXTURE_NORMAL_OMINOUS = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/dead_king/ominous/dead_king_ominous.png");
    public static final Identifier TEXTURE_ENRAGED_OMINOUS = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/dead_king/ominous/dead_king_enraged_ominous.png");

    public static final Identifier MODEL = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "dead_king");

    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        var object = mob(state);
        if (object instanceof DeadKingBoss boss) {
            boolean enraged = boss.isPhase(DeadKingBoss.Phases.FinalPhase);
            if (boss.isOminous()) {
                return enraged ? TEXTURE_ENRAGED_OMINOUS : TEXTURE_NORMAL_OMINOUS;
            } else {
                return enraged ? TEXTURE_ENRAGED : TEXTURE_NORMAL;

            }
        } else {
            return TEXTURE_CORPSE;
        }
    }

    @Override
    public Identifier getModelResource(GeoRenderState state) {
        var object = mob(state);
        return MODEL;
    }

    @Override
    public void animateBones(AbstractSpellCastingMob entity, float partialTick, TransformStack transformStack) {
        this.transformStack = transformStack;
        float f = entity.tickCount + partialTick;

        if (entity instanceof DeadKingBoss boss && boss.isPhase(DeadKingBoss.Phases.FinalPhase)) {
            String torso = "torso";
            float torsoHeight = 18;
            float range = 20;
            float rotation = (Mth.sin(f * .05f) * range - range - 30) * Mth.DEG_TO_RAD / 2f;
            this.transformStack.pushRotation(torso, rotation, 0, 0);
            this.transformStack.pushPosition(torso, 0, torsoHeight * (Mth.cos(Mth.PI - rotation) + 1), 1 - torsoHeight * Mth.sin(Mth.PI - rotation));
        }
        super.animateBones(entity, partialTick, transformStack);
        String jaw = "jaw";
        String hair1 = "hair";
        String hair2 = "hair2";

        //Builtin Resource Pack does not contain these bones
        if (jaw == null || hair1 == null || hair2 == null)
            return;

        transformStack.setRotX(jaw, Mth.sin(f * .05f) * 5 * Mth.DEG_TO_RAD);
        transformStack.setRotX(hair1, (Mth.sin(f * .1f) * 10 - 30) * Mth.DEG_TO_RAD);
        transformStack.setRotX(hair2, Mth.sin(f * .15f) * 15 * Mth.DEG_TO_RAD);

    }
}
