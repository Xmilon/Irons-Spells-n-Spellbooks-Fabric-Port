package io.redspace.ironslib.transmog.client;

import io.redspace.ironslib.IronsLib;
import io.redspace.ironslib.patreon.PatreonData;
import io.redspace.ironslib.patreon.data.PatreonPermissions;
import io.redspace.ironslib.transmog.TransmogHolder;
import io.redspace.ironslib.transmog.TransmogItemData;
import io.redspace.ironslib.util.MemoizedSupplier;
import java.util.HashMap;
import java.util.Objects;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class TransmogClientHandler {
   private static final HashMap<Identifier, MemoizedSupplier<TransmogArmorRenderer<?, ?>>> TRANSMOGS = new HashMap<>();

   public static TransmogArmorRenderer<?, ?> renderer(TransmogHolder transmogHolder) {
      return Objects.requireNonNull(TRANSMOGS.get(transmogHolder.id()), "No Renderer Registered for transmog: " + transmogHolder.id()).get();
   }

   public static boolean shouldDisableOuterLayer(ItemStack stack, EquipmentSlot equipmentSlot) {
      TransmogItemData data = TransmogItemData.get(stack);
      if (data == null) {
         return false;
      }

      TransmogArmorRenderer<?, ?> renderer = renderer(data.transmog());

      return switch (equipmentSlot) {
         case FEET, LEGS, CHEST -> renderer.hideJacket();
         case HEAD -> renderer.hideHat();
         default -> false;
      };
   }

   public static boolean canUseTransmog(Player player, ItemStack stack) {
      if (stack.isEmpty() || !TransmogItemData.has(stack)) {
         return false;
      }

      if (((ITransmogPreview)player).irons_lib$isTransmogPreview()) {
         return true;
      }

      PatreonPermissions permission = PatreonData.getPatreonPermissions(player);
      TransmogHolder transmogHolder = TransmogItemData.get(stack).transmog();
      return transmogHolder != null && permission.canUse(transmogHolder);
   }

   static {
      TRANSMOGS.put(
         IronsLib.id("red_rogue"),
         new MemoizedSupplier<>(() -> new TransmogArmorRenderer(new GenericArmorModel("irons_lib", "transmog/rogue")).setHideHat().setHideJacket())
      );
      TRANSMOGS.put(
         IronsLib.id("standard_wizard_robes"),
         new MemoizedSupplier<>(() -> new TransmogArmorRenderer(new GenericArmorModel("irons_lib", "transmog/wizard")).setHideJacket().setDyeable())
      );
      TRANSMOGS.put(
         IronsLib.id("standard_wizard_hat"),
         new MemoizedSupplier<>(
            () -> new TransmogArmorRenderer(
                  new GenericArmorModel(IronsLib.id("transmog/wizard_armor_hat"), IronsLib.id("textures/models/armor/transmog/wizard.png"))
               )
               .setHideJacket()
               .setDyeable()
         )
      );
      TRANSMOGS.put(
         IronsLib.id("silver_sorcerer"),
         new MemoizedSupplier<>(() -> new TransmogArmorRenderer(new GenericArmorModel("irons_lib", "transmog/silver_sorcerer")).setDyeable())
      );
      TRANSMOGS.put(
         IronsLib.id("adventurer"),
         new MemoizedSupplier<>(() -> new TransmogArmorRenderer(new GenericArmorModel("irons_lib", "transmog/adventurer")).setDyeable())
      );
      TRANSMOGS.put(
         IronsLib.id("witchhunter"),
         new MemoizedSupplier<>(() -> new TransmogArmorRenderer(new GenericArmorModel("irons_lib", "transmog/witchhunter")).setDyeable().withCapeBone("cape"))
      );
      TRANSMOGS.put(
         IronsLib.id("traveler"),
         new MemoizedSupplier<>(
            () -> new TransmogArmorRenderer(new GenericArmorModel("irons_lib", "transmog/traveler"))
               .setDyeable()
               .withCapeBone(
                  new TransmogArmorRenderer.CapeBone("cape", new Vec3(0.5, 0.5, 0.5)), new TransmogArmorRenderer.CapeBone("leg_cape", new Vec3(1.0, 0.0, 0.0))
               )
         )
      );
      TRANSMOGS.put(
         IronsLib.id("battlemage"),
         new MemoizedSupplier<>(() -> new TransmogArmorRenderer(new GenericArmorModel("irons_lib", "transmog/battlemage")).setDyeable())
      );
      TRANSMOGS.put(
         IronsLib.id("ruby_warrior"),
         new MemoizedSupplier<>(() -> new TransmogArmorRenderer(new GenericArmorModel("irons_lib", "transmog/ruby_warrior")).setHideHat())
      );
      TRANSMOGS.put(
         IronsLib.id("onyx_shadow"),
         new MemoizedSupplier<>(
            () -> new TransmogArmorRenderer(new GenericArmorModel("irons_lib", "transmog/onyx_shadow"))
               .setHideJacket()
               .setHideHat()
               .withCapeBone(new TransmogArmorRenderer.CapeBone("cape", new Vec3(0.85F, 0.85F, 0.85F)))
         )
      );
      TRANSMOGS.put(
         IronsLib.id("sapphire_scholar"),
         new MemoizedSupplier<>(
            () -> new TransmogArmorRenderer(new GenericArmorModel("irons_lib", "transmog/sapphire_scholar"))
               .setHideJacket()
               .setHideHat()
               .withCapeBone(new TransmogArmorRenderer.CapeBone("cape", new Vec3(0.85F, 0.85F, 0.85F)))
               .withCapeBone(new TransmogArmorRenderer.CapeBone("rightArmSleeveCape", new Vec3(0.0, 0.0, 1.5)))
               .withCapeBone(new TransmogArmorRenderer.CapeBone("leftArmSleeveCape", new Vec3(0.0, 0.0, 1.5)))
         )
      );
      TRANSMOGS.put(
         IronsLib.id("autumn_knight"),
         new MemoizedSupplier<>(
            () -> new TransmogArmorRenderer(new GenericArmorModel("irons_lib", "transmog/autumn_knight"))
               .setHideHat()
               .setDyeable()
               .withCapeBone(new TransmogArmorRenderer.CapeBone("cape", new Vec3(0.65F, 0.85F, 0.65F)))
         )
      );
      TRANSMOGS.put(
         IronsLib.id("pumpkin_head_happy"),
         new MemoizedSupplier<>(
            () -> new TransmogArmorRenderer(
                  new GenericArmorModel(IronsLib.id("transmog/pumpkin_head"), IronsLib.id("textures/models/armor/transmog/pumpkin_head_evil_happy.png"))
               )
               .setHideHat()
         )
      );
      TRANSMOGS.put(
         IronsLib.id("pumpkin_head_sad"),
         new MemoizedSupplier<>(
            () -> new TransmogArmorRenderer(
                  new GenericArmorModel(IronsLib.id("transmog/pumpkin_head"), IronsLib.id("textures/models/armor/transmog/pumpkin_head_ghost.png"))
               )
               .setHideHat()
         )
      );
   }
}
