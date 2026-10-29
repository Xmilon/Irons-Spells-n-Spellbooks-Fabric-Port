package io.redspace.ironsspellbooks.network.particles;

import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;


public final class ParticlePacketCodecCheck {
    private record Fixture(CustomPacketPayload packet, List<Vec3> vectors, Consumer<FriendlyByteBuf> tail) { }

    public static void main(String[] args) throws ReflectiveOperationException {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
        var a = new Vec3(-12.125, 63.875, 30_000_000.0625);
        var b = new Vec3(.025, -.125, 4.5);
        Consumer<FriendlyByteBuf> none = buffer -> { };
        var one = List.of(a);
        var two = List.of(a, b);
        var fixtures = List.of(
                new Fixture(new AbsorptionParticlesPacket(a), one, none),
                new Fixture(new FortifyAreaParticlesPacket(a), one, none),
                new Fixture(new HealParticlesPacket(a), one, none),
                new Fixture(new OakskinParticlesPacket(a), one, none),
                new Fixture(new RegenCloudParticlesPacket(a), one, none),
                new Fixture(new BloodSiphonParticlesPacket(a, b), two, none),
                new Fixture(new FlamethrowerParticlesPacket(a, b), two, none),
                new Fixture(new FrostStepParticlesPacket(a, b), two, none),
                new Fixture(new TeleportParticlesPacket(a, b), two, none),
                new Fixture(new FieryExplosionParticlesPacket(a, 3.25f), one, buffer -> buffer.writeFloat(3.25f)),
                new Fixture(new ShockwaveParticlesPacket(a, 3.25f, ParticleTypes.FLAME), one,
                        buffer -> { buffer.writeFloat(3.25f); buffer.writeUtf("minecraft:flame"); })
        );
        var registries = RegistryAccess.EMPTY;
        for (var fixture : fixtures) {
            @SuppressWarnings("unchecked")
            var codec = (StreamCodec<RegistryFriendlyByteBuf, CustomPacketPayload>) fixture.packet.getClass().getField("STREAM_CODEC").get(null);
            var encoded = new RegistryFriendlyByteBuf(Unpooled.buffer(), registries);
            var expected = new FriendlyByteBuf(Unpooled.buffer());
            var roundTrip = new RegistryFriendlyByteBuf(Unpooled.buffer(), registries);
            var truncated = new RegistryFriendlyByteBuf(Unpooled.buffer(7).writeZero(7), registries);
            try {
                codec.encode(encoded, fixture.packet);
                for (var vector : fixture.vectors) expected.writeDouble(vector.x).writeDouble(vector.y).writeDouble(vector.z);
                fixture.tail.accept(expected);
                var bytes = new byte[encoded.readableBytes()];
                encoded.getBytes(0, bytes);
                var legacy = new byte[expected.readableBytes()];
                expected.getBytes(0, legacy);
                assert Arrays.equals(bytes, legacy) : "Wire layout changed: " + fixture.packet.type().id();
                var decoded = codec.decode(encoded);
                assert !encoded.isReadable() : "Unconsumed payload bytes";
                assert decoded.type().equals(fixture.packet.type()) : "Payload ID changed";
                codec.encode(roundTrip, decoded);
                var reencoded = new byte[roundTrip.readableBytes()];
                roundTrip.getBytes(0, reencoded);
                assert Arrays.equals(bytes, reencoded) : "Fractional vector/fields did not round-trip";
                try {
                    codec.decode(truncated);
                    throw new AssertionError("Truncated vector accepted");
                } catch (IndexOutOfBoundsException expectedFailure) { }
            } finally {
                encoded.release(); expected.release(); roundTrip.release(); truncated.release();
            }
        }
        assert ShockwaveParticlesPacket.resolveParticle("minecraft:flame") == ParticleTypes.FLAME : "Vanilla namespace lost";
        assert ShockwaveParticlesPacket.resolveParticle("missing:particle") == null : "Unknown particle accepted";
        assert ShockwaveParticlesPacket.resolveParticle("bad::particle") == null : "Malformed particle ID accepted";
        System.out.println("11 particle payload codecs: exact wire bytes, fractional vectors, field order, IDs, truncation and shockwave lookup passed");
    }
}
