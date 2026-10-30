package io.redspace.ironsspellbooks;

import net.minecraft.world.entity.EntityTypes;
import io.redspace.ironsspellbooks.entity.VisualFallingBlockRenderer;
import io.redspace.ironsspellbooks.entity.mobs.dead_king_boss.DeadKingRenderer;
import io.redspace.ironsspellbooks.entity.mobs.dead_king_boss.DeadKingSoulRenderer;
import io.redspace.ironsspellbooks.entity.mobs.dead_king_boss.undead_spawner.UndeadRiftRenderer;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.fire_orb.OminousFireOrbRenderer;
import io.redspace.ironsspellbooks.entity.spells.acid_orb.AcidOrbRenderer;
import io.redspace.ironsspellbooks.entity.spells.ball_lightning.BallLightningRenderer;
import io.redspace.ironsspellbooks.entity.spells.echoing_strikes.EchoingMagicArrowRenderer;
import io.redspace.ironsspellbooks.entity.spells.echoing_strikes.EchoingSwordModel;
import io.redspace.ironsspellbooks.entity.spells.echoing_strikes.MagicSwordRenderer;
import io.redspace.ironsspellbooks.entity.spells.eldritch_blast.EldritchBlastRenderer;
import io.redspace.ironsspellbooks.entity.spells.ender_chain.ArcaneShackleRenderer;
import io.redspace.ironsspellbooks.entity.spells.ender_chain.EnderChainRenderer;
import io.redspace.ironsspellbooks.entity.spells.fireball.FireballRenderer;
import io.redspace.ironsspellbooks.entity.spells.firebolt.FireboltRenderer;
import io.redspace.ironsspellbooks.entity.spells.guiding_bolt.GuidingBoltRenderer;
import io.redspace.ironsspellbooks.entity.spells.gust.GustRenderer;
import io.redspace.ironsspellbooks.entity.spells.ice_spike.IceSpikeRenderer;
import io.redspace.ironsspellbooks.entity.spells.ice_tomb.IceTombRenderer;
import io.redspace.ironsspellbooks.entity.spells.icicle.IcicleRenderer;
import io.redspace.ironsspellbooks.entity.spells.ray_of_frost.RayOfFrostRenderer;
import io.redspace.ironsspellbooks.entity.spells.scapegoat.ScapegoatRenderer;
import io.redspace.ironsspellbooks.entity.spells.shield.ShieldModel;
import io.redspace.ironsspellbooks.entity.spells.shield.ShieldTrimModel;
import io.redspace.ironsspellbooks.entity.spells.skull_projectile.SkullProjectileRenderer;
import io.redspace.ironsspellbooks.item.weapons.pyrium_staff.PyriumStaffHeadModel;
import io.redspace.ironsspellbooks.item.weapons.pyrium_staff.PyriumStaffOrbModel;
import io.redspace.ironsspellbooks.particle.DragonFireParticle;
import io.redspace.ironsspellbooks.particle.FallingSparkleParticle;
import io.redspace.ironsspellbooks.particle.SoulfireRayParticle;
import io.redspace.ironsspellbooks.particle.TintedBubblePopParticle;
import io.redspace.ironsspellbooks.render.AngelWingsModel;
import io.redspace.ironsspellbooks.render.ArmorCapeLayer;
import io.redspace.ironsspellbooks.render.EnergySwirlLayer;
import io.redspace.ironsspellbooks.render.ReplacedFireballRenderer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import io.redspace.ironsspellbooks.gui.arcane_anvil.ArcaneAnvilScreen;
import io.redspace.ironsspellbooks.gui.inscription_table.InscriptionTableScreen;
import io.redspace.ironsspellbooks.gui.overlays.ActiveSpellOverlay;
import io.redspace.ironsspellbooks.gui.overlays.CastBarOverlay;
import io.redspace.ironsspellbooks.gui.overlays.ManaBarOverlay;
import io.redspace.ironsspellbooks.gui.overlays.RecastOverlay;
import io.redspace.ironsspellbooks.gui.overlays.ScreenEffectsOverlay;
import io.redspace.ironsspellbooks.gui.overlays.ScreenTooltipOverlay;
import io.redspace.ironsspellbooks.gui.overlays.SpellBarOverlay;
import io.redspace.ironsspellbooks.gui.overlays.SpellWheelOverlay;
import io.redspace.ironsspellbooks.gui.scroll_forge.ScrollForgeScreen;
import io.redspace.ironsspellbooks.player.ClientInputEvents;
import io.redspace.ironsspellbooks.player.ClientPlayerEvents;
import io.redspace.ironsspellbooks.player.KeyMappings;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.registries.BlockRegistry;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.registries.MenuRegistry;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.config.ClientConfigs;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import io.redspace.ironsspellbooks.network.CauldronVisualSyncPacket;
import io.redspace.ironsspellbooks.render.animation.AnimationHelper;
import io.redspace.ironsspellbooks.setup.ClientMessages;
import io.redspace.ironsspellbooks.util.MinecraftInstanceHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import io.redspace.ironsspellbooks.item.WaywardCompass;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;


