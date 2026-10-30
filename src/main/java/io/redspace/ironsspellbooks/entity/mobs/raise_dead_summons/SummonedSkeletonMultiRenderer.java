package io.redspace.ironsspellbooks.entity.mobs.raise_dead_summons;

import com.geckolib.constant.dataticket.DataTicket;
import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.HumanoidRenderer;
import io.redspace.ironsspellbooks.entity.mobs.SummonedSkeleton;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;

public class SummonedSkeletonMultiRenderer extends HumanoidRenderer<SummonedSkeleton> {
    private static final DataTicket<SkeletonRenderState> VANILLA_STATE = DataTicket.create("irons_spellbooks_vanilla_skeleton_state", SkeletonRenderState.class);
    public static final Identifier TEXTURE_ALT = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/entity/summoned_skeleton_alt.png");
    private final SkeletonRenderer vanillaRenderer;

    public SummonedSkeletonMultiRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new SummonedSkeletonModel());
        vanillaRenderer = new SkeletonRenderer(pContext) {
            @Override
            public Identifier getTextureLocation(SkeletonRenderState state) {
                return TEXTURE_ALT;
            }
        };
    }

    @Override
    public void extractRenderState(SummonedSkeleton entity, EntityRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        // once risen, the vanilla skeleton renderer takes over; its state is extracted here so submission reads no live entity
        if (!entity.isAnimatingRise()) {
            state.addGeckolibData(VANILLA_STATE, vanillaRenderer.createRenderState(entity, partialTick));
        }
    }

    @Override
    public void submit(EntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        var vanilla = state.getGeckolibData(VANILLA_STATE);
        if (vanilla != null) {
            vanillaRenderer.submit(vanilla, poseStack, collector, camera);
        } else {
            super.submit(state, poseStack, collector, camera);
        }
    }
}
