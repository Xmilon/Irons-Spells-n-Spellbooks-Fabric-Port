package io.redspace.ironsspellbooks.network.particles;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.player.ClientSpellCastHelper;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Objects;

public class ShockwaveParticlesPacket implements CustomPacketPayload {
    Vec3 pos;
    float radius;
    String particleName;
    public static final CustomPacketPayload.Type<ShockwaveParticlesPacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "shockwave_particles"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ShockwaveParticlesPacket> STREAM_CODEC = CustomPacketPayload.codec(ShockwaveParticlesPacket::write, ShockwaveParticlesPacket::new);

    public ShockwaveParticlesPacket(Vec3 pos, float radius, ParticleType particleType) {
        this.pos = pos;
        this.radius = radius;
        this.particleName = Objects.requireNonNull(BuiltInRegistries.PARTICLE_TYPE.getKey(particleType)).toString();
    }

    public ShockwaveParticlesPacket(FriendlyByteBuf buf) {
        this.pos = Vec3.STREAM_CODEC.decode(buf);
        this.radius = buf.readFloat();
        this.particleName = buf.readUtf();
    }

    public void write(FriendlyByteBuf buf) {
        Vec3.STREAM_CODEC.encode(buf, pos);
        buf.writeFloat(radius);
        buf.writeUtf(particleName);
    }

    public static void handle(ShockwaveParticlesPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var type = resolveParticle(packet.particleName);
            if (type != null) ClientSpellCastHelper.handleClientboundShockwaveParticle(packet.pos, packet.radius, type);
        });
    }

    static ParticleType<?> resolveParticle(String name) {
        var id = Identifier.tryParse(name);
        return id == null ? null : BuiltInRegistries.PARTICLE_TYPE.getValue(id);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