import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;

import java.util.HashSet;
import java.util.Set;

public class IronsSpellbooksClient implements ClientModInitializer {
    private static final Set<EntityType<?>> REGISTERED_RENDERERS = new HashSet<>();

    @Override
    public void onInitializeClient() {
        MinecraftInstanceHelper.instance = () -> Minecraft.getInstance().player;
        io.redspace.ironsspellbooks.render.ItemModelRegistration.register();
        AnimationHelper.initializePlayerAnimationFactory();
        ClientMessages.registerClientReceivers();
        registerKeyMappingsAndInputTick();
        registerClientLifecycleCallbacks();
        registerTooltipCallbacks();

        registerHudOverlays();
        io.redspace.ironsspellbooks.setup.ClientEventBridge.init();
        registerModelLayers();
        registerEntityRenderers();
        registerPlayerRenderLayers();
        registerFallbackEntityRenderers();
        registerBlockEntityRenderers();
        io.redspace.ironsspellbooks.particle.ParticleProviders.register();
        MenuScreens.register(MenuRegistry.INSCRIPTION_TABLE_MENU.get(), InscriptionTableScreen::new);
        MenuScreens.register(MenuRegistry.SCROLL_FORGE_MENU.get(), ScrollForgeScreen::new);
        MenuScreens.register(MenuRegistry.ARCANE_ANVIL_MENU.get(), ArcaneAnvilScreen::new);
    }

