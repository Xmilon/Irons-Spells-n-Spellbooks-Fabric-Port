package io.redspace.ironsspellbooks.entity.mobs.raise_dead_summons;

import com.geckolib.renderer.base.GeoRenderState;


import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.SummonedSkeleton;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;

public class SummonedSkeletonModel extends GeoModel<SummonedSkeleton> {
    //public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("textures/entity/skeleton/skeleton.png");
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/summoned_skeleton.png");

    public static final Identifier MODEL = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "skeleton_mob");
    public static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "casting_animations");


    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        return TEXTURE;
    }

    @Override
    public Identifier getModelResource(GeoRenderState state) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "skeleton_mob");
    }

    @Override
    public Identifier getAnimationResource(SummonedSkeleton animatable) {
        return ANIMATIONS;
    }

}
