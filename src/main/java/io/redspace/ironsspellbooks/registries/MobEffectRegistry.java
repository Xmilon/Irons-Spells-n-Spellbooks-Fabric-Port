package io.redspace.ironsspellbooks.registries;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.effect.SacrificialMarkEffect;
import io.redspace.ironsspellbooks.effect.SoulBurnMobEffect;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.effect.*;
import io.redspace.ironsspellbooks.effect.guiding_bolt.GuidingBoltEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class MobEffectRegistry {
    private static Holder.Reference<MobEffect> register(String path, java.util.function.Supplier<MobEffect> factory) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, IronsSpellbooks.id(path), factory.get());
    }

    public static void register() {
        // Class initialization registers native references before registry freeze.
    }

    public static final Holder.Reference<MobEffect> ANGEL_WINGS = register("angel_wings", () -> new AngelWingsEffect(MobEffectCategory.BENEFICIAL, 0xbea925));
    public static final Holder.Reference<MobEffect> EVASION = register("evasion", () -> new EvasionEffect(MobEffectCategory.BENEFICIAL, 0xf17bf4));
    public static final Holder.Reference<MobEffect> HEARTSTOP = register("heartstop", () -> new HeartstopEffect(MobEffectCategory.BENEFICIAL, 4393481));
    public static final Holder.Reference<MobEffect> ABYSSAL_SHROUD = register("abyssal_shroud", () -> new AbyssalShroudEffect(MobEffectCategory.BENEFICIAL, 0));
    public static final Holder.Reference<MobEffect> ASCENSION = register("ascension", () -> new AscensionEffect(MobEffectCategory.BENEFICIAL, 0xbea925)
            .addAttributeModifier(Attributes.GRAVITY, IronsSpellbooks.id("mobeffect_ascension"), -.85f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final Holder.Reference<MobEffect> CHARGED = register("charged", () -> new ChargeEffect(MobEffectCategory.BENEFICIAL, 3311322)
            .addAttributeModifier(Attributes.ATTACK_DAMAGE, IronsSpellbooks.id("mobeffect_charged"), ChargeEffect.ATTACK_DAMAGE_PER_LEVEL, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, IronsSpellbooks.id("mobeffect_charged"), ChargeEffect.SPEED_PER_LEVEL, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(AttributeRegistry.SPELL_POWER, IronsSpellbooks.id("mobeffect_charged"), ChargeEffect.SPELL_POWER_PER_LEVEL, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final Holder.Reference<MobEffect> TRUE_INVISIBILITY = register("true_invisibility", () -> new TrueInvisibilityEffect(MobEffectCategory.BENEFICIAL, 8356754));
    public static final Holder.Reference<MobEffect> FORTIFY = register("fortify", () -> new FortifyEffect(MobEffectCategory.BENEFICIAL, 16239960)
            .addAttributeModifier(Attributes.MAX_ABSORPTION, IronsSpellbooks.id("mobeffect_fortify"), 1, AttributeModifier.Operation.ADD_VALUE));
    public static final Holder.Reference<MobEffect> REND = register("rend", () -> new RendEffect(MobEffectCategory.HARMFUL, 4800826)
            .addAttributeModifier(Attributes.ARMOR, IronsSpellbooks.id("mobeffect_rend"), RendEffect.ARMOR_PER_LEVEL, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final Holder.Reference<MobEffect> SPIDER_ASPECT = register("spider_aspect", () -> new SpiderAspectEffect(MobEffectCategory.BENEFICIAL, 4800826));
    public static final Holder.Reference<MobEffect> BLIGHT = register("blight", () -> new BlightEffect(MobEffectCategory.HARMFUL, 0xdfff2b));
    public static final Holder.Reference<MobEffect> GUIDING_BOLT = register("guided", () -> new GuidingBoltEffect(MobEffectCategory.HARMFUL, 16239960));
    public static final Holder.Reference<MobEffect> AIRBORNE = register("airborne", () -> new AirborneEffect(MobEffectCategory.HARMFUL, 0xFFFFFF));
    public static final Holder.Reference<MobEffect> VIGOR = register("vigor", () -> new MagicMobEffect(MobEffectCategory.BENEFICIAL, 0x850d0d)
            .addAttributeModifier(Attributes.MAX_HEALTH, IronsSpellbooks.id("mobeffect_vigor"), 2, AttributeModifier.Operation.ADD_VALUE));
    public static final Holder.Reference<MobEffect> INSTANT_MANA = register("instant_mana", () -> new InstantManaEffect(MobEffectCategory.BENEFICIAL, 0x00b7ec));
    public static final Holder.Reference<MobEffect> OAKSKIN = register("oakskin", () -> new OakskinEffect(MobEffectCategory.BENEFICIAL, 0xffef95)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, IronsSpellbooks.id("mobeffect_oakskin"), -OakskinEffect.SLOWNESS_MAGNITUDE, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final Holder.Reference<MobEffect> SOUL_BURN = register("soul_burn", () -> new SoulBurnMobEffect(MobEffectCategory.HARMFUL, 0x614ab7));
    public static final Holder.Reference<MobEffect> SACRIFICIAL_MARK = register("sacrificial_mark", SacrificialMarkEffect::new);
    public static final Holder.Reference<MobEffect> PLANAR_SIGHT = register("planar_sight", () -> new PlanarSightEffect(MobEffectCategory.BENEFICIAL, 0x6c42f5));
    public static final Holder.Reference<MobEffect> ANTIGRAVITY = register("antigravity", () -> new MagicMobEffect(MobEffectCategory.NEUTRAL, 0x6c42f5)
            .addAttributeModifier(Attributes.GRAVITY, IronsSpellbooks.id("mobeffect_antigravity"), -1.02, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    public static final Holder.Reference<MobEffect> HASTENED = register("hastened", () -> new MagicMobEffect(MobEffectCategory.BENEFICIAL, 0xD9C043)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, IronsSpellbooks.id("mobeffect_haste"), .10, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.ATTACK_SPEED, IronsSpellbooks.id("mobeffect_haste"), .10, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(AttributeRegistry.MANA_REGEN, IronsSpellbooks.id("mobeffect_haste"), .10, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(AttributeRegistry.CAST_TIME_REDUCTION, IronsSpellbooks.id("mobeffect_haste"), .10, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final Holder.Reference<MobEffect> SLOWED = register("slowed", () -> new MagicMobEffect(MobEffectCategory.HARMFUL, 0x5A6C81)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, IronsSpellbooks.id("mobeffect_slow"), -.10, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.ATTACK_SPEED, IronsSpellbooks.id("mobeffect_slow"), -.10, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(AttributeRegistry.MANA_REGEN, IronsSpellbooks.id("mobeffect_slow"), -.10, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(AttributeRegistry.CAST_TIME_REDUCTION, IronsSpellbooks.id("mobeffect_slow"), -.10, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final Holder.Reference<MobEffect> CHILLED = register("chilled", () -> new ChilledEffect(MobEffectCategory.HARMFUL, 0xd0f9ff)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, IronsSpellbooks.id("mobeffect_chilled"), -.20, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final Holder.Reference<MobEffect> BURNING_DASH = register("burning_dash", () -> new BurningDashEffect(MobEffectCategory.BENEFICIAL, 0xd0f9ff));
    public static final Holder.Reference<MobEffect> VOLT_STRIKE = register("volt_strike", () -> new VoltStrikeEffect(MobEffectCategory.BENEFICIAL, 0xd0088FF));
    public static final Holder.Reference<MobEffect> GLUTTONY = register("gluttony", () -> new GluttonyEffect(MobEffectCategory.BENEFICIAL, 0xd0f9ff));
    public static final Holder.Reference<MobEffect> ECHOING_STRIKES = register("echoing_strikes", () -> new EchoingStrikesEffect(MobEffectCategory.BENEFICIAL, 0x9f0be3));
    public static final Holder.Reference<MobEffect> THUNDERSTORM = register("thunderstorm", () -> new ThunderstormEffect(MobEffectCategory.BENEFICIAL, 0x9f0be3));
    public static final Holder.Reference<MobEffect> FROSTBITTEN_STRIKES = register("frostbite", () -> new FrostbiteEffect(MobEffectCategory.BENEFICIAL, 0xFFFFFF));
    public static final Holder.Reference<MobEffect> IMMOLATE = register("immolate", () -> new ImmolateEffect(MobEffectCategory.HARMFUL, 0xFFAA00));
    public static final Holder.Reference<MobEffect> FALL_DAMAGE_IMMUNITY = register("fall_damage_immunity", () -> new FallDamageImmunityEffect(MobEffectCategory.BENEFICIAL, 0xDDDDFF));
    //todo: reimplement ice spider lure mechanics
//    public static final Holder.Reference<MobEffect> ICE_SPIDER_LURE = register("ice_spider_lure", () -> new IceSpiderLureEffect(MobEffectCategory.HARMFUL, 0xffef95));


    @Deprecated(forRemoval = true)
    public static final Holder.Reference<MobEffect> VEX_TIMER = register("vex_timer", () -> new SummonTimer(MobEffectCategory.BENEFICIAL, 0xbea925));
    @Deprecated(forRemoval = true)
    public static final Holder.Reference<MobEffect> POLAR_BEAR_TIMER = register("polar_bear_timer", () -> new SummonTimer(MobEffectCategory.BENEFICIAL, 0xbea925));
    @Deprecated(forRemoval = true)
    public static final Holder.Reference<MobEffect> SUMMONED_SWORD_TIMER = register("summon_swords_timer", () -> new SummonTimer(MobEffectCategory.BENEFICIAL, 0xbea925));
    @Deprecated(forRemoval = true)
    public static final Holder.Reference<MobEffect> RAISE_DEAD_TIMER = register("raise_dead_timer", () -> new SummonTimer(MobEffectCategory.BENEFICIAL, 0xbea925));
    @Deprecated(forRemoval = true)
    public static final Holder.Reference<MobEffect> SUMMON_HORSE_TIMER = register("summon_horse_timer", () -> new SummonTimer(MobEffectCategory.BENEFICIAL, 0xbea925));
}
