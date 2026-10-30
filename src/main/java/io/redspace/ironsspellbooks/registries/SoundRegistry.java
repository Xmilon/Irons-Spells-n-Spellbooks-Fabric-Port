package io.redspace.ironsspellbooks.registries;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;


public class SoundRegistry {

    public static void register() {
        // Class initialization registers native references before registry freeze.
    }

    //TODO: bring sound locations into spec
    public static Holder.Reference<SoundEvent> ARCANE_IMPACT = registerSoundEvent("entity.generic.arcane_impact");
    public static Holder.Reference<SoundEvent> GRAVITY_FISSURE_CHARGE = registerSoundEvent("spell.gravity_fissure.charge");
    public static Holder.Reference<SoundEvent> SOULFIRE_RAY_CAST = registerSoundEvent("spell.soulfire_ray.cast");
    public static Holder.Reference<SoundEvent> SOULFIRE_RAY_CHARGE = registerSoundEvent("spell.soulfire_ray.charge");
    public static Holder.Reference<SoundEvent> CHARGE_CHAINS = registerSoundEvent("spell.arcane_shackle.charge");
    public static Holder.Reference<SoundEvent> ECHOING_STRIKES_CAST = registerSoundEvent("spell.echoing_strikes.cast");
    public static Holder.Reference<SoundEvent> FORCE_IMPACT = registerSoundEvent("force_impact");
    public static Holder.Reference<SoundEvent> ICE_IMPACT = registerSoundEvent("ice_impact");
    public static Holder.Reference<SoundEvent> FIRE_IMPACT = registerSoundEvent("entity.generic.fire_impact");

    public static Holder.Reference<SoundEvent> MAGIC_SPELL_REVERSE_3 = registerSoundEvent("magic_spell_reverse_3");
    public static Holder.Reference<SoundEvent> ARIAL_SUMMONING_5_CUSTOM_1 = registerSoundEvent("arial_summoning_5_custom_1");
    public static Holder.Reference<SoundEvent> DARK_MAGIC_BUFF_03_CUSTOM_1 = registerSoundEvent("dark_magic_buff_03_custom_1");
    public static Holder.Reference<SoundEvent> DARK_SPELL_02 = registerSoundEvent("dark_spell_02");
    public static Holder.Reference<SoundEvent> LIGHTNING_WOOSH_01 = registerSoundEvent("lightning_woosh_01");

