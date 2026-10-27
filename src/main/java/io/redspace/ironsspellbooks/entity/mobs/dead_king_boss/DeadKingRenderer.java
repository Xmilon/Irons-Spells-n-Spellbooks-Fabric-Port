package io.redspace.ironsspellbooks.entity.mobs.dead_king_boss;

import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class DeadKingRenderer extends AbstractSpellCastingMobRenderer {
    public DeadKingRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DeadKingModel());
        this.withRenderLayer(new DeadKingEmissiveLayer(this));
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<EntityRenderState> info, BoneSnapshots snapshots) {
        super.adjustModelBonesForRender(info, snapshots);
        if (entity(info) instanceof DeadKingBoss king) {
            boolean hideLegs = king.isPhase(DeadKingBoss.Phases.FinalPhase);
            snapshots.ifPresent(PartNames.LEFT_LEG, snapshot -> snapshot.skipRender(hideLegs));
            snapshots.ifPresent(PartNames.RIGHT_LEG, snapshot -> snapshot.skipRender(hideLegs));
        }
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<EntityRenderState> info) {
        if (entity(info) instanceof DeadKingBoss) {
            info.poseStack().scale(1.3f, 1.3f, 1.3f);
        }
        super.adjustRenderPose(info);
    }

    @Override
    public RenderType getRenderType(EntityRenderState state, Identifier texture) {
        return entity(state).isInvisible() ? RenderTypes.entityTranslucent(texture) : RenderTypes.entityCutout(texture);
    }

    @Override
    protected void adjustHandItemRendering(PoseStack poseStack, ItemStack itemStack, AbstractSpellCastingMob animatable, float partialTick, boolean offhand) {
        if (itemStack.is(ItemRegistry.BLOOD_STAFF.get())) {
            poseStack.translate(0, .25, 0);
        }
    }
}
