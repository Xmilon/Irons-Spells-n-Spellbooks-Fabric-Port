package io.redspace.ironsspellbooks.render;

import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.debug_wizard.DebugWizard;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DebugWizardSpellName extends GeoRenderLayer<AbstractSpellCastingMob, Void, EntityRenderState> {
    public DebugWizardSpellName(GeoRenderer<AbstractSpellCastingMob, Void, EntityRenderState> renderer) {
        super(renderer);
    }

    // unverified: native name tag submission replaces the hand-built font draw.
    @Override
    public void submitRenderTask(RenderPassInfo<EntityRenderState> info, SubmitNodeCollector collector) {
        if (!(IronsGeoEntityRenderer.<AbstractSpellCastingMob>entityOf(info.renderState()) instanceof DebugWizard debugWizard)) {
            return;
        }
        var spellInfo = debugWizard.getSpellInfo();
        if (spellInfo != null) {
            collector.submitNameTag(info.poseStack(), new Vec3(0, debugWizard.getBbHeight() + 0.5, 0), 0, net.minecraft.network.chat.Component.literal(spellInfo), !debugWizard.isDiscrete(), info.packedLight(), info.cameraState());
        }
    }
}
