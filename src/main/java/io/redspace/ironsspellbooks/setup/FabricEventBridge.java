package io.redspace.ironsspellbooks.setup;

import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.effect.BlightEffect;
import io.redspace.ironsspellbooks.effect.EchoingStrikesEffect;
import io.redspace.ironsspellbooks.effect.FrostbiteEffect;
import io.redspace.ironsspellbooks.effect.GluttonyEffect;
import io.redspace.ironsspellbooks.effect.OakskinEffect;
import io.redspace.ironsspellbooks.effect.SpiderAspectEffect;
import io.redspace.ironsspellbooks.effect.TrueInvisibilityEffect;
import io.redspace.ironsspellbooks.effect.guiding_bolt.GuidingBoltManager;
import io.redspace.ironsspellbooks.entity.mobs.wizards.WizardAIEvents;
import io.redspace.ironsspellbooks.item.curios.BetrayerSignetRingItem;
import io.redspace.ironsspellbooks.item.curios.ExpulsionRing;
import io.redspace.ironsspellbooks.item.curios.LurkerRing;
import io.redspace.ironsspellbooks.player.ServerPlayerEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.InteractionResult;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Re-posts the Fabric callbacks onto {@link NeoForge#EVENT_BUS} so the @SubscribeEvent handlers
 * carried over from the NeoForge version run. Events that need a mixin are posted from
 * {@code LivingEntityEventsMixin} and {@code EntityEventsMixin}.
 * Client-side events are wired by hand in IronsSpellbooksClient.
 */
public final class FabricEventBridge {
    private FabricEventBridge() {
    }

    public static void init() {
        // Server/common handler classes. Client-only classes are driven from IronsSpellbooksClient.
        for (Class<?> handlers : new Class<?>[]{
                CameraShakeManager.class, SummonManager.class, DamageSources.class, BlightEffect.class,
                EchoingStrikesEffect.class, FrostbiteEffect.class, GluttonyEffect.class, OakskinEffect.class,
                SpiderAspectEffect.class, TrueInvisibilityEffect.class, GuidingBoltManager.class,
                WizardAIEvents.class, BetrayerSignetRingItem.class, ExpulsionRing.class, LurkerRing.class,
                ServerPlayerEvents.class, io.redspace.ironsspellbooks.entity.mobs.ice_spider.IceSpiderEntity.class}) {
            NeoForge.EVENT_BUS.register(handlers);
        }

        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> NeoForge.EVENT_BUS.post(new EntityJoinLevelEvent(entity, level)));
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, level) -> NeoForge.EVENT_BUS.post(new EntityLeaveLevelEvent(entity)));

        ServerTickEvents.END_SERVER_TICK.register(server -> NeoForge.EVENT_BUS.post(new ServerTickEvent.Post(server)));
        ServerTickEvents.END_WORLD_TICK.register(level -> NeoForge.EVENT_BUS.post(new LevelTickEvent.Post(level)));
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> NeoForge.EVENT_BUS.post(new ServerStoppingEvent(server)));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> NeoForge.EVENT_BUS.post(new PlayerEvent.PlayerLoggedInEvent(handler.getPlayer())));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> NeoForge.EVENT_BUS.post(new PlayerEvent.PlayerLoggedOutEvent(handler.getPlayer())));
        net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> NeoForge.EVENT_BUS.post(new PlayerEvent.PlayerRespawnEvent(newPlayer)));

        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) -> !NeoForge.EVENT_BUS.post(new LivingDeathEvent(entity, source)).isCanceled());
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.living.LivingDropsEvent(entity, source)));
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamage, damageTaken, blocked) -> NeoForge.EVENT_BUS.post(new LivingDamageEvent.Post(entity, source, damageTaken)));

        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            var event = new PlayerInteractEvent.RightClickBlock(player, level, hit.getBlockPos(), player.getItemInHand(hand));
            NeoForge.EVENT_BUS.post(event);
            return event.isCanceled() ? InteractionResult.FAIL : InteractionResult.PASS;
        });
        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) ->
                !NeoForge.EVENT_BUS.post(new BlockEvent.BreakEvent(level, pos, player)).isCanceled());

        net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) ->
                NeoForge.EVENT_BUS.post(new PlayerEvent.Clone(newPlayer, oldPlayer, !alive)));
        net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) ->
                NeoForge.EVENT_BUS.post(new PlayerEvent.PlayerChangedDimensionEvent(player)));
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents.EQUIPMENT_CHANGE.register((entity, slot, previous, current) ->
                NeoForge.EVENT_BUS.post(new LivingEquipmentChangeEvent(entity, previous, current)));
        net.fabricmc.fabric.api.event.player.UseItemCallback.EVENT.register((player, level, hand) -> {
            var stack = player.getItemInHand(hand);
            var event = new PlayerInteractEvent.RightClickItem(player, stack, hand);
            NeoForge.EVENT_BUS.post(event);
            return event.isCanceled()
                    ? new net.minecraft.world.InteractionResultHolder<>(event.getCancellationResult(), stack)
                    : net.minecraft.world.InteractionResult.PASS;
        });
        net.fabricmc.fabric.api.event.player.UseEntityCallback.EVENT.register((player, level, hand, entity, hit) -> {
            var event = new PlayerInteractEvent.EntityInteractSpecific(player, entity, hand);
            NeoForge.EVENT_BUS.post(event);
            return event.isCanceled() ? event.getCancellationResult() : InteractionResult.PASS;
        });
    }
}
