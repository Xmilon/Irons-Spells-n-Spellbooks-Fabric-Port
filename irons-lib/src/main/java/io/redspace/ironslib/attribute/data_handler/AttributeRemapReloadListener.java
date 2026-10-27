package io.redspace.ironslib.attribute.data_handler;

import io.redspace.ironslib.attribute.AttributeRemapRegistry;
import io.redspace.ironslib.attribute.network.SyncAttributeRemapsPacket;
import java.util.List;
import java.util.Map;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jspecify.annotations.NonNull;

public class AttributeRemapReloadListener extends SimpleJsonResourceReloadListener<AttributeRemapEntry> {
   public AttributeRemapReloadListener() {
      super(AttributeRemapEntry.CODEC, FileToIdConverter.json("irons_lib_attribute_remap"));
   }

   protected void apply(Map<Identifier, AttributeRemapEntry> resourceList, @NonNull ResourceManager resourceManagerIn, @NonNull ProfilerFiller profilerIn) {
      AttributeRemapRegistry.handleDatapackSync(List.copyOf(resourceList.values()));
      // The Fabric datapack synchronization event sends the rebuilt map after reload completes.
   }
}
