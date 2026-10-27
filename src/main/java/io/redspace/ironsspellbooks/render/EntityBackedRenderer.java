package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;

/**
 * Renderer for simple spell entities whose drawing depends on many live entity fields.
 * The render state carries the entity itself, so {@link #submit(Entity, float, int, PoseStack, SubmitNodeCollector, CameraRenderState)}
 * reads it like the pre-26 {@code render} did.
 * shortcut: entity is read at submit time instead of being copied into the state; safe while submission stays on the client thread, switch to copied fields if that changes.
 */
public abstract class EntityBackedRenderer<T extends Entity> extends EntityRenderer<T, EntityBackedRenderer.State<T>> {
    public static class State<T extends Entity> extends EntityRenderState {
        public T entity;
        public float partialTick;
    }

    protected EntityBackedRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public State<T> createRenderState() {
        return new State<>();
    }

    @Override
    public void extractRenderState(T entity, State<T> state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.entity = entity;
        state.partialTick = partialTick;
    }

    @Override
    public final void submit(State<T> state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        submit(state.entity, state.partialTick, state.lightCoords, poseStack, collector, camera);
        super.submit(state, poseStack, collector, camera);
    }

    protected abstract void submit(T entity, float partialTick, int light, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera);
}