    private static void registerKeyMappingsAndInputTick() {
        KeyMappingHelper.registerKeyMapping(KeyMappings.SPELL_WHEEL_KEYMAP);
        KeyMappingHelper.registerKeyMapping(KeyMappings.SPELL_WHEEL_TOGGLE_KEYMAP);
        KeyMappingHelper.registerKeyMapping(KeyMappings.SPELLBOOK_CAST_ACTIVE_KEYMAP);
        KeyMappingHelper.registerKeyMapping(KeyMappings.SPELLBAR_SCROLL_MODIFIER_KEYMAP);
        for (var quickCast : KeyMappings.QUICK_CAST_MAPPINGS) {
            KeyMappingHelper.registerKeyMapping(quickCast);
        }
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            CauldronVisualSyncPacket.flushPending();
            ClientInputEvents.onClientTick(new net.neoforged.neoforge.client.event.ClientTickEvent.Post());
            if (client.player != null) {
                ClientPlayerEvents.onPlayerTick(new net.neoforged.neoforge.event.tick.PlayerTickEvent.Pre(client.player));
                if (client.level != null) {
                    for (Entity entity : client.level.getEntities((Entity) null, client.player.getBoundingBox().inflate(128), candidate -> candidate instanceof LivingEntity)) {
                        ClientPlayerEvents.tickSyncedEffects(entity);
                    }
                } else {
                    ClientPlayerEvents.tickSyncedEffects(client.player);
                }
            }
        });
    }

    private static void registerClientLifecycleCallbacks() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.player != null) {
                ClientPlayerEvents.onPlayerLogin(new net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent(client.player));
            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (client.player != null) {
                ClientPlayerEvents.onPlayerLogOut(new net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut(client.player));
            }
        });
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) ->
                ClientPlayerEvents.onPlayerOpenScreen(new net.neoforged.neoforge.client.event.ScreenEvent.Opening()));
    }

    private static void registerHudOverlays() {
        // same layer order as the original: cast bar under the crosshair, mana after air, spell/recast bars after the xp bar
        HudElementRegistry.attachElementBefore(VanillaHudElements.CROSSHAIR, IronsSpellbooks.id("cast_bar"), CastBarOverlay.instance);
        HudElementRegistry.attachElementAfter(VanillaHudElements.AIR_BAR, IronsSpellbooks.id("mana_bar"), ManaBarOverlay.instance);
        HudElementRegistry.attachElementAfter(VanillaHudElements.INFO_BAR, IronsSpellbooks.id("spell_bar"), SpellBarOverlay.instance);
        HudElementRegistry.attachElementAfter(VanillaHudElements.INFO_BAR, IronsSpellbooks.id("recast"), RecastOverlay.instance);
        HudElementRegistry.addLast(IronsSpellbooks.id("active_spell"), ActiveSpellOverlay.instance);
        HudElementRegistry.addLast(IronsSpellbooks.id("spell_wheel"), SpellWheelOverlay.instance);
        HudElementRegistry.addLast(IronsSpellbooks.id("screen_effects"), ScreenEffectsOverlay.instance);
        HudElementRegistry.addLast(IronsSpellbooks.id("screen_tooltip"), ScreenTooltipOverlay.instance);
        // the mana bar takes the experience bar's place when anchored to it
        for (var vanilla : new Identifier[]{VanillaHudElements.INFO_BAR, VanillaHudElements.EXPERIENCE_LEVEL}) {
            HudElementRegistry.replaceElement(vanilla, original -> (graphics, deltaTracker) -> {
                var player = Minecraft.getInstance().player;
                if (ClientConfigs.safeGet(ClientConfigs.MANA_BAR_ANCHOR) == ManaBarOverlay.Anchor.XP && ManaBarOverlay.shouldShowManaBar(player)) {
                    return;
                }
                original.extractRenderState(graphics, deltaTracker);
            });
        }
    }

    private static void registerTooltipCallbacks() {
        ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> {
            var event = new net.neoforged.neoforge.event.entity.player.ItemTooltipEvent(stack, lines, flag);
            ClientPlayerEvents.imbuedWeaponTooltips(event);
            ClientPlayerEvents.customPotionTooltips(event);
        });
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <T extends Entity> void registerEntityRenderer(EntityType<? extends T> entityType, EntityRendererProvider<T> provider) {
        EntityRendererRegistry.register((EntityType) entityType, provider);
        REGISTERED_RENDERERS.add(entityType);
    }

    private static void registerEntityRenderers() {
        registerEntityRenderer(EntityRegistry.WISP.get(), io.redspace.ironsspellbooks.entity.spells.wisp.WispRenderer::new);
        registerEntityRenderer(EntityRegistry.SPECTRAL_HAMMER.get(), io.redspace.ironsspellbooks.entity.spells.spectral_hammer.SpectralHammerRenderer::new);
        registerEntityRenderer(EntityRegistry.MAGIC_MISSILE_PROJECTILE.get(), io.redspace.ironsspellbooks.entity.spells.magic_missile.MagicMissileRenderer::new);
        registerEntityRenderer(EntityRegistry.THROWN_ITEM.get(), io.redspace.ironsspellbooks.entity.spells.thrown_item.ThrownItemRenderer::new);
        registerEntityRenderer(EntityRegistry.BLOOD_SLASH_PROJECTILE.get(), io.redspace.ironsspellbooks.entity.spells.blood_slash.BloodSlashRenderer::new);
        registerEntityRenderer(EntityRegistry.ELECTROCUTE_PROJECTILE.get(), io.redspace.ironsspellbooks.entity.spells.electrocute.ElectrocuteRenderer::new);
        registerEntityRenderer(EntityRegistry.FIREBOLT_PROJECTILE.get(), io.redspace.ironsspellbooks.entity.spells.firebolt.FireboltRenderer::new);
        registerEntityRenderer(EntityRegistry.ICICLE_PROJECTILE.get(), io.redspace.ironsspellbooks.entity.spells.icicle.IcicleRenderer::new);
        registerEntityRenderer(EntityRegistry.DEBUG_WIZARD.get(), io.redspace.ironsspellbooks.entity.mobs.debug_wizard.DebugWizardRenderer::new);
        registerEntityRenderer(EntityRegistry.SPECTRAL_STEED.get(), io.redspace.ironsspellbooks.entity.mobs.horse.SpectralSteedRenderer::new);
        registerEntityRenderer(EntityRegistry.SHIELD_ENTITY.get(), io.redspace.ironsspellbooks.entity.spells.shield.ShieldRenderer::new);
        registerEntityRenderer(EntityRegistry.WALL_OF_FIRE_ENTITY.get(), io.redspace.ironsspellbooks.entity.spells.wall_of_fire.WallOfFireRenderer::new);
        registerEntityRenderer(EntityRegistry.SUMMONED_VEX.get(), net.minecraft.client.renderer.entity.VexRenderer::new);
        registerEntityRenderer(EntityRegistry.PYROMANCER.get(), io.redspace.ironsspellbooks.entity.mobs.wizards.pyromancer.PyromancerRenderer::new);
        registerEntityRenderer(EntityRegistry.CRYOMANCER.get(), io.redspace.ironsspellbooks.entity.mobs.wizards.cryomancer.CryomancerRenderer::new);
        registerEntityRenderer(EntityRegistry.LIGHTNING_LANCE_PROJECTILE.get(), io.redspace.ironsspellbooks.entity.spells.lightning_lance.LightningLanceRenderer::new);
        registerEntityRenderer(EntityRegistry.NECROMANCER.get(), io.redspace.ironsspellbooks.entity.mobs.necromancer.NecromancerRenderer::new);
        registerEntityRenderer(EntityRegistry.SUMMONED_ZOMBIE.get(), io.redspace.ironsspellbooks.entity.mobs.raise_dead_summons.SummonedZombieMultiRenderer::new);
        registerEntityRenderer(EntityRegistry.SUMMONED_SKELETON.get(), io.redspace.ironsspellbooks.entity.mobs.raise_dead_summons.SummonedSkeletonMultiRenderer::new);
        registerEntityRenderer(EntityRegistry.WITHER_SKULL_PROJECTILE.get(), context -> new io.redspace.ironsspellbooks.entity.spells.skull_projectile.SkullProjectileRenderer(context, IronsSpellbooks.id("textures/entity/wither_skull.png")));
        registerEntityRenderer(EntityRegistry.MAGIC_ARROW_PROJECTILE.get(), io.redspace.ironsspellbooks.entity.spells.magic_arrow.MagicArrowRenderer::new);
        registerEntityRenderer(EntityRegistry.CREEPER_HEAD_PROJECTILE.get(), context -> new io.redspace.ironsspellbooks.entity.spells.skull_projectile.SkullProjectileRenderer(context, IronsSpellbooks.id("textures/entity/creeper_head.png")));
        registerEntityRenderer(EntityRegistry.FROZEN_HUMANOID.get(), io.redspace.ironsspellbooks.entity.mobs.frozen_humanoid.FrozenHumanoidRenderer::new);
        registerEntityRenderer(EntityRegistry.SMALL_FIREBALL_PROJECTILE.get(), context -> new io.redspace.ironsspellbooks.entity.spells.fireball.FireballRenderer(context, 0.5f));
        registerEntityRenderer(EntityRegistry.MAGIC_FIREBALL.get(), context -> new io.redspace.ironsspellbooks.entity.spells.fireball.FireballRenderer(context, 1.0f));
        registerEntityRenderer(EntityRegistry.SUMMONED_POLAR_BEAR.get(), net.minecraft.client.renderer.entity.PolarBearRenderer::new);
        registerEntityRenderer(EntityRegistry.DEAD_KING.get(), io.redspace.ironsspellbooks.entity.mobs.dead_king_boss.DeadKingRenderer::new);
        registerEntityRenderer(EntityRegistry.CATACOMBS_ZOMBIE.get(), net.minecraft.client.renderer.entity.ZombieRenderer::new);
        registerEntityRenderer(EntityRegistry.ARCHEVOKER.get(), io.redspace.ironsspellbooks.entity.mobs.wizards.archevoker.ArchevokerRenderer::new);
        registerEntityRenderer(EntityRegistry.MAGEHUNTER_VINDICATOR.get(), net.minecraft.client.renderer.entity.VindicatorRenderer::new);
        registerEntityRenderer(EntityRegistry.KEEPER.get(), io.redspace.ironsspellbooks.entity.mobs.keeper.KeeperRenderer::new);
        registerEntityRenderer(EntityRegistry.FIRE_BOSS.get(), io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.FireBossRenderer::new);
        registerEntityRenderer(EntityRegistry.SCULK_TENTACLE.get(), io.redspace.ironsspellbooks.entity.spells.void_tentacle.VoidTentacleRenderer::new);
        registerEntityRenderer(EntityRegistry.ICE_BLOCK_PROJECTILE.get(), io.redspace.ironsspellbooks.entity.spells.ice_block.IceBlockRenderer::new);
        registerEntityRenderer(EntityRegistry.SUNBEAM.get(), io.redspace.ironsspellbooks.entity.spells.sunbeam.SunbeamRenderer::new);
        registerEntityRenderer(EntityRegistry.POISON_ARROW.get(), io.redspace.ironsspellbooks.entity.spells.poison_arrow.PoisonArrowRenderer::new);
        registerEntityRenderer(EntityRegistry.SMALL_MAGIC_ARROW.get(), io.redspace.ironsspellbooks.entity.spells.small_magic_arrow.SmallMagicArrowRenderer::new);
        registerEntityRenderer(EntityRegistry.ACID_ORB.get(), io.redspace.ironsspellbooks.entity.spells.acid_orb.AcidOrbRenderer::new);
        registerEntityRenderer(EntityRegistry.ROOT.get(), io.redspace.ironsspellbooks.entity.spells.root.RootRenderer::new);
        registerEntityRenderer(EntityRegistry.BLACK_HOLE.get(), io.redspace.ironsspellbooks.entity.spells.black_hole.BlackHoleRenderer::new);
        registerEntityRenderer(EntityRegistry.BLOOD_NEEDLE.get(), io.redspace.ironsspellbooks.entity.spells.blood_needle.BloodNeedleRenderer::new);
        registerEntityRenderer(EntityRegistry.FIRE_BOMB.get(), io.redspace.ironsspellbooks.entity.spells.magma_ball.MagmaBallRenderer::new);
        registerEntityRenderer(EntityRegistry.COMET.get(), context -> new io.redspace.ironsspellbooks.entity.spells.comet.CometRenderer(context, 1.2f));
        registerEntityRenderer(EntityRegistry.TARGET_AREA_ENTITY.get(), io.redspace.ironsspellbooks.entity.spells.target_area.TargetAreaRenderer::new);
        registerEntityRenderer(EntityRegistry.PRIEST.get(), io.redspace.ironsspellbooks.entity.mobs.wizards.priest.PriestRenderer::new);
        registerEntityRenderer(EntityRegistry.GUIDING_BOLT.get(), io.redspace.ironsspellbooks.entity.spells.guiding_bolt.GuidingBoltRenderer::new);
        registerEntityRenderer(EntityRegistry.GUST_COLLIDER.get(), io.redspace.ironsspellbooks.entity.spells.gust.GustRenderer::new);
        registerEntityRenderer(EntityRegistry.RAY_OF_FROST_VISUAL_ENTITY.get(), io.redspace.ironsspellbooks.entity.spells.ray_of_frost.RayOfFrostRenderer::new);
        registerEntityRenderer(EntityRegistry.ELDRITCH_BLAST_VISUAL_ENTITY.get(), io.redspace.ironsspellbooks.entity.spells.eldritch_blast.EldritchBlastRenderer::new);
        registerEntityRenderer(EntityRegistry.DEVOUR_JAW.get(), io.redspace.ironsspellbooks.entity.spells.devour_jaw.DevourJawRenderer::new);
        registerEntityRenderer(EntityRegistry.PORTAL.get(), io.redspace.ironsspellbooks.entity.spells.portal.PortalRenderer::new);
        registerEntityRenderer(EntityRegistry.APOTHECARIST.get(), io.redspace.ironsspellbooks.entity.mobs.wizards.alchemist.ApothecaristRenderer::new);
        registerEntityRenderer(EntityRegistry.CULTIST.get(), io.redspace.ironsspellbooks.entity.mobs.wizards.cultist.CultistRenderer::new);
        registerEntityRenderer(EntityRegistry.BALL_LIGHTNING.get(), io.redspace.ironsspellbooks.entity.spells.ball_lightning.BallLightningRenderer::new);
        registerEntityRenderer(EntityRegistry.ICE_SPIKE.get(), io.redspace.ironsspellbooks.entity.spells.ice_spike.IceSpikeRenderer::new);
        registerEntityRenderer(EntityRegistry.FIRE_ARROW_PROJECTILE.get(), io.redspace.ironsspellbooks.entity.spells.fire_arrow.FireArrowRenderer::new);
        registerEntityRenderer(EntityRegistry.FIERY_DAGGER_PROJECTILE.get(), io.redspace.ironsspellbooks.entity.spells.fiery_dagger.FieryDaggerRenderer::new);
        registerEntityRenderer(EntityRegistry.CURSED_ARMOR_STAND.get(), io.redspace.ironsspellbooks.entity.mobs.wizards.cursed_armor_stand.CursedArmorStandRenderer::new);
        registerEntityRenderer(EntityRegistry.THUNDERSTEP_PROJECTILE.get(), io.redspace.ironsspellbooks.entity.spells.thunderstep.ThunderstepProjectileRenderer::new);
        registerEntityRenderer(EntityRegistry.SUMMONED_SWORD.get(), context -> new io.redspace.ironsspellbooks.entity.spells.summoned_weapons.SummonedSwordRenderer(context, io.redspace.ironsspellbooks.entity.spells.summoned_weapons.SummonedSwordModel::new));
        registerEntityRenderer(EntityRegistry.SUMMONED_CLAYMORE.get(), context -> new io.redspace.ironsspellbooks.entity.spells.summoned_weapons.SummonedSwordRenderer(context, io.redspace.ironsspellbooks.entity.spells.summoned_weapons.SummonedClaymoreModel::new));
        registerEntityRenderer(EntityRegistry.SUMMONED_RAPIER.get(), context -> new io.redspace.ironsspellbooks.entity.spells.summoned_weapons.SummonedSwordRenderer(context, io.redspace.ironsspellbooks.entity.spells.summoned_weapons.SummonedRapierModel::new));
        registerEntityRenderer(EntityRegistry.ICE_SPIDER.get(), io.redspace.ironsspellbooks.entity.mobs.ice_spider.IceSpiderRenderer::new);
        registerEntityRenderer(EntityRegistry.ICE_TOMB.get(), io.redspace.ironsspellbooks.entity.spells.ice_tomb.IceTombRenderer::new);
        registerEntityRenderer(EntityRegistry.SNOWBALL.get(), io.redspace.ironsspellbooks.entity.spells.snowball.SnowballRenderer::new);
        registerEntityRenderer(EntityRegistry.THROWN_SPEAR.get(), io.redspace.ironsspellbooks.entity.spells.thrown_spear.ThrownSpearRenderer::new);
        registerEntityRenderer(EntityTypes.SMALL_FIREBALL, (context) -> new ReplacedFireballRenderer(context, 0.75f, .75f));
        registerEntityRenderer(EntityTypes.FIREBALL, (context) -> new ReplacedFireballRenderer(context, 1.25f, 3f));
        registerEntityRenderer(EntityRegistry.CONE_OF_COLD_PROJECTILE.get(), NoopRenderer::new);
        registerEntityRenderer(EntityRegistry.FIRE_BREATH_PROJECTILE.get(), NoopRenderer::new);
        registerEntityRenderer(EntityRegistry.POISON_BREATH_PROJECTILE.get(), NoopRenderer::new);
        registerEntityRenderer(EntityRegistry.DRAGON_BREATH_PROJECTILE.get(), NoopRenderer::new);
        registerEntityRenderer(EntityRegistry.FIRE_ERUPTION_AOE.get(), NoopRenderer::new);
        registerEntityRenderer(EntityRegistry.DEAD_KING_CORPSE.get(), DeadKingRenderer::new);
        registerEntityRenderer(EntityRegistry.DEAD_KING_SOUL.get(), DeadKingSoulRenderer::new);
        registerEntityRenderer(EntityRegistry.POISON_CLOUD.get(), NoopRenderer::new);
        registerEntityRenderer(EntityRegistry.DRAGON_BREATH_POOL.get(), NoopRenderer::new);
        registerEntityRenderer(EntityRegistry.POISON_SPLASH.get(), NoopRenderer::new);
        registerEntityRenderer(EntityRegistry.FIRE_FIELD.get(), NoopRenderer::new);
        registerEntityRenderer(EntityRegistry.HEALING_AOE.get(), NoopRenderer::new);
        registerEntityRenderer(EntityRegistry.CHAIN_LIGHTNING.get(), NoopRenderer::new);
        registerEntityRenderer(EntityRegistry.FIREFLY_SWARM.get(), NoopRenderer::new);
        registerEntityRenderer(EntityRegistry.EARTHQUAKE_AOE.get(), NoopRenderer::new);
        registerEntityRenderer(EntityRegistry.BLIZZARD_AOE.get(), NoopRenderer::new);
        registerEntityRenderer(EntityRegistry.FALLING_BLOCK.get(), VisualFallingBlockRenderer::new);
        registerEntityRenderer(EntityRegistry.ARROW_VOLLEY_ENTITY.get(), NoopRenderer::new);
        registerEntityRenderer(EntityRegistry.STOMP_AOE.get(), NoopRenderer::new);
        registerEntityRenderer(EntityRegistry.ECHOING_STRIKE.get(), NoopRenderer::new);
        registerEntityRenderer(EntityRegistry.LIGHTNING_STRIKE.get(), NoopRenderer::new);
        registerEntityRenderer(EntityRegistry.FROST_FIELD.get(), NoopRenderer::new);
        registerEntityRenderer(EntityRegistry.OMINOUS_FIRE_ORB.get(), OminousFireOrbRenderer::new);
        registerEntityRenderer(EntityRegistry.UNDEAD_RIFT.get(), UndeadRiftRenderer::new);
        registerEntityRenderer(EntityRegistry.FANG_SWIRL.get(), NoopRenderer::new);
        registerEntityRenderer(EntityRegistry.ARCANE_SHACKLE.get(), ArcaneShackleRenderer::new);
        registerEntityRenderer(EntityRegistry.ENDER_CHAIN.get(), EnderChainRenderer::new);
        registerEntityRenderer(EntityRegistry.ECHOING_SWORD.get(), c -> new MagicSwordRenderer<>(c, EchoingSwordModel::new));
        registerEntityRenderer(EntityRegistry.ECHOING_ARROW.get(), EchoingMagicArrowRenderer::new);
        registerEntityRenderer(EntityRegistry.SCAPEGOAT.get(), ScapegoatRenderer::new);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void registerPlayerRenderLayers() {
        net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            registrationHelper.register(new io.redspace.ironsspellbooks.render.SpellTargetingLayer.Vanilla(entityRenderer));
            if (entityRenderer.getModel() instanceof net.minecraft.client.model.HumanoidModel) {
                registrationHelper.register(new io.redspace.ironsspellbooks.render.ArmorCapeLayer(entityRenderer));
                registrationHelper.register(new io.redspace.ironsspellbooks.render.GlowingEyesLayer.Vanilla(entityRenderer));
            }
            if (entityRenderer instanceof AvatarRenderer playerRenderer) {
                registrationHelper.register(new io.redspace.ironsspellbooks.render.EnergySwirlLayer.Vanilla(playerRenderer, io.redspace.ironsspellbooks.render.EnergySwirlLayer.CHARGE_TEXTURE, MobEffectRegistry.CHARGED));
                registrationHelper.register(new io.redspace.ironsspellbooks.render.EnergySwirlLayer.Vanilla(playerRenderer, io.redspace.ironsspellbooks.render.EnergySwirlLayer.EVASION_TEXTURE, MobEffectRegistry.EVASION));
                registrationHelper.register(new io.redspace.ironsspellbooks.render.EchoingStrikesHologramLayer.Vanilla(playerRenderer));
                registrationHelper.register(new io.redspace.ironsspellbooks.render.ChargeSpellLayer.Vanilla(playerRenderer));
            }
        });
    }

    private static void registerModelLayer(ModelLayerLocation location, java.util.function.Supplier<net.minecraft.client.model.geom.builders.LayerDefinition> supplier) {
        ModelLayerRegistry.registerModelLayer(location, supplier::get);
    }

    private static void registerModelLayers() {
        registerModelLayer(io.redspace.ironsspellbooks.entity.spells.acid_orb.AcidOrbRenderer.MODEL_LAYER_LOCATION, io.redspace.ironsspellbooks.entity.spells.acid_orb.AcidOrbRenderer::createBodyLayer);
        registerModelLayer(io.redspace.ironsspellbooks.entity.spells.ball_lightning.BallLightningRenderer.MODEL_LAYER_LOCATION, io.redspace.ironsspellbooks.entity.spells.ball_lightning.BallLightningRenderer::createBodyLayer);
        registerModelLayer(io.redspace.ironsspellbooks.entity.spells.eldritch_blast.EldritchBlastRenderer.MODEL_LAYER_LOCATION, io.redspace.ironsspellbooks.entity.spells.eldritch_blast.EldritchBlastRenderer::createBodyLayer);
        registerModelLayer(io.redspace.ironsspellbooks.entity.spells.fireball.FireballRenderer.MODEL_LAYER_LOCATION, io.redspace.ironsspellbooks.entity.spells.fireball.FireballRenderer::createBodyLayer);
        registerModelLayer(io.redspace.ironsspellbooks.entity.spells.firebolt.FireboltRenderer.MODEL_LAYER_LOCATION, io.redspace.ironsspellbooks.entity.spells.firebolt.FireboltRenderer::createBodyLayer);
        registerModelLayer(io.redspace.ironsspellbooks.entity.spells.guiding_bolt.GuidingBoltRenderer.MODEL_LAYER_LOCATION, io.redspace.ironsspellbooks.entity.spells.guiding_bolt.GuidingBoltRenderer::createBodyLayer);
        registerModelLayer(io.redspace.ironsspellbooks.entity.spells.gust.GustRenderer.MODEL_LAYER_LOCATION, io.redspace.ironsspellbooks.entity.spells.gust.GustRenderer::createBodyLayer);
        registerModelLayer(io.redspace.ironsspellbooks.entity.spells.ice_spike.IceSpikeRenderer.IceSpikeModel.LAYER_LOCATION, io.redspace.ironsspellbooks.entity.spells.ice_spike.IceSpikeRenderer.IceSpikeModel::createBodyLayer);
        registerModelLayer(io.redspace.ironsspellbooks.entity.spells.ice_tomb.IceTombRenderer.IceTombModel.LAYER_LOCATION, io.redspace.ironsspellbooks.entity.spells.ice_tomb.IceTombRenderer.IceTombModel::createBodyLayer);
        registerModelLayer(io.redspace.ironsspellbooks.entity.spells.icicle.IcicleRenderer.MODEL_LAYER_LOCATION, io.redspace.ironsspellbooks.entity.spells.icicle.IcicleRenderer::createBodyLayer);
        registerModelLayer(io.redspace.ironsspellbooks.entity.spells.ray_of_frost.RayOfFrostRenderer.MODEL_LAYER_LOCATION, io.redspace.ironsspellbooks.entity.spells.ray_of_frost.RayOfFrostRenderer::createBodyLayer);
        registerModelLayer(io.redspace.ironsspellbooks.entity.spells.shield.ShieldModel.LAYER_LOCATION, io.redspace.ironsspellbooks.entity.spells.shield.ShieldModel::createBodyLayer);
        registerModelLayer(io.redspace.ironsspellbooks.entity.spells.shield.ShieldTrimModel.LAYER_LOCATION, io.redspace.ironsspellbooks.entity.spells.shield.ShieldTrimModel::createBodyLayer);
        registerModelLayer(io.redspace.ironsspellbooks.entity.spells.skull_projectile.SkullProjectileRenderer.MODEL_LAYER_LOCATION, io.redspace.ironsspellbooks.entity.spells.skull_projectile.SkullProjectileRenderer::createBodyLayer);
        registerModelLayer(io.redspace.ironsspellbooks.render.ArmorCapeLayer.ARMOR_CAPE_LAYER, io.redspace.ironsspellbooks.render.ArmorCapeLayer::createBodyLayer);
        registerModelLayer(io.redspace.ironsspellbooks.render.AngelWingsModel.ANGEL_WINGS_LAYER, io.redspace.ironsspellbooks.render.AngelWingsModel::createLayer);
        registerModelLayer(io.redspace.ironsspellbooks.render.EnergySwirlLayer.Vanilla.ENERGY_LAYER, () ->
                net.minecraft.client.model.geom.builders.LayerDefinition.create(net.minecraft.client.model.HumanoidModel.createMesh(new net.minecraft.client.model.geom.builders.CubeDeformation(0.0F), 0.0F), 64, 64));
        registerModelLayer(io.redspace.ironsspellbooks.item.weapons.pyrium_staff.PyriumStaffHeadModel.LAYER_LOCATION, io.redspace.ironsspellbooks.item.weapons.pyrium_staff.PyriumStaffHeadModel::createBodyLayer);
        registerModelLayer(io.redspace.ironsspellbooks.item.weapons.pyrium_staff.PyriumStaffOrbModel.LAYER_LOCATION, io.redspace.ironsspellbooks.item.weapons.pyrium_staff.PyriumStaffOrbModel::createBodyLayer);
        registerModelLayer(DeadKingSoulRenderer.MODEL_LAYER_LOCATION, DeadKingSoulRenderer::createBodyLayer);
        registerModelLayer(DeadKingSoulRenderer.CROWN_CUBE_LAYER_LOCATION, DeadKingSoulRenderer::createCrownCubeLayer);
    }

    private static void registerBlockEntityRenderers() {
        BlockEntityRendererRegistry.register(BlockRegistry.SCROLL_FORGE_TILE.get(), io.redspace.ironsspellbooks.block.scroll_forge.ScrollForgeRenderer::new);
        BlockEntityRendererRegistry.register(BlockRegistry.PEDESTAL_TILE.get(), io.redspace.ironsspellbooks.block.pedestal.PedestalRenderer::new);
        BlockEntityRendererRegistry.register(BlockRegistry.ALCHEMIST_CAULDRON_TILE.get(), io.redspace.ironsspellbooks.block.alchemist_cauldron.AlchemistCauldronRenderer::new);
        BlockEntityRendererRegistry.register(BlockRegistry.PORTAL_FRAME_BLOCK_ENTITY.get(), io.redspace.ironsspellbooks.block.portal_frame.PortalFrameRenderer::new);
    }



    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void registerFallbackEntityRenderers() {
        for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
            Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
            if (id != null && IronsSpellbooks.MODID.equals(id.getNamespace()) && !REGISTERED_RENDERERS.contains(entityType)) {
                EntityRendererRegistry.register((EntityType) entityType, NullEntityRenderer::new);
            }
        }
    }

    private static class NullEntityRenderer extends EntityRenderer<Entity, net.minecraft.client.renderer.entity.state.EntityRenderState> {
        protected NullEntityRenderer(EntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public boolean shouldRender(Entity livingEntity, Frustum camera, double camX, double camY, double camZ, float partialTick) {
            return false;
        }

        @Override
        public net.minecraft.client.renderer.entity.state.EntityRenderState createRenderState() {
            return new net.minecraft.client.renderer.entity.state.EntityRenderState();
        }
    }
}
