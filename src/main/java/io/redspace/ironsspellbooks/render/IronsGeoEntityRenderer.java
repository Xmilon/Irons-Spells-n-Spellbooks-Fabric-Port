package io.redspace.ironsspellbooks.render;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;

/**
 * GeckoLib entity renderer that keeps the entity reachable from the render state (like {@link EntityBackedRenderer})
 * and runs {@link ProceduralBoneAnimation} models.
 * shortcut: the entity is read during the render pass instead of being copied into the state.
 */
public class IronsGeoEntityRenderer<T extends Entity & GeoAnimatable> extends GeoEntityRenderer<T, EntityRenderState> {
    public static final DataTicket<Entity> ENTITY = DataTicket.create("irons_spellbooks_entity", Entity.class);
    public static final DataTicket<Float> PARTIAL_TICK = DataTicket.create("irons_spellbooks_partial_tick", Float.class);

    public IronsGeoEntityRenderer(EntityRendererProvider.Context context, GeoModel<T> model) {
        super(context, model);
    }

    @Override
    public void addRenderData(T animatable, Void relatedObject, EntityRenderState renderState, float partialTick) {
        super.addRenderData(animatable, relatedObject, renderState, partialTick);
        renderState.addGeckolibData(ENTITY, animatable);
        renderState.addGeckolibData(PARTIAL_TICK, partialTick);
    }

    @SuppressWarnings("unchecked")
    protected T entity(EntityRenderState state) {
        return (T) state.getGeckolibData(ENTITY);
    }

    protected T entity(RenderPassInfo<EntityRenderState> info) {
        return entity(info.renderState());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void adjustModelBonesForRender(RenderPassInfo<EntityRenderState> info, BoneSnapshots snapshots) {
        super.adjustModelBonesForRender(info, snapshots);
        if (getGeoModel() instanceof ProceduralBoneAnimation<?> animation) {
            ((ProceduralBoneAnimation<T>) animation).animateBones(entity(info), info.renderState().getGeckolibData(PARTIAL_TICK), new TransformStack(snapshots, info));
        }
    }
}
