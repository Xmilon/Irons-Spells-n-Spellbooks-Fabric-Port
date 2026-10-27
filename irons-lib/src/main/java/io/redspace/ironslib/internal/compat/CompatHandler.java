package io.redspace.ironslib.internal.compat;

import io.redspace.ironslib.attribute.AttributeRemapRegistry;
import io.wispforest.accessories.api.attributes.AttributeModificationData;
import io.wispforest.accessories.api.events.AdjustAttributeModifierCallback;
import java.util.ArrayList;
import java.util.HashMap;
import io.redspace.ironslib.registry.IronsLibRegistries;
import java.util.Map;
import net.minecraft.resources.Identifier;
import net.fabricmc.loader.api.FabricLoader;

public class CompatHandler {

   public static void init() {
      if (FabricLoader.getInstance().isModLoaded("accessories")) {
         AdjustAttributeModifierCallback.EVENT.register((stack, slot, modifiers) -> {
            var exclusive = new ArrayList<AttributeModificationData>();
            modifiers.exclusiveAttributes().values().forEach(entries -> exclusive.addAll(entries.values()));
            for (var entry : exclusive) {
               AttributeRemapRegistry.findTarget(entry.attribute()).ifPresent(target -> {
                  modifiers.removeExclusive(entry.attribute(), entry.modifier().id());
                  modifiers.exclusiveAttributes().computeIfAbsent(target, ignored -> new HashMap<>()).put(
                     entry.modifier().id(), new AttributeModificationData(entry.slotPath(), target, entry.modifier(), entry.usedInSlotValidation()));
               });
            }
            for (var entry : new ArrayList<>(modifiers.stackedAttributes().values())) {
               AttributeRemapRegistry.findTarget(entry.attribute()).ifPresent(target -> {
                  modifiers.stackedAttributes().remove(entry.attribute(), entry);
                  modifiers.stackedAttributes().put(target,
                     new AttributeModificationData(entry.slotPath(), target, entry.modifier(), entry.usedInSlotValidation()));
               });
            }
         });
      }
      AttributeRemapRegistry.register(IronsLibRegistries.AttributeRegistry.ARMOR_PIERCE, Identifier.parse("apothic_attributes:armor_pierce"));
      AttributeRemapRegistry.register(IronsLibRegistries.AttributeRegistry.EXPERIENCE_GAINED, Identifier.parse("apothic_attributes:experience_gained"));
      AttributeRemapRegistry.register(IronsLibRegistries.AttributeRegistry.ARROW_DAMAGE, Identifier.parse("apothic_attributes:arrow_damage"));
      AttributeRemapRegistry.register(IronsLibRegistries.AttributeRegistry.CRIT_DAMAGE, Identifier.parse("apothic_attributes:crit_damage"));
      AttributeRemapRegistry.register(IronsLibRegistries.AttributeRegistry.DODGE_CHANCE, Identifier.parse("apothic_attributes:dodge_chance"));
   }
}
