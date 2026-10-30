package io.redspace.ironsspellbooks.render;

import com.geckolib.renderer.base.GeoRenderState;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;

/** Poses bones using values captured during render-state extraction. */
public interface ExtractedProceduralBoneAnimation {
    void animateBones(GeoRenderState state, TransformStack transformStack);
}
