package io.redspace.ironsspellbooks.network.particles;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.player.ClientSpellCastHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class FlamethrowerParticlesPacket implements CustomPacketPayload {
    private final Vec3 position;
    private final Vec3 forward;
    public static final Type<FlamethrowerParticlesPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "flamethrower_particles"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FlamethrowerParticlesPacket> STREAM_CODEC = CustomPacketPayload.codec(FlamethrowerParticlesPacket::write, FlamethrowerParticlesPacket::new);

    public FlamethrowerParticlesPacket(Vec3 position, Vec3 forward) {
        this.position = position;
        this.forward = forward;
    }

    public FlamethrowerParticlesPacket(FriendlyByteBuf buf) {
        position = Vec3.STREAM_CODEC.decode(buf);
        forward = Vec3.STREAM_CODEC.decode(buf);
    }

    public void write(FriendlyByteBuf buf) {
        Vec3.STREAM_CODEC.encode(buf, position);
        Vec3.STREAM_CODEC.encode(buf, forward);
    }

    public static void handle(FlamethrowerParticlesPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientSpellCastHelper.handleClientboundFlamethrowerParticles(packet.position, packet.forward);
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
