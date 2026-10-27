package io.redspace.ironslib.internal.network.packet;

import io.redspace.ironslib.statue.PlayerStatuePose;
import io.redspace.ironslib.statue.block.statue_block.player.PlayerStatueBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class SelectStatuePosePacket implements CustomPacketPayload {
   private final BlockPos blockPos;
   private final PlayerStatuePose pose;
   private final boolean flipped;
   public static final Type<SelectStatuePosePacket> TYPE = new Type(Identifier.fromNamespaceAndPath("irons_lib", "select_player_statue_pose"));
   public static final StreamCodec<RegistryFriendlyByteBuf, SelectStatuePosePacket> STREAM_CODEC = CustomPacketPayload.codec(
      SelectStatuePosePacket::write, SelectStatuePosePacket::new
   );

   public SelectStatuePosePacket(BlockPos blockPos, PlayerStatuePose pose, boolean flipped) {
      this.blockPos = blockPos;
      this.pose = pose;
      this.flipped = flipped;
   }

   public SelectStatuePosePacket(FriendlyByteBuf buf) {
      this.blockPos = buf.readBlockPos();
      this.pose = PlayerStatuePose.fromString(buf.readUtf());
      this.flipped = buf.readBoolean();
   }

   public void write(FriendlyByteBuf buf) {
      buf.writeBlockPos(this.blockPos);
      buf.writeUtf(this.pose.getSerializedName());
      buf.writeBoolean(this.flipped);
   }

   public static void handle(SelectStatuePosePacket packet, ServerPlayNetworking.Context context) {
      var player = context.player();
      var level = player.level();
      if (packet.pose == null || !level.hasChunkAt(packet.blockPos)
            || player.isSpectator() || !player.mayInteract(level, packet.blockPos)
            || !player.isWithinBlockInteractionRange(packet.blockPos, 1.0)
            || !level.mayInteract(player, packet.blockPos)) {
         return;
      }
      if (level.getBlockEntity(packet.blockPos) instanceof PlayerStatueBlockEntity statueBlock) {
         statueBlock.getPrimaryControllerOpt().ifPresent(controller -> {
            controller.setPose(packet.pose);
            controller.setFlipped(packet.flipped);
         });
      }
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
