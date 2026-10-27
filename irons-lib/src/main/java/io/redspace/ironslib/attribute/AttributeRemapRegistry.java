package io.redspace.ironslib.attribute;

import io.redspace.ironslib.IronsLib;
import io.redspace.ironslib.attribute.data_handler.AttributeRemapEntry;
import io.redspace.ironslib.attribute.network.SyncAttributeRemapsPacket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.fabricmc.loader.api.FabricLoader;

public final class AttributeRemapRegistry {
   private static final List<AttributeRemapEntry> REGISTERED = new ArrayList<>();
   private static final List<AttributeRemapEntry> DATAPACK = new ArrayList<>();
   private static final Map<Holder<Attribute>, Holder<Attribute>> REMAPS = new HashMap<>();

   public static void register(Holder<Attribute> from, Holder<Attribute> to) {
      from.unwrapKey().ifPresent(fromKey -> to.unwrapKey().ifPresent(toKey -> register(fromKey.identifier(), toKey.identifier())));
   }

   public static void register(Identifier from, Identifier to) {
      REGISTERED.removeIf(entry -> entry.from().equals(from));
      REGISTERED.add(new AttributeRemapEntry(from, to));
      rebuild();
   }

   public static void register(Identifier from, Holder<Attribute> to) {
      to.unwrapKey().ifPresent(toKey -> register(from, toKey.identifier()));
   }

   public static void register(Holder<Attribute> from, Identifier to) {
      from.unwrapKey().ifPresent(fromKey -> register(fromKey.identifier(), to));
   }

   public static void handleDatapackSync(List<AttributeRemapEntry> entries) {
      DATAPACK.clear();
      DATAPACK.addAll(entries);
      rebuild();
   }

   public static SyncAttributeRemapsPacket buildSyncPacket() {
      List<AttributeRemapEntry> entries = REMAPS.entrySet()
         .stream()
         .map(
            entry -> new AttributeRemapEntry(
               ((ResourceKey)entry.getKey().unwrapKey().orElseThrow()).identifier(), ((ResourceKey)entry.getValue().unwrapKey().orElseThrow()).identifier()
            )
         )
         .toList();
      return new SyncAttributeRemapsPacket(entries);
   }

   public static Optional<Holder<Attribute>> findTarget(Holder<Attribute> source) {
      return Optional.ofNullable(REMAPS.get(source));
   }

   private static void rebuild() {
      REMAPS.clear();
      applyEntries(REGISTERED);
      applyEntries(DATAPACK);
   }

   private static void applyEntries(List<AttributeRemapEntry> entries) {
      for (AttributeRemapEntry entry : entries) {
         if (entry.from().equals(entry.to())) {
            IronsLib.LOGGER.warn("[Attribute Remapper] Skipping self-remap for {}", entry.from());
         } else if (FabricLoader.getInstance().isModLoaded(entry.from().getNamespace()) && FabricLoader.getInstance().isModLoaded(entry.to().getNamespace())) {
            Optional<Reference<Attribute>> from = BuiltInRegistries.ATTRIBUTE.get(entry.from());
            Optional<Reference<Attribute>> to = BuiltInRegistries.ATTRIBUTE.get(entry.to());
            if (!from.isEmpty() && !to.isEmpty()) {
               REMAPS.put((Holder<Attribute>)from.get(), (Holder<Attribute>)to.get());
               relax();
            } else {
               IronsLib.LOGGER.warn("[Attribute Remapper] Unknown attribute remap {} -> {} (missing registry entry)", entry.from(), entry.to());
            }
         }
      }
   }

   private static void relax() {
      if (!REMAPS.isEmpty()) {
         HashSet<Entry<Holder<Attribute>, Holder<Attribute>>> toCollapse = new HashSet<>();

         for (Entry<Holder<Attribute>, Holder<Attribute>> entry : REMAPS.entrySet()) {
            if (REMAPS.containsKey(entry.getValue())) {
               toCollapse.add(entry);
            }
         }

         for (Entry<Holder<Attribute>, Holder<Attribute>> entry : toCollapse) {
            Holder<Attribute> relaxed = REMAPS.remove(entry.getValue());
            if (relaxed != null) {
               REMAPS.put(entry.getKey(), relaxed);
            }
         }
      }
   }
}