    public static Holder.Reference<SoundEvent> HEARTSTOP_CAST = registerSoundEvent("heartstop_cast");
    public static Holder.Reference<SoundEvent> LIGHTNING_LANCE_CAST = registerSoundEvent("lightning_lance_cast");
    public static Holder.Reference<SoundEvent> MAGIC_ARROW_RELEASE = registerSoundEvent("magic_arrow_release");
    public static Holder.Reference<SoundEvent> MAGIC_ARROW_CHARGE = registerSoundEvent("magic_arrow_charge");
    public static Holder.Reference<SoundEvent> FROST_STEP = registerSoundEvent("frost_step");
    public static Holder.Reference<SoundEvent> ABYSSAL_TELEPORT = registerSoundEvent("abyssal_teleport");
    public static Holder.Reference<SoundEvent> ABYSSAL_SHROUD = registerSoundEvent("cast.abyssal_shroud");
    public static Holder.Reference<SoundEvent> BLOOD_STEP = registerSoundEvent("cast.blood_step");
    public static Holder.Reference<SoundEvent> FIRE_BREATH_LOOP = registerSoundEvent("loop.fire_breath");
    public static Holder.Reference<SoundEvent> ELECTROCUTE_LOOP = registerSoundEvent("loop.electrocute");
    public static Holder.Reference<SoundEvent> CONE_OF_COLD_LOOP = registerSoundEvent("loop.cone_of_cold");
    public static Holder.Reference<SoundEvent> CLOUD_OF_REGEN_LOOP = registerSoundEvent("loop.cloud_of_regen");
    public static Holder.Reference<SoundEvent> RAISE_DEAD_START = registerSoundEvent("cast.raise_dead.start");
    public static Holder.Reference<SoundEvent> RAISE_DEAD_FINISH = registerSoundEvent("cast.raise_dead.finish");
    public static Holder.Reference<SoundEvent> VOID_TENTACLES_START = registerSoundEvent("cast.void_tentacles.start");
    public static Holder.Reference<SoundEvent> VOID_TENTACLES_FINISH = registerSoundEvent("cast.void_tentacles.finish");
    public static Holder.Reference<SoundEvent> VOID_TENTACLES_LEAVE = registerSoundEvent("entity.void_tentacles.retreat");
    public static Holder.Reference<SoundEvent> VOID_TENTACLES_AMBIENT = registerSoundEvent("entity.void_tentacles.ambient");
    public static Holder.Reference<SoundEvent> ICE_BLOCK_CAST = registerSoundEvent("cast.ice_block");
    public static Holder.Reference<SoundEvent> ICE_BLOCK_IMPACT = registerSoundEvent("entity.ice_block.impact");
    public static Holder.Reference<SoundEvent> RAY_OF_SIPHONING = registerSoundEvent("loop.ray_of_siphoning");
    public static Holder.Reference<SoundEvent> FIREBALL_START = registerSoundEvent("cast.fireball");
    public static Holder.Reference<SoundEvent> ACID_ORB_CHARGE = registerSoundEvent("spell.acid_orb.charge");
    public static Holder.Reference<SoundEvent> ACID_ORB_CAST = registerSoundEvent("spell.acid_orb.cast");
    public static Holder.Reference<SoundEvent> ACID_ORB_IMPACT = registerSoundEvent("entity.acid_orb.impact");
    public static Holder.Reference<SoundEvent> POISON_ARROW_CHARGE = registerSoundEvent("spell.poison_arrow.charge");
    public static Holder.Reference<SoundEvent> POISON_ARROW_CAST = registerSoundEvent("spell.poison_arrow.cast");
    public static Holder.Reference<SoundEvent> POISON_BREATH_LOOP = registerSoundEvent("spell.poison_breath.loop");
    public static Holder.Reference<SoundEvent> ROOT_EMERGE = registerSoundEvent("entity.root.emerge");
    public static Holder.Reference<SoundEvent> BLACK_HOLE_CHARGE = registerSoundEvent("spell.black_hole.charge");
    public static Holder.Reference<SoundEvent> BLACK_HOLE_CAST = registerSoundEvent("spell.black_hole.cast");
    public static Holder.Reference<SoundEvent> BLACK_HOLE_LOOP = registerSoundEvent("entity.black_hole.loop");
    public static Holder.Reference<SoundEvent> POISON_SPLASH_BEGIN = registerSoundEvent("spell.poison_splash.begin");
    public static Holder.Reference<SoundEvent> BLIGHT_BEGIN = registerSoundEvent("spell.blight.begin");
    public static Holder.Reference<SoundEvent> SPIDER_ASPECT_CAST = registerSoundEvent("spell.spider_aspect.cast");
    public static Holder.Reference<SoundEvent> BLOOD_NEEDLE_IMPACT = registerSoundEvent("entity.blood_needle.impact");
    public static Holder.Reference<SoundEvent> FIRE_BOMB_CHARGE = registerSoundEvent("spell.fire_bomb.charge");
    public static Holder.Reference<SoundEvent> FIRE_BOMB_CAST = registerSoundEvent("spell.fire_bomb.cast");
    public static Holder.Reference<SoundEvent> GUST_CHARGE = registerSoundEvent("spell.gust.charge");
    public static Holder.Reference<SoundEvent> GUST_CAST = registerSoundEvent("spell.gust.cast");
    public static Holder.Reference<SoundEvent> GUIDING_BOLT_IMPACT = registerSoundEvent("entity.guiding_bolt.impact");
    public static Holder.Reference<SoundEvent> GUIDING_BOLT_CAST = registerSoundEvent("spell.guiding_bolt.cast");
    public static Holder.Reference<SoundEvent> CHAIN_LIGHTNING_CHAIN = registerSoundEvent("entity.chain_lightning.lightning_chain");
    public static Holder.Reference<SoundEvent> DEVOUR_BITE = registerSoundEvent("entity.devour_jaw.bite");
    public static Holder.Reference<SoundEvent> KEEPER_SWING = registerSoundEvent("entity.citadel_keeper.swing");
    public static Holder.Reference<SoundEvent> KEEPER_STEP = registerSoundEvent("entity.citadel_keeper.step");
    public static Holder.Reference<SoundEvent> KEEPER_DEATH = registerSoundEvent("entity.citadel_keeper.death");
    public static Holder.Reference<SoundEvent> KEEPER_HURT = registerSoundEvent("entity.citadel_keeper.hurt");
    public static Holder.Reference<SoundEvent> KEEPER_SWORD_IMPACT = registerSoundEvent("entity.citadel_keeper.sword_impact");
    public static Holder.Reference<SoundEvent> KEEPER_IDLE = registerSoundEvent("entity.citadel_keeper.idle");
    public static Holder.Reference<SoundEvent> OAKSKIN_CAST = registerSoundEvent("spell.oakskin.cast");
    public static Holder.Reference<SoundEvent> TOUCH_DIG_CAST = registerSoundEvent("spell.touch_dig.cast");
    public static Holder.Reference<SoundEvent> EARTHQUAKE_LOOP = registerSoundEvent("entity.earthquake_aoe.loop");
    public static Holder.Reference<SoundEvent> EARTHQUAKE_IMPACT = registerSoundEvent("entity.earthquake_aoe.impact");
    public static Holder.Reference<SoundEvent> EARTHQUAKE_CAST = registerSoundEvent("spell.earthquake.cast");
    public static Holder.Reference<SoundEvent> FIREFLY_SWARM_IDLE = registerSoundEvent("entity.firefly_swarm.idle");
    public static Holder.Reference<SoundEvent> FIREFLY_SWARM_ATTACK = registerSoundEvent("entity.firefly_swarm.attack");
    public static Holder.Reference<SoundEvent> FIREFLY_SPELL_PREPARE = registerSoundEvent("spell.firefly_swarm.prepare");
    public static Holder.Reference<SoundEvent> RAY_OF_FROST = registerSoundEvent("spell.ray_of_frost.cast");
    public static Holder.Reference<SoundEvent> SONIC_BOOM = registerSoundEvent("spell.sonic_boom.cast");
    public static Holder.Reference<SoundEvent> DIVINE_SMITE_WINDUP = registerSoundEvent("spell.divine_smite.windup");
    public static Holder.Reference<SoundEvent> DIVINE_SMITE_CAST = registerSoundEvent("spell.divine_smite.cast");
    public static Holder.Reference<SoundEvent> TELEKINESIS_CAST = registerSoundEvent("spell.telekinesis.cast");
    public static Holder.Reference<SoundEvent> TELEKINESIS_LOOP = registerSoundEvent("spell.telekinesis.loop");
    public static Holder.Reference<SoundEvent> PLANAR_SIGHT_CAST = registerSoundEvent("spell.planar_sight.cast");
    public static Holder.Reference<SoundEvent> HEAT_SURGE_PREPARE = registerSoundEvent("spell.heat_surge.prepare");
    public static Holder.Reference<SoundEvent> FROSTWAVE_PREPARE = registerSoundEvent("spell.frostwave.prepare");
    public static Holder.Reference<SoundEvent> ARROW_VOLLEY_PREPARE = registerSoundEvent("spell.arrow_volley.prepare");
    public static Holder.Reference<SoundEvent> BOW_SHOOT = registerSoundEvent("bow_shoot");
    public static Holder.Reference<SoundEvent> RECALL_PREPARE = registerSoundEvent("spell.recall.prepare");
    public static Holder.Reference<SoundEvent> ELDRITCH_BLAST = registerSoundEvent("spell.eldritch_blast.cast");
    public static Holder.Reference<SoundEvent> FLAMING_STRIKE_UPSWING = registerSoundEvent("spell.flaming_strike.begin");
    public static Holder.Reference<SoundEvent> FLAMING_STRIKE_SWING = registerSoundEvent("spell.flaming_strike.cast");
    public static Holder.Reference<SoundEvent> SHOCKWAVE_CAST = registerSoundEvent("spell.shockwave.cast");
    public static Holder.Reference<SoundEvent> SHOCKWAVE_PREPARE = registerSoundEvent("spell.shockwave.prepare");
    public static Holder.Reference<SoundEvent> TRADER_YES = registerSoundEvent("entity.generic.trader.yes");
    public static Holder.Reference<SoundEvent> TRADER_NO = registerSoundEvent("entity.generic.trader.no");
    public static Holder.Reference<SoundEvent> SCORCH_PREPARE = registerSoundEvent("spell.scorch.prepare");
    public static Holder.Reference<SoundEvent> FIERY_EXPLOSION = registerSoundEvent("entity.generic.fiery_explosion");
    public static Holder.Reference<SoundEvent> ECHOING_STRIKE = registerSoundEvent("entity.echoing_strike.echoing_strike");
    public static Holder.Reference<SoundEvent> SMALL_LIGHTNING_STRIKE = registerSoundEvent("entity.lightning_strike.strike");
    public static Holder.Reference<SoundEvent> THUNDERSTORM_PREPARE = registerSoundEvent("spell.thunderstorm.prepare");
    public static Holder.Reference<SoundEvent> BLOOD_EXPLOSION = registerSoundEvent("spell.sacrifice.blood_explosion");
    public static Holder.Reference<SoundEvent> SUNBEAM_WINDUP = registerSoundEvent("entity.sunbeam.windup");
    public static Holder.Reference<SoundEvent> SUNBEAM_IMPACT = registerSoundEvent("entity.sunbeam.impact");
    public static Holder.Reference<SoundEvent> CLEANSE_CAST = registerSoundEvent("spell.cleanse.cast");
    public static Holder.Reference<SoundEvent> ICE_SPIKE_EMERGE = registerSoundEvent("entity.ice_spike.emerge");
    public static Holder.Reference<SoundEvent> FIRE_ARROW_CHARGE = registerSoundEvent("spell.fire_arrow.charge");
    public static Holder.Reference<SoundEvent> FIRE_ARROW_CAST = registerSoundEvent("spell.fire_arrow.cast");
    public static Holder.Reference<SoundEvent> RAISE_HELL_PREPARE = registerSoundEvent("spell.raise_hell.prepare");
    public static Holder.Reference<SoundEvent> FIERY_DAGGER_THROW = registerSoundEvent("entity.fiery_dagger.throw");
    public static Holder.Reference<SoundEvent> FIRE_DAGGER_PARRY = registerSoundEvent("entity.fiery_dagger.parry");
    public static Holder.Reference<SoundEvent> SOULCALLER_TOLL_SUCCESS = registerSoundEvent("item.cinderous_soulcaller.toll.success");
    public static Holder.Reference<SoundEvent> SOULCALLER_TOLL_FAILURE = registerSoundEvent("item.cinderous_soulcaller.toll.failure");
    public static Holder.Reference<SoundEvent> GENERIC_BLADE_SWING = registerSoundEvent("entity.generic.swing_blade");
    public static Holder.Reference<SoundEvent> SUMMONED_SWORDS_CHARGE = registerSoundEvent("spell.summon_swords.charge");
    public static Holder.Reference<SoundEvent> SUMMONED_SWORDS_CAST = registerSoundEvent("spell.summon_swords.cast");
    public static Holder.Reference<SoundEvent> POCKET_DIMENSION_TRAVEL = registerSoundEvent("spell.pocket_dimension.travel");
    public static Holder.Reference<SoundEvent> ELDRITCH_PREPARE = registerSoundEvent("cast.generic.eldritch_prepare");
    public static Holder.Reference<SoundEvent> ICE_SPIDER_HURT = registerSoundEvent("entity.ice_spider.hurt");
    public static Holder.Reference<SoundEvent> ICE_SPIDER_BITE = registerSoundEvent("entity.ice_spider.bite");
    public static Holder.Reference<SoundEvent> ICE_SPIDER_GRAPPLE_LATCH = registerSoundEvent("entity.ice_spider.grapple_latch");
    public static Holder.Reference<SoundEvent> ICE_SPIDER_GRAPPLE_SPIT = registerSoundEvent("entity.ice_spider.grapple_spit");
    public static Holder.Reference<SoundEvent> ICE_SPIDER_DEATH = registerSoundEvent("entity.ice_spider.death");
    public static Holder.Reference<SoundEvent> ICE_SPIDER_AMBIENT = registerSoundEvent("entity.ice_spider.ambient");
    public static Holder.Reference<SoundEvent> ICE_SPIDER_HOWL = registerSoundEvent("entity.ice_spider.howl");
    public static Holder.Reference<SoundEvent> ICE_SPIDER_SWING = registerSoundEvent("entity.ice_spider.swing");
    public static Holder.Reference<SoundEvent> FROSTBITE_FREEZE = registerSoundEvent("spell.frostbite.freeze");
    public static Holder.Reference<SoundEvent> SPEAR_RETURN = registerSoundEvent("item.spear.loyalty_return");
    public static Holder.Reference<SoundEvent> SPEAR_CHANNELING_STRIKE = registerSoundEvent("item.spear.channeling_strike");
    public static Holder.Reference<SoundEvent> SHADOW_SLASH = registerSoundEvent("spell.shadow_slash.cast");
    public static Holder.Reference<SoundEvent> SWING_WEAPON_WEIRD = registerSoundEvent("entity.generic.swing_weird");
    public static Holder.Reference<SoundEvent> THROW_DAGGER = registerSoundEvent("entity.generic.throw_dagger");

