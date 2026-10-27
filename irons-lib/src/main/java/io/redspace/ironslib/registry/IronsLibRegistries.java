package io.redspace.ironslib.registry;

import io.redspace.ironslib.IronsLib;
import io.redspace.ironslib.statue.StatueData;
import io.redspace.ironslib.statue.block.statue_block.player.PlayerStatueBlock;
import io.redspace.ironslib.statue.block.statue_block.player.PlayerStatueBlockEntity;
import io.redspace.ironslib.statue.block.statue_block.player.PlayerStatueBlockItem;
import io.redspace.ironslib.statue.worldgen.RandomizePlayerStatueProcessor;
import io.redspace.ironslib.transmog.TransmogItemData;
import io.redspace.ironslib.transmog.block.transmog_table.TransmogTableBlock;
import io.redspace.ironslib.transmog.block.transmog_table.TransmogTableMenu;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

public final class IronsLibRegistries {
   public static void register() {
      // Initialize every nested registry while registration is still open.
      AttributeRegistry.initialize();
      ComponentRegistry.initialize();
      BlockRegistry.initialize();
      ItemRegistry.initialize();
      MenuRegistry.initialize();
      SoundRegistry.initialize();
      StructureProcessorRegistry.initialize();
   }

   private static <R, T extends R> T register(Registry<R> registry, String name, T value) {
      return Registry.register(registry, IronsLib.id(name), value);
   }

   public static final class AttributeRegistry {
      public static final Holder.Reference<Attribute> ARMOR_PIERCE = register("armor_pierce", 0.0);
      public static final Holder.Reference<Attribute> MINING_SPEED = register("mining_speed", 1.0);
      public static final Holder.Reference<Attribute> EXPERIENCE_GAINED = register("experience_gained", 1.0);
      public static final Holder.Reference<Attribute> ARROW_DAMAGE = register("arrow_damage", 1.0);
      public static final Holder.Reference<Attribute> PROJECTILE_DAMAGE = register("projectile_damage", 1.0);
      public static final Holder.Reference<Attribute> CRIT_DAMAGE = register("crit_damage", 1.0);
      public static final Holder.Reference<Attribute> DODGE_CHANCE = register("dodge_chance", 1.0);
      public static final Holder.Reference<Attribute> HEALING_RECEIVED = register("healing_received", 1.0);
      private static final List<Holder.Reference<Attribute>> ATTRIBUTES = List.of(
         ARMOR_PIERCE, MINING_SPEED, EXPERIENCE_GAINED, ARROW_DAMAGE,
         PROJECTILE_DAMAGE, CRIT_DAMAGE, DODGE_CHANCE, HEALING_RECEIVED
      );

      private static Holder.Reference<Attribute> register(String name, double defaultValue) {
         return Registry.registerForHolder(
            BuiltInRegistries.ATTRIBUTE, IronsLib.id(name),
            new RangedAttribute("attribute.name.irons_lib." + name, defaultValue, 0.0, 1024.0).setSyncable(true)
         );
      }

      private static void initialize() {
         FabricDefaultAttributeRegistry.MODIFY.register(context -> context.modifyAll(
            (entityType, builder) -> ATTRIBUTES.forEach(builder::add)
         ));
      }
   }

   public static final class BlockRegistry {
      public static final TransmogTableBlock TRANSMOG_TABLE = register(
         BuiltInRegistries.BLOCK, "transmog_table",
         new TransmogTableBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.CRAFTING_TABLE)
            .setId(ResourceKey.create(Registries.BLOCK, IronsLib.id("transmog_table"))))
      );
      public static final PlayerStatueBlock PLAYER_STATUE_BLOCK = register(
         BuiltInRegistries.BLOCK, "player_statue",
         new PlayerStatueBlock(Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.STONE).noOcclusion()
            .setId(ResourceKey.create(Registries.BLOCK, IronsLib.id("player_statue"))))
      );
      public static final BlockEntityType<PlayerStatueBlockEntity> STATUE_BLOCK_ENTITY = register(
         BuiltInRegistries.BLOCK_ENTITY_TYPE, "statue",
         new BlockEntityType<>(PlayerStatueBlockEntity::new, Set.of(PLAYER_STATUE_BLOCK))
      );

      private static void initialize() {}
   }

   public static final class ComponentRegistry {
      public static final DataComponentType<TransmogItemData> TRANSMOG = register(
         "transmog", builder -> builder.persistent(TransmogItemData.CODEC)
            .networkSynchronized(TransmogItemData.STREAM_CODEC).cacheEncoding()
      );
      public static final DataComponentType<StatueData> STATUE_ITEM_DATA = register(
         "statue_item_data", builder -> builder.persistent(StatueData.CODEC).cacheEncoding()
      );
      public static final DataComponentType<Identifier> KINETIC_ANIMATION = register(
         "kinetic_animation", builder -> builder.persistent(Identifier.CODEC).networkSynchronized(Identifier.STREAM_CODEC)
      );

      private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
         return Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE, IronsLib.id(name), builder.apply(DataComponentType.builder()).build()
         );
      }

      private static void initialize() {}
   }

   public static final class ItemRegistry {
      public static final PlayerStatueBlockItem PLAYER_STATUE_BLOCK_ITEM = register(
         BuiltInRegistries.ITEM, "player_statue",
         new PlayerStatueBlockItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, IronsLib.id("player_statue"))))
      );
      public static final BlockItem TRANSMOG_TABLE_BLOCK_ITEM = register(
         BuiltInRegistries.ITEM, "transmog_table",
         new BlockItem(BlockRegistry.TRANSMOG_TABLE, new Item.Properties()
            .setId(ResourceKey.create(Registries.ITEM, IronsLib.id("transmog_table"))).useBlockDescriptionPrefix())
      );

      private static void initialize() {}
   }

   public static final class MenuRegistry {
      public static final MenuType<TransmogTableMenu> TRANSMOG_TABLE_MENU = register(
         BuiltInRegistries.MENU, "transmog_menu",
         new ExtendedMenuType<>(TransmogTableMenu::new, BlockPos.STREAM_CODEC)
      );

      private static void initialize() {}
   }

   public static final class SoundRegistry {
      public static final SoundEvent TRANSMOG_TABLE_FORGE = register(
         BuiltInRegistries.SOUND_EVENT, "block.transmog_table.forge",
         SoundEvent.createVariableRangeEvent(IronsLib.id("block.transmog_table.forge"))
      );

      private static void initialize() {}
   }

   public static final class StructureProcessorRegistry {
      public static final com.mojang.serialization.MapCodec<? extends net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor> RANDOMIZE_PLAYER_STATUE_PROCESSOR_TYPE = register(
         BuiltInRegistries.STRUCTURE_PROCESSOR, "randomize_player_statue",
         RandomizePlayerStatueProcessor.MAP_CODEC
      );

      private static void initialize() {}
   }
}
