package io.redspace.ironsspellbooks.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironsspellbooks.render.AngelWingsLayer;
import io.redspace.ironsspellbooks.render.ArmorCapeLayer;
import io.redspace.ironsspellbooks.render.LivingEffectsRenderState;
import io.redspace.ironsspellbooks.render.SpellRenderingHelper;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At("TAIL"))
    private void irons_spellbooks$captureEffects(LivingEntity entity, LivingEntityRenderState state, float partialTick, CallbackInfo ci) {
        Identifier texture = ((LivingEntityRenderer) (Object) this).getTextureLocation(state);
        LivingEffectsRenderState.capture(entity, state, partialTick, texture);
        AngelWingsLayer.capture(entity, state, partialTick);
        ArmorCapeLayer.capture(entity, state, partialTick);
    }

    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$hideEffects(LivingEntityRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera, CallbackInfo ci) {
        var effects = LivingEffectsRenderState.get(state);
        if (effects != null && effects.hideAll()) ci.cancel();
    }

    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At("TAIL"))
    private void irons_spellbooks$submitSpell(LivingEntityRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera, CallbackInfo ci) {
        var effects = LivingEffectsRenderState.get(state);
        if (effects != null && !effects.hideAll()) SpellRenderingHelper.submitRay(effects.ray(), poses, collector);
    }
}
