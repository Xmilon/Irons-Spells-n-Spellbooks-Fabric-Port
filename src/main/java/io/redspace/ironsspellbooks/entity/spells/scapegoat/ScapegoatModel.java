package io.redspace.ironsspellbooks.entity.spells.scapegoat;

import com.geckolib.renderer.base.GeoRenderState;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;
import io.redspace.ironsspellbooks.render.ExtractedProceduralBoneAnimation;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.DefaultedEntityGeoModel;

public class ScapegoatModel extends DefaultedEntityGeoModel<ScapegoatEntity> implements ExtractedProceduralBoneAnimation {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/scapegoat.png");
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "scapegoat");
    public static final Identifier ANIMS = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "scapegoat_animations");
    public static final DataTicket<WalkPose> WALK_POSE = DataTicket.create("irons_spellbooks_scapegoat_walk_pose", WalkPose.class);

    public record WalkPose(float swing, float amount) {}

    public ScapegoatModel() {
        super(IronsSpellbooks.id("spellcastingmob"));
    }


    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        return TEXTURE;
    }

    @Override
    public Identifier getModelResource(GeoRenderState state) {
        return MODEL;
    }

    @Override
    public Identifier getAnimationResource(ScapegoatEntity animatable) {
        return ANIMS;
    }

    @Override
    public void addAdditionalStateData(ScapegoatEntity entity, Object relatedObject, GeoRenderState state) {
        super.addAdditionalStateData(entity, relatedObject, state);
        float partialTick = state.getPartialTick();
        float amount = entity.isAlive() ? Math.min(entity.walkAnimation.speed(partialTick), 1) : 0;
        float swing = entity.isAlive() ? entity.walkAnimation.position(partialTick) : 0;
        if (entity.isBaby()) swing *= 3;
        state.addGeckolibData(WALK_POSE, new WalkPose(swing, amount));
    }

    @Override
    public void animateBones(GeoRenderState state, TransformStack transformStack) {
        WalkPose pose = state.getGeckolibData(WALK_POSE);
        float first = Mth.cos(pose.swing() * 0.6662F) * 1.4F * pose.amount();
        float opposite = Mth.cos(pose.swing() * 0.6662F + (float) Math.PI) * 1.4F * pose.amount();
        transformStack.pushRotation("rightHindLeg", first, 0, 0);
        transformStack.pushRotation("leftHindLeg", opposite, 0, 0);
        transformStack.pushRotation("rightFrontLeg", opposite, 0, 0);
        transformStack.pushRotation("leftFrontLeg", first, 0, 0);
    }
}
