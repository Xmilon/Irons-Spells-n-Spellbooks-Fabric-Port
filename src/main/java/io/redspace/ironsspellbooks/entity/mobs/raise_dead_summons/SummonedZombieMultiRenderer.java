package io.redspace.ironsspellbooks.entity.mobs.raise_dead_summons;

import com.geckolib.constant.dataticket.DataTicket;
import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironsspellbooks.entity.mobs.HumanoidRenderer;
import io.redspace.ironsspellbooks.entity.mobs.SummonedZombie;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;

public class SummonedZombieMultiRenderer extends HumanoidRenderer<SummonedZombie> {
    private static final DataTicket<ZombieRenderState> VANILLA_STATE = DataTicket.create("irons_spellbooks_vanilla_zombie_state", ZombieRenderState.class);
    private final ZombieRenderer vanillaRenderer;

    public SummonedZombieMultiRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new SummonedZombieModel());
        vanillaRenderer = new ZombieRenderer(pContext) {
            @Override
            public Identifier getTextureLocation(ZombieRenderState state) {
                return SummonedZombieModel.TEXTURE;
            }
        };
    }

    @Override
    public void extractRenderState(SummonedZombie entity, EntityRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        // once risen, the vanilla zombie renderer takes over; its state is extracted here so submission reads no live entity
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
