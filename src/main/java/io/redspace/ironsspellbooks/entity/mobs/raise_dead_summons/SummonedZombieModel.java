package io.redspace.ironsspellbooks.entity.mobs.raise_dead_summons;

import com.geckolib.renderer.base.GeoRenderState;


import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.SummonedZombie;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;

public class SummonedZombieModel extends GeoModel<SummonedZombie> {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/summoned_zombie.png");
    public static final Identifier MODEL = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "geo/abstract_casting_mob.geo.json");
    public static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "animations/casting_animations.json");


    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        return TEXTURE;
    }

    @Override
    public Identifier getModelResource(GeoRenderState state) {
        return MODEL;
    }

    @Override
    public Identifier getAnimationResource(SummonedZombie animatable) {
        return ANIMATIONS;
    }

}