    public static Holder.Reference<SoundEvent> HELLRAZOR_SWING = registerSoundEvent("item.hellrazor.swing");
    public static Holder.Reference<SoundEvent> FIRE_ERUPTION_SLAM = registerSoundEvent("entity.fire_eruption.slam");
    public static Holder.Reference<SoundEvent> BOSS_STANCE_BREAK = registerSoundEvent("entity.fire_boss.stance_break");
    public static Holder.Reference<SoundEvent> FIRE_BOSS_HURT = registerSoundEvent("entity.fire_boss.hurt");
    public static Holder.Reference<SoundEvent> FIRE_BOSS_DEATH = registerSoundEvent("entity.fire_boss.death");
    public static Holder.Reference<SoundEvent> FIRE_BOSS_ACCENT = registerSoundEvent("entity.fire_boss.death_final");
    public static Holder.Reference<SoundEvent> FIRE_BOSS_TRANSITION_SOUL = registerSoundEvent("entity.fire_boss.enter_soul");
    public static Holder.Reference<SoundEvent> FIRE_BOSS_SUMMON_SCYTHE = registerSoundEvent("entity.fire_boss.summon_scythe");
    public static Holder.Reference<SoundEvent> FIRE_BOSS_FIREBALL = registerSoundEvent("entity.fire_boss.fireball");
    public static Holder.Reference<SoundEvent> MUSIC_FIRE_BOSS_MELODY_A = registerSoundEvent("music.fire_boss.melody_a");
    public static Holder.Reference<SoundEvent> MUSIC_FIRE_BOSS_MELODY_B = registerSoundEvent("music.fire_boss.melody_b");
    public static Holder.Reference<SoundEvent> MUSIC_FIRE_BOSS_BELLS_A = registerSoundEvent("music.fire_boss.bells_a");
    public static Holder.Reference<SoundEvent> MUSIC_FIRE_BOSS_BELLS_B = registerSoundEvent("music.fire_boss.bells_b");
    public static Holder.Reference<SoundEvent> MUSIC_FIRE_BOSS_BACKTRACK = registerSoundEvent("music.fire_boss.backtrack");
    public static Holder.Reference<SoundEvent> MUSIC_FIRE_BOSS_DRUMS = registerSoundEvent("music.fire_boss.drums");


