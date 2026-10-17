package io.redspace.ironsspellbooks.network;

import io.netty.buffer.Unpooled;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Fabric replacement for NeoForge's IEntityWithComplexSpawn: sent right after an entity's spawn packet
 * (see ServerEntityMixin) so the client entity can read the extra data written by writeSpawnData.
 */
public record ComplexSpawnDataPacket(int entityId, byte[] data) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ComplexSpawnDataPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(IronsSpellbooks.MODID, "complex_spawn_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ComplexSpawnDataPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ComplexSpawnDataPacket::entityId,
            ByteBufCodecs.BYTE_ARRAY, ComplexSpawnDataPacket::data,
            ComplexSpawnDataPacket::new);

    public static ComplexSpawnDataPacket create(Entity entity, net.minecraft.core.RegistryAccess registryAccess) {
        var buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), registryAccess);
        ((IEntityWithComplexSpawn) entity).writeSpawnData(buffer);
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);
        return new ComplexSpawnDataPacket(entity.getId(), bytes);
    }

    public static void handle(ComplexSpawnDataPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = Minecraft.getInstance().level;
            if (level != null && level.getEntity(packet.entityId) instanceof IEntityWithComplexSpawn spawnData) {
                spawnData.readSpawnData(new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(packet.data), level.registryAccess()));
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
