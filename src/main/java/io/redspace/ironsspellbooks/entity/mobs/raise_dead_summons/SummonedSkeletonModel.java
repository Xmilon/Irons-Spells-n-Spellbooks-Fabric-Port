package io.redspace.ironsspellbooks.entity.mobs.raise_dead_summons;


import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.SummonedSkeleton;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;

public class SummonedSkeletonModel extends GeoModel<SummonedSkeleton> {
    //public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("textures/entity/skeleton/skeleton.png");
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/summoned_skeleton.png");

    public static final Identifier MODEL = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "geo/skeleton_mob.geo.json");
    public static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "animations/casting_animations.json");


    @Override
    public Identifier getTextureResource(SummonedSkeleton object) {
        return TEXTURE;
    }

    @Override
    public Identifier getModelResource(SummonedSkeleton object) {
        return Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "geo/skeleton_mob.geo.json");
    }

    @Override
    public Identifier getAnimationResource(SummonedSkeleton animatable) {
        return ANIMATIONS;
    }

}
