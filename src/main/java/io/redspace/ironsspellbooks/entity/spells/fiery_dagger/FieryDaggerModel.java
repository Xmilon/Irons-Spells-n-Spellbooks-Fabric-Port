package io.redspace.ironsspellbooks.entity.spells.fiery_dagger;

import com.geckolib.renderer.base.GeoRenderState;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.render.RenderHelper;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import com.geckolib.model.GeoModel;

public class FieryDaggerModel extends GeoModel<FieryDaggerEntity> {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/fiery_dagger.png");
    public static final Identifier MODEL = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "fiery_dagger");
    @Override
    public Identifier getModelResource(GeoRenderState state) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(FieryDaggerEntity animatable) {
        return AbstractSpellCastingMob.animationInstantCast;
    }

    @Override
    public @Nullable RenderType getRenderType(FieryDaggerEntity animatable, Identifier texture) {
        return RenderHelper.CustomerRenderType.magic(TEXTURE);
    }
}
