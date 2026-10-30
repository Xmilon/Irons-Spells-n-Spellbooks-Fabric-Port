package io.redspace.ironsspellbooks.render;

import io.redspace.ironsspellbooks.effect.EchoingStrikesData;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.player.SpinAttackType;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;
import java.util.stream.Collectors;

/** Extra visual values captured once; layers never query a live entity. */
public record LivingEffectsRenderState(Set<Holder<MobEffect>> effects, String castingSpell,
                                      float scale, float age, float echo, Identifier texture,
                                      boolean hideAll, SpellRenderingHelper.RayState ray,
                                      GlowingEyesLayer.EyeType eye, float eyeScale, SpellTargetingLayer.Target targeting, SpinAttackType spinAttack) {
    public static final RenderStateDataKey<LivingEffectsRenderState> KEY = RenderStateDataKey.create();

    public static void capture(LivingEntity entity, EntityRenderState state, float partialTick, Identifier texture) {
        var data = ClientMagicData.getSyncedSpellData(entity);
        String spell = data.isCasting() ? data.getCastingSpellId() : "";
        var effects = entity.getActiveEffects().stream().map(effect -> effect.getEffect()).collect(Collectors.toUnmodifiableSet());
        float echo = EchoingStrikesData.has(entity)
                ? Mth.clamp((EchoingStrikesData.get(entity).vfxTimestamp - entity.tickCount) / 20f, 0, 1) : 0;
        var player = Minecraft.getInstance().player;
        boolean hidden = player != null && effects.contains(MobEffectRegistry.TRUE_INVISIBILITY) && entity.isInvisibleTo(player);
        ((FabricRenderState) state).setData(KEY, new LivingEffectsRenderState(effects, spell, entity.getScale(),
                entity.tickCount + partialTick, echo, texture, hidden,
                SpellRenderingHelper.extractRay(entity, spell, partialTick),
                GlowingEyesLayer.getEyeType(entity), GlowingEyesLayer.getEyeScale(entity), SpellTargetingLayer.capture(entity), data.getSpinAttackType()));
    }

    public static LivingEffectsRenderState get(EntityRenderState state) {
        return ((FabricRenderState) state).getData(KEY);
    }
}
