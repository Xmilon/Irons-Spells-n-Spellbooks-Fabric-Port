package io.redspace.ironslib.internal.network.packet;

import io.redspace.ironslib.internal.client.ClientHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class OpenStatuePoseScreenPacket implements CustomPacketPayload {
   private final BlockPos blockPos;
   public static final Type<OpenStatuePoseScreenPacket> TYPE = new Type(Identifier.fromNamespaceAndPath("irons_lib", "open_player_statue_pose_screen"));
   public static final StreamCodec<RegistryFriendlyByteBuf, OpenStatuePoseScreenPacket> STREAM_CODEC = CustomPacketPayload.codec(
      OpenStatuePoseScreenPacket::write, OpenStatuePoseScreenPacket::new
   );

   public OpenStatuePoseScreenPacket(BlockPos blockPos) {
      this.blockPos = blockPos;
   }

   public OpenStatuePoseScreenPacket(FriendlyByteBuf buf) {
      this.blockPos = buf.readBlockPos();
   }

   public void write(FriendlyByteBuf buf) {
      buf.writeBlockPos(this.blockPos);
   }

   public static void handle(OpenStatuePoseScreenPacket packet, ClientPlayNetworking.Context context) {
      context.client().execute(() -> ClientHelper.openStatuePoseScreen(packet.blockPos));
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
