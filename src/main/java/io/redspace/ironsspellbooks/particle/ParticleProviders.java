package io.redspace.ironsspellbooks.particle;

import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;

public final class ParticleProviders {
    public static void register() {
        ClientShieldHelper.init();
        MeshParticle.registerGroup();
        FallingBlockParticle.registerGroup();
        var factory = ParticleProviderRegistry.getInstance();
        factory.register(ParticleRegistry.BLOOD_PARTICLE, io.redspace.ironsspellbooks.particle.BloodParticle.Provider::new);
        factory.register(ParticleRegistry.WISP_PARTICLE, io.redspace.ironsspellbooks.particle.WispParticle.Provider::new);
        factory.register(ParticleRegistry.BLOOD_GROUND_PARTICLE, io.redspace.ironsspellbooks.particle.BloodGroundParticle.Provider::new);
        factory.register(ParticleRegistry.SNOWFLAKE_PARTICLE, io.redspace.ironsspellbooks.particle.SnowflakeParticle.Provider::new);
        factory.register(ParticleRegistry.ELECTRICITY_PARTICLE, io.redspace.ironsspellbooks.particle.ElectricityParticle.Provider::new);
        factory.register(ParticleRegistry.UNSTABLE_ENDER_PARTICLE, io.redspace.ironsspellbooks.particle.UnstableEnderParticle.Provider::new);
        factory.register(ParticleRegistry.DRAGON_FIRE_PARTICLE, io.redspace.ironsspellbooks.particle.DragonFireParticle.Provider::new);
        factory.register(ParticleRegistry.FIRE_PARTICLE, io.redspace.ironsspellbooks.particle.FireParticle.Provider::new);
        factory.register(ParticleRegistry.EMBER_PARTICLE, io.redspace.ironsspellbooks.particle.EmberParticle.Provider::new);
        factory.register(ParticleRegistry.SIPHON_PARTICLE, io.redspace.ironsspellbooks.particle.SiphonParticle.Provider::new);
        factory.register(ParticleRegistry.ACID_PARTICLE, io.redspace.ironsspellbooks.particle.AcidParticle.Provider::new);
        factory.register(ParticleRegistry.ACID_BUBBLE_PARTICLE, io.redspace.ironsspellbooks.particle.AcidBubbleParticle.Provider::new);
        factory.register(ParticleRegistry.SNOW_DUST, io.redspace.ironsspellbooks.particle.SnowDustParticle.Provider::new);
        factory.register(ParticleRegistry.RING_SMOKE_PARTICLE, io.redspace.ironsspellbooks.particle.RingSmokeParticle.Provider::new);
        factory.register(ParticleRegistry.FOG_PARTICLE, io.redspace.ironsspellbooks.particle.FogParticle.Provider::new);
        factory.register(ParticleRegistry.SHOCKWAVE_PARTICLE, io.redspace.ironsspellbooks.particle.ShockwaveParticle.Provider::new);
        factory.register(ParticleRegistry.ZAP_PARTICLE, io.redspace.ironsspellbooks.particle.ZapParticle.Provider::new);
        factory.register(ParticleRegistry.FIREFLY_PARTICLE, io.redspace.ironsspellbooks.particle.FireflyParticle.Provider::new);
        factory.register(ParticleRegistry.PORTAL_FRAME_PARTICLE, io.redspace.ironsspellbooks.particle.PortalFrameParticle.Provider::new);
        factory.register(ParticleRegistry.BLASTWAVE_PARTICLE, io.redspace.ironsspellbooks.particle.BlastwaveParticle.Provider::new);
        factory.register(ParticleRegistry.SPARK_PARTICLE, io.redspace.ironsspellbooks.particle.SparkParticle.Provider::new);
        factory.register(ParticleRegistry.CLEANSE_PARTICLE, io.redspace.ironsspellbooks.particle.CleanseParticle.Provider::new);
        factory.register(ParticleRegistry.FLAME_STRIKE_PARTICLE, io.redspace.ironsspellbooks.particle.FlameStrikeParticle.Provider::new);
        factory.register(ParticleRegistry.EMBEROUS_ASH_PARTICLE, io.redspace.ironsspellbooks.particle.EmberousAshParticle.Provider::new);
        factory.register(ParticleRegistry.FIERY_SMOKE_PARTICLE, io.redspace.ironsspellbooks.particle.FierySmokeParticle.Provider::new);
        factory.register(ParticleRegistry.ENDER_SLASH_PARTICLE, io.redspace.ironsspellbooks.particle.EnderSlashParticle.Provider::new);
        factory.register(ParticleRegistry.TRACE_PARTICLE, io.redspace.ironsspellbooks.particle.TraceParticle.Provider::new);
        factory.register(ParticleRegistry.FALLING_BLOCK_PARTICLE, new FallingBlockParticle.Provider());
        factory.register(ParticleRegistry.SWIRLING_PARTICLE, new SwirlingParticle.Provider());
        factory.register(ParticleRegistry.SOULFIRE_RAY_PARTICLE, SoulfireRayParticle.Provider::new);
        factory.register(ParticleRegistry.SOUL_FIRE_PARTICLE, DragonFireParticle.Provider::new);
        factory.register(ParticleRegistry.TINTED_BUBBLE_POP_PARTICLE, TintedBubblePopParticle.Provider::new);
        factory.register(ParticleRegistry.FALLING_SPARKLE_PARTICLE, FallingSparkleParticle.Provider::new);
    }

}