    public static Holder.Reference<SoundEvent> DEAD_KING_SWING = registerSoundEvent("entity.dead_king.attack_swing");
    public static Holder.Reference<SoundEvent> DEAD_KING_SLAM = registerSoundEvent("entity.dead_king.attack_slam");
    public static Holder.Reference<SoundEvent> DEAD_KING_HIT = registerSoundEvent("entity.dead_king.attack_hit");
    public static Holder.Reference<SoundEvent> DEAD_KING_RESURRECT = registerSoundEvent("entity.dead_king.resurrect");
    public static Holder.Reference<SoundEvent> DEAD_KING_SPAWN = registerSoundEvent("entity.dead_king.spawn");
    public static Holder.Reference<SoundEvent> DEAD_KING_FAKE_DEATH = registerSoundEvent("entity.dead_king.fake_death");
    public static Holder.Reference<SoundEvent> DEAD_KING_DEATH = registerSoundEvent("entity.dead_king.death");
    public static Holder.Reference<SoundEvent> DEAD_KING_HURT = registerSoundEvent("entity.dead_king.hurt");
    public static Holder.Reference<SoundEvent> DEAD_KING_EXPLODE = registerSoundEvent("entity.dead_king.explode");
    public static Holder.Reference<SoundEvent> DEAD_KING_DRUM_LOOP = registerSoundEvent("entity.dead_king.music.drum_loop");
    public static Holder.Reference<SoundEvent> DEAD_KING_AMBIENCE = registerSoundEvent("entity.dead_king.ambience");
    public static Holder.Reference<SoundEvent> DEAD_KING_MUSIC_INTRO = registerSoundEvent("entity.dead_king.music.intro");
    public static Holder.Reference<SoundEvent> DEAD_KING_FIRST_PHASE_MELODY = registerSoundEvent("entity.dead_king.music.first_phase_melody");
    public static Holder.Reference<SoundEvent> DEAD_KING_FIRST_PHASE_ACCENT_01 = registerSoundEvent("entity.dead_king.music.first_phase_accent_01");
    public static Holder.Reference<SoundEvent> DEAD_KING_SECOND_PHASE_MELODY_ALT = registerSoundEvent("entity.dead_king.music.second_phase_melody_alt");
    public static Holder.Reference<SoundEvent> DEAD_KING_SUSPENSE = registerSoundEvent("entity.dead_king.music.suspense");

