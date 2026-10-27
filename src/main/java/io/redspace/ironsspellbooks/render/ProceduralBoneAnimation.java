package io.redspace.ironsspellbooks.render;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;
import net.minecraft.world.entity.Entity;

/**
 * Implemented by GeckoLib models that pose bones from live entity state on top of their keyframe animation.
 */
public interface ProceduralBoneAnimation<T extends Entity> {
    void animateBones(T entity, float partialTick, TransformStack transformStack);
}
