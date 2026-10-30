package io.redspace.ironsspellbooks.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironsspellbooks.player.SpinAttackType;
import io.redspace.ironsspellbooks.render.LivingEffectsRenderState;
import net.minecraft.client.model.effects.SpinAttackEffectModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpinAttackEffectLayer.class)
public class SpinAttackEffectLayerMixin {
    @Shadow
    @Final
    private SpinAttackEffectModel model;

    /** Draws custom spin attack textures (fire, lightning...) in place of the vanilla riptide effect. */
    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$customSpinAttack(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, AvatarRenderState state, float yRot, float xRot, CallbackInfo ci) {
        var effects = LivingEffectsRenderState.get(state);
        if (effects == null || !state.isAutoSpinAttack) {
            return;
        }
        SpinAttackType type = effects.spinAttack();
        if (type.textureId().equals(SpinAttackType.RIPTIDE.textureId())) {
            return;
        }
        int light = type.fullbright() ? LightCoordsUtil.FULL_BRIGHT : packedLight;
        collector.submitModel(this.model, state, poseStack, type.textureId(), light, OverlayTexture.NO_OVERLAY, state.outlineColor);
        ci.cancel();
    }
}
