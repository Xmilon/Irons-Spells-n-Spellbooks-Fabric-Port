package io.redspace.ironsspellbooks.entity.mobs.wizards.alchemist;

import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.entity.state.EntityRenderState;


import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.Nullable;
import com.geckolib.cache.model.BakedGeoModel;

public class ApothecaristRenderer extends AbstractSpellCastingMobRenderer {

    public ApothecaristRenderer(EntityRendererProvider.Context context) {
        super(context, new ApothecaristModel());
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<EntityRenderState> info) {
        info.poseStack().translate(0, 0, -0.0825f);
        super.adjustRenderPose(info);
    }
}
