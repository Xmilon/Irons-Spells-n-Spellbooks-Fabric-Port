package io.redspace.ironsspellbooks.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SoulfireRayParticleOptions implements ParticleOptions {
    public static StreamCodec<? super ByteBuf, SoulfireRayParticleOptions> STREAM_CODEC = StreamCodec.of(
            (buf, option) -> {
                buf.writeDouble(option.destination.x);
                buf.writeDouble(option.destination.y);
                buf.writeDouble(option.destination.z);
            },
            (buf) -> new SoulfireRayParticleOptions(buf.readDouble() , buf.readDouble(), buf.readDouble())
    );

    public static MapCodec<SoulfireRayParticleOptions> MAP_CODEC = RecordCodecBuilder.mapCodec(object ->
            object.group(
                    Codec.INT.listOf().validate(values -> values.size() == 3
                            ? DataResult.success(values) : DataResult.error(() -> "Destination must contain exactly three coordinates"))
                            .fieldOf("destination").forGetter((option) -> List.of(
                                    (int) Math.round(option.destination.x * 10),
                                    (int) Math.round(option.destination.y * 10),
                                    (int) Math.round(option.destination.z * 10)))
            ).apply(object, values -> new SoulfireRayParticleOptions(new Vec3(
                    values.get(0) / 10.0, values.get(1) / 10.0, values.get(2) / 10.0))));

    private final Vec3 destination;

    public SoulfireRayParticleOptions(Vec3 destination) {
        this.destination = destination;
    }

    public SoulfireRayParticleOptions(double x, double y, double z) {
        this(new Vec3(x, y, z));
    }

    public ParticleType<SoulfireRayParticleOptions> getType() {
        return ParticleRegistry.SOULFIRE_RAY_PARTICLE;
    }

    public Vec3 getDestination() {
        return this.destination;
    }

}
