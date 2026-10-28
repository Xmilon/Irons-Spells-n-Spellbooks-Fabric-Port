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

public class ZapParticleOption implements ParticleOptions {
    // Keep the existing three-int wire format, quantized to tenths of a block.
    public static StreamCodec<? super ByteBuf, ZapParticleOption> STREAM_CODEC = StreamCodec.of(
            (buf, option) -> {
                buf.writeInt((int) Math.round(option.destination.x * 10));
                buf.writeInt((int) Math.round(option.destination.y * 10));
                buf.writeInt((int) Math.round(option.destination.z * 10));
            },
            (buf) -> {
                return new ZapParticleOption(new Vec3(buf.readInt() / 10.0, buf.readInt() / 10.0, buf.readInt() / 10.0));
            }
    );

    public static MapCodec<ZapParticleOption> MAP_CODEC = RecordCodecBuilder.mapCodec(object ->
            object.group(
                    Codec.INT.listOf().validate(values -> values.size() == 3
                            ? DataResult.success(values) : DataResult.error(() -> "Destination must contain exactly three coordinates"))
                            .fieldOf("destination").forGetter((option) -> List.of(
                                    (int) Math.round(option.destination.x * 10),
                                    (int) Math.round(option.destination.y * 10),
                                    (int) Math.round(option.destination.z * 10)))
            ).apply(object, values -> new ZapParticleOption(new Vec3(
                    values.get(0) / 10.0, values.get(1) / 10.0, values.get(2) / 10.0))));

    private final Vec3 destination;

    public ZapParticleOption(Vec3 destination) {
        this.destination = destination;
    }

    public ZapParticleOption(float x, float y, float z) {
        this(new Vec3(x, y, z));
    }

    public ParticleType<ZapParticleOption> getType() {
        return ParticleRegistry.ZAP_PARTICLE;
    }

    public Vec3 getDestination() {
        return this.destination;
    }

}
