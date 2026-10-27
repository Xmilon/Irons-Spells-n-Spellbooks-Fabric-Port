package io.redspace.ironsspellbooks.render;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.model.GeoModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.LivingEntity;

public class GeoLivingEntityRenderer<T extends LivingEntity & GeoAnimatable> extends IronsGeoEntityRenderer<T> {
    public GeoLivingEntityRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
        super(renderManager, model);
    }

    @Override
    public boolean shouldShowName(T animatable, double distanceToCameraSq) {
        float f = animatable.isCrouching() ? 32.0F : 64.0F;
        return !(distanceToCameraSq >= (double) (f * f)) && animatable.isCustomNameVisible();
    }
}
