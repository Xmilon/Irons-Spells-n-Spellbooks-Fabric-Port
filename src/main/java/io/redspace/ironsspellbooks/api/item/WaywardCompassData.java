package io.redspace.ironsspellbooks.api.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record WaywardCompassData(BlockPos blockPos) {
    public net.minecraft.world.item.component.LodestoneTracker lodestoneTracker() {
        return new net.minecraft.world.item.component.LodestoneTracker(
                java.util.Optional.of(net.minecraft.core.GlobalPos.of(net.minecraft.world.level.Level.OVERWORLD, blockPos)), false);
    }

    public static final Codec<WaywardCompassData> CODEC = RecordCodecBuilder.create(builder -> builder.group(BlockPos.CODEC.fieldOf("catacombs_pos").forGetter(WaywardCompassData::blockPos)).apply(builder, WaywardCompassData::new));
    public static final StreamCodec<FriendlyByteBuf, WaywardCompassData> STREAM_CODEC = StreamCodec.of((buf, data) -> buf.writeBlockPos(data.blockPos), (buf) -> new WaywardCompassData(buf.readBlockPos()));

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof WaywardCompassData waywardCompassData && waywardCompassData.blockPos.equals(this.blockPos);
    }

    @Override
    public int hashCode() {
        return this.blockPos.hashCode();
    }
}
