package io.redspace.ironsspellbooks.registries;

import com.mojang.serialization.MapCodec;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.particle.*;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class ParticleRegistry {
    public static void register() {
    }

    private static <T extends ParticleType<?>> T register(String name, T type) {
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, name), type);
    }

    /*
    To Create Particle:
    - textures + json
    - particle class
    - register it here
    - add it to particle helper
    - register it in client setup
     */

    public static final SimpleParticleType BLOOD_PARTICLE = register("blood", FabricParticleTypes.simple(false));
    public static final SimpleParticleType WISP_PARTICLE = register("wisp", FabricParticleTypes.simple(false));
    public static final SimpleParticleType BLOOD_GROUND_PARTICLE = register("blood_ground", FabricParticleTypes.simple(false));
    public static final SimpleParticleType SNOWFLAKE_PARTICLE = register("snowflake", FabricParticleTypes.simple(false));
    public static final SimpleParticleType ELECTRICITY_PARTICLE = register("electricity", FabricParticleTypes.simple(false));
    public static final SimpleParticleType UNSTABLE_ENDER_PARTICLE = register("unstable_ender", FabricParticleTypes.simple(false));
    public static final SimpleParticleType DRAGON_FIRE_PARTICLE = register("dragon_fire", FabricParticleTypes.simple(false));
    public static final SimpleParticleType FIRE_PARTICLE = register("fire", FabricParticleTypes.simple(false));
    public static final SimpleParticleType EMBER_PARTICLE = register("embers", FabricParticleTypes.simple(false));
    public static final SimpleParticleType SIPHON_PARTICLE = register("spell", FabricParticleTypes.simple(false));
    public static final SimpleParticleType ACID_PARTICLE = register("acid", FabricParticleTypes.simple(false));
    public static final SimpleParticleType ACID_BUBBLE_PARTICLE = register("acid_bubble", FabricParticleTypes.simple(false));
    public static final SimpleParticleType SNOW_DUST = register("snow_dust", FabricParticleTypes.simple(false));
    public static final SimpleParticleType RING_SMOKE_PARTICLE = register("ring_smoke", FabricParticleTypes.simple(false));
    public static final ParticleType<FogParticleOptions> FOG_PARTICLE = register("fog", new ParticleType<>(true) {
        public MapCodec<FogParticleOptions> codec() {
            return FogParticleOptions.MAP_CODEC;
        }

        public StreamCodec<? super RegistryFriendlyByteBuf, FogParticleOptions> streamCodec() {
            return FogParticleOptions.STREAM_CODEC;
        }
    });
    public static final ParticleType<ShockwaveParticleOptions> SHOCKWAVE_PARTICLE = register("shockwave", new ParticleType<>(false) {
        public MapCodec<ShockwaveParticleOptions> codec() {
            return ShockwaveParticleOptions.CODEC;
        }

        public StreamCodec<? super RegistryFriendlyByteBuf, ShockwaveParticleOptions> streamCodec() {
            return ShockwaveParticleOptions.STREAM_CODEC;
        }
    });
    public static final ParticleType<ZapParticleOption> ZAP_PARTICLE = register("zap", new ParticleType<>(false) {
        public MapCodec<ZapParticleOption> codec() {
            return ZapParticleOption.MAP_CODEC;
        }

        public StreamCodec<? super RegistryFriendlyByteBuf, ZapParticleOption> streamCodec() {
            return ZapParticleOption.STREAM_CODEC;
        }
    });
    public static final SimpleParticleType FIREFLY_PARTICLE = register("firefly", FabricParticleTypes.simple(false));
    public static final SimpleParticleType PORTAL_FRAME_PARTICLE = register("portal_frame", FabricParticleTypes.simple(false));
    public static final ParticleType<BlastwaveParticleOptions> BLASTWAVE_PARTICLE = register("blastwave", new ParticleType<>(true) {
        public MapCodec<BlastwaveParticleOptions> codec() {
            return BlastwaveParticleOptions.MAP_CODEC;
        }

        public StreamCodec<? super RegistryFriendlyByteBuf, BlastwaveParticleOptions> streamCodec() {
            return BlastwaveParticleOptions.STREAM_CODEC;
        }
    });
    public static final ParticleType<SparkParticleOptions> SPARK_PARTICLE = register("spark", new ParticleType<>(true) {
        public MapCodec<SparkParticleOptions> codec() {
            return SparkParticleOptions.MAP_CODEC;
        }

        public StreamCodec<? super RegistryFriendlyByteBuf, SparkParticleOptions> streamCodec() {
            return SparkParticleOptions.STREAM_CODEC;
        }
    });
    public static final SimpleParticleType CLEANSE_PARTICLE = register("cleanse", FabricParticleTypes.simple(false));
    public static final ParticleType<FlameStrikeParticleOptions> FLAME_STRIKE_PARTICLE = register("flame_strike", new ParticleType<>(true) {
        public MapCodec<FlameStrikeParticleOptions> codec() {
            return FlameStrikeParticleOptions.MAP_CODEC;
        }

        public StreamCodec<? super RegistryFriendlyByteBuf, FlameStrikeParticleOptions> streamCodec() {
            return FlameStrikeParticleOptions.STREAM_CODEC;
        }
    });
    public static final SimpleParticleType EMBEROUS_ASH_PARTICLE = register("emberous_ash", FabricParticleTypes.simple(false));
    public static final SimpleParticleType FIERY_SMOKE_PARTICLE = register("fiery_smoke", FabricParticleTypes.simple(true));
    public static final ParticleType<EnderSlashParticleOptions> ENDER_SLASH_PARTICLE = register("ender_slash", new ParticleType<>(true) {
        public MapCodec<EnderSlashParticleOptions> codec() {
            return EnderSlashParticleOptions.MAP_CODEC;
        }

        public StreamCodec<? super RegistryFriendlyByteBuf, EnderSlashParticleOptions> streamCodec() {
            return EnderSlashParticleOptions.STREAM_CODEC;
        }
    });

    public static final ParticleType<TraceParticleOptions> TRACE_PARTICLE = register("trace", new ParticleType<>(true) {
        public MapCodec<TraceParticleOptions> codec() {
            return TraceParticleOptions.MAP_CODEC;
        }

        public StreamCodec<? super RegistryFriendlyByteBuf, TraceParticleOptions> streamCodec() {
            return TraceParticleOptions.STREAM_CODEC;
        }
    });

    public static final ParticleType<FallingBlockParticleOption> FALLING_BLOCK_PARTICLE = register("falling_block", new ParticleType<>(true) {
        public MapCodec<FallingBlockParticleOption> codec() {
            return FallingBlockParticleOption.codec((ParticleType<FallingBlockParticleOption>) this);
        }

        public StreamCodec<? super RegistryFriendlyByteBuf, FallingBlockParticleOption> streamCodec() {
            return FallingBlockParticleOption.streamCodec((ParticleType<FallingBlockParticleOption>) this);
        }
    });

    public static final ParticleType<SwirlingParticleOptions> SWIRLING_PARTICLE = register("swirling_particle", new ParticleType<>(false) {
        public MapCodec<SwirlingParticleOptions> codec() {
            return SwirlingParticleOptions.MAP_CODEC;
        }
        public StreamCodec<? super RegistryFriendlyByteBuf, SwirlingParticleOptions> streamCodec() {
            return SwirlingParticleOptions.STREAM_CODEC;
        }
    });

    public static final SimpleParticleType FALLING_SPARKLE_PARTICLE = register("falling_sparkle", FabricParticleTypes.simple(true));

    public static final ParticleType<SoulfireRayParticleOptions> SOULFIRE_RAY_PARTICLE = register("soulfire_ray", new ParticleType<>(false) {
        public MapCodec<SoulfireRayParticleOptions> codec() {
            return SoulfireRayParticleOptions.MAP_CODEC;
        }

        public StreamCodec<? super RegistryFriendlyByteBuf, SoulfireRayParticleOptions> streamCodec() {
            return SoulfireRayParticleOptions.STREAM_CODEC;
        }
    });

    public static final SimpleParticleType SOUL_FIRE_PARTICLE = register("soul_fire", FabricParticleTypes.simple(false));

    public static final ParticleType<TintedBubblePopParticleOptions> TINTED_BUBBLE_POP_PARTICLE = register("tinted_bubble_pop", new ParticleType<>(false) {
        public MapCodec<TintedBubblePopParticleOptions> codec() {
            return TintedBubblePopParticleOptions.MAP_CODEC;
        }

        public StreamCodec<? super RegistryFriendlyByteBuf, TintedBubblePopParticleOptions> streamCodec() {
            return TintedBubblePopParticleOptions.STREAM_CODEC;
        }
    });
}
