package io.redspace.ironslib.kinetic_weapon.client;

import io.redspace.ironslib.IronsLib;
import io.redspace.ironslib.kinetic_weapon.KineticAnimation;
import io.redspace.ironslib.registry.IronsLibRegistries;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class KineticAnimations {
   private static final Map<Identifier, KineticAnimation> ANIMATIONS = new HashMap<>();
   private static final Set<Identifier> WARNED_MISSING = new HashSet<>();

   private KineticAnimations() {
   }

   public static void register(Identifier id, KineticAnimation animation) {
      KineticAnimation previous = ANIMATIONS.put(id, animation);
      if (previous != null) {
         IronsLib.LOGGER.warn("Kinetic animation {} registered twice; keeping the latest", id);
      }
   }

   @Nullable
   public static KineticAnimation get(Identifier id) {
      KineticAnimation animation = ANIMATIONS.get(id);
      if (animation == null && WARNED_MISSING.add(id)) {
         IronsLib.LOGGER.warn("No kinetic animation registered as {}; using the spear", id);
      }

      return animation;
   }

   @Nullable
   public static KineticAnimation get(ItemStack stack) {
      Identifier id = (Identifier)stack.get(IronsLibRegistries.ComponentRegistry.KINETIC_ANIMATION);
      return id == null ? null : get(id);
   }
}
