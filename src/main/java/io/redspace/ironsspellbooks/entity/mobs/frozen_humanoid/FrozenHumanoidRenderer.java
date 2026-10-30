package io.redspace.ironsspellbooks.entity.mobs.frozen_humanoid;

import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironsspellbooks.render.IExtendedSimpleTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;

/** Copy renderer/model selection happens during extraction; submission never retains an entity. */
public class FrozenHumanoidRenderer extends EntityRenderer<FrozenHumanoid, FrozenHumanoidRenderer.State> {
    private static final Identifier TEXTURE = Identifier.parse("irons_spellbooks:textures/entity/frozen_humanoid.png");
    private static final Identifier TEXTURE_ALT = Identifier.parse("irons_spellbooks:textures/entity/frozen_humanoid_alt.png");
    private final IceModelRenderer iceRenderer;

    public static class State extends EntityRenderState {
        EntityModel<LivingEntityRenderState> copiedModel;
        LivingEntityRenderState copiedState;
        Identifier texture;
    }

    public FrozenHumanoidRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = .36f;
        iceRenderer = new IceModelRenderer(context);
    }

    @Override
    public State createRenderState() { return new State(); }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void extractRenderState(FrozenHumanoid entity, State state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.copiedModel = iceRenderer.getModel();
        state.copiedState = iceRenderer.createRenderState(entity, partialTick);
        state.texture = TEXTURE;
        if (entity.entityToCopy != null) {
            var copy = entity.entityToCopy.create(entity.level(), EntitySpawnReason.LOAD);
            if (copy instanceof LivingEntity livingCopy) {
                FrozenHumanoid.copyEntityVisualProperties(livingCopy, entity);
                EntityRenderer renderer = entityRenderDispatcher.getRenderer(livingCopy);
                if (renderer instanceof LivingEntityRenderer livingRenderer) {
                    var copyState = (LivingEntityRenderState) livingRenderer.createRenderState(livingCopy, partialTick);
                    var texture = Minecraft.getInstance().getTextureManager().getTexture(livingRenderer.getTextureLocation(copyState));
                    state.texture = texture instanceof IExtendedSimpleTexture extended && extended.irons_spellbooks$isRectangular() ? TEXTURE_ALT : TEXTURE;
                    state.copiedModel = (EntityModel<LivingEntityRenderState>) livingRenderer.getModel();
                    state.copiedState = copyState;
                }
            }
        }
        // Ice shadows freeze age-based bobbing while retaining captured walking and attack poses.
        state.copiedState.ageInTicks = 0;
    }

    @Override
    public void submit(State state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        iceRenderer.submitIce(state.copiedModel, state.copiedState, state.texture, poses, collector, camera);
    }

    private static final class IceModelRenderer extends LivingEntityRenderer<LivingEntity, LivingEntityRenderState, EntityModel<LivingEntityRenderState>> {
        private final EntityModel<LivingEntityRenderState> fallback;
        private Identifier texture = TEXTURE;

        @SuppressWarnings({"unchecked", "rawtypes"})
        IceModelRenderer(EntityRendererProvider.Context context) {
            super(context, (EntityModel) new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), .36f);
            fallback = model;
        }

        @Override
        public LivingEntityRenderState createRenderState() { return new HumanoidRenderState(); }

        @Override
        public void extractRenderState(LivingEntity entity, LivingEntityRenderState state, float partialTick) {
            super.extractRenderState(entity, state, partialTick);
            HumanoidMobRenderer.extractHumanoidRenderState(entity, (HumanoidRenderState) state, partialTick, itemModelResolver);
        }

        @Override
        public Identifier getTextureLocation(LivingEntityRenderState state) { return texture; }

        @Override
        protected boolean shouldShowName(LivingEntity entity, double distance) {
            float range = entity.isCrouching() ? 32 : 64;
            return distance < range * range && entity.isCustomNameVisible();
        }

        void submitIce(EntityModel<LivingEntityRenderState> capturedModel, LivingEntityRenderState capturedState,
                       Identifier capturedTexture, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
            model = capturedModel;
            texture = capturedTexture;
            try {
                super.submit(capturedState, poses, collector, camera);
            } finally {
                model = fallback;
                texture = TEXTURE;
            }
        }
    }
}