    public static Holder.Reference<SoundEvent> FIRE_CAST = registerSoundEvent("cast.generic.fire");
    public static Holder.Reference<SoundEvent> ICE_CAST = registerSoundEvent("cast.generic.ice");
    public static Holder.Reference<SoundEvent> LIGHTNING_CAST = registerSoundEvent("cast.generic.lightning");
    public static Holder.Reference<SoundEvent> HOLY_CAST = registerSoundEvent("cast.generic.holy");
    public static Holder.Reference<SoundEvent> ENDER_CAST = registerSoundEvent("cast.generic.ender");
    public static Holder.Reference<SoundEvent> BLOOD_CAST = registerSoundEvent("cast.generic.blood");
    public static Holder.Reference<SoundEvent> EVOCATION_CAST = registerSoundEvent("cast.generic.evocation");
    public static Holder.Reference<SoundEvent> NATURE_CAST = registerSoundEvent("cast.generic.nature");
    public static Holder.Reference<SoundEvent> POISON_CAST = registerSoundEvent("cast.generic.poison");

    public static Holder.Reference<SoundEvent> LEARN_ELDRITCH_SPELL = registerSoundEvent("ui.learn_eldritch_spell");
    public static Holder.Reference<SoundEvent> UI_TICK = registerSoundEvent("ui.tick");
    public static Holder.Reference<SoundEvent> EQUIP_SPELL_BOOK = registerSoundEvent("item.spell_book.equip");

    public static Holder.Reference<SoundEvent> MUSIC_DISC_DEAD_KING_LULLABY = registerSoundEvent("music.dead_king_lullaby");
    public static Holder.Reference<SoundEvent> MUSIC_DISC_FLAME_STILL_BURNS = registerSoundEvent("music.flame_still_burns");
    public static Holder.Reference<SoundEvent> MUSIC_DISC_WHISPERS_OF_ICE = registerSoundEvent("music.whispers_of_ice");


    private static Holder.Reference<SoundEvent> registerSoundEvent(String name) {
        return Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, IronsSpellbooks.id(name), SoundEvent.createVariableRangeEvent(IronsSpellbooks.id(name)));
    }
}
