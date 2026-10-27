package io.redspace.ironslib.internal.registry;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.redspace.ironslib.patreon.PatreonData;
import io.redspace.ironslib.patreon.data.PatreonPermissions;
import io.redspace.ironslib.patreon.data.ProfileResult;
import io.redspace.ironslib.registry.IronsLibRegistries;
import io.redspace.ironslib.soulfire.SoulFireHelper;
import io.redspace.ironslib.statue.PlayerStatuePose;
import io.redspace.ironslib.statue.StatueData;
import io.redspace.ironslib.statue.block.statue_block.player.PlayerStatueBlockEntity;
import io.redspace.ironslib.statue.block.statue_block.player.PlayerStatueBlockItem;
import io.redspace.ironslib.statue.client.StatueTextureManager;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.ClickEvent.OpenFile;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class CommandHandler {
   public static void initialize() {
      CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> register(dispatcher));
   }

   public static void register(CommandDispatcher<CommandSourceStack> pDispatcher) {
      pDispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("irons_lib")
                  .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS)))
               .then(
                  ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("statue")
                           .then(
                              Commands.literal("generate")
                                 .then(
                                    Commands.argument("textures/entity/player/wide/steve.png", IdentifierArgument.id())
                                       .executes(
                                          context -> {
                                             try {
                                                Identifier resource = IdentifierArgument.getId(context, "textures/entity/player/wide/steve.png");
                                                if (!resource.getPath().endsWith(".png")) {
                                                   resource = resource.withSuffix(".png");
                                                }

                                                NativeImage image = NativeImage.read(
                                                   ((Resource)Minecraft.getInstance().getResourceManager().getResource(resource).orElseThrow()).open()
                                                );
                                                String[] split = resource.getPath().split("/");
                                                File file = StatueTextureManager.export(split[split.length - 1], StatueTextureManager.transformTexture(image));
                                                if (file == null) {
                                                   ((CommandSourceStack)context.getSource()).sendFailure(Component.literal("failure"));
                                                   return 0;
                                                } else {
                                                   ((CommandSourceStack)context.getSource())
                                                      .sendSuccess(
                                                         () -> Component.literal("success")
                                                            .withStyle(Style.EMPTY.withUnderlined(true).withClickEvent(new OpenFile(file))),
                                                         true
                                                      );
                                                   return 1;
                                                }
                                             } catch (Exception e) {
                                                ((CommandSourceStack)context.getSource()).sendFailure(Component.literal("failure: " + e.getMessage()));
                                                return 0;
                                             }
                                          }
                                       )
                                 )
                           ))
                        .then(
                           Commands.literal("set_player")
                              .then(
                                 Commands.argument("pos", BlockPosArgument.blockPos())
                                    .then(Commands.argument("username", StringArgumentType.string()).executes(context -> {
                                       String username = StringArgumentType.getString(context, "username");
                                       Optional<GameProfile> optionalProfile = getProfileByUsername(
                                          ((CommandSourceStack)context.getSource()).getServer(), username
                                       );
                                       if (optionalProfile.isPresent()) {
                                          GameProfile profile = optionalProfile.get();
                                          BlockPos blockpos = BlockPosArgument.getBlockPos(context, "pos");
                                          if (((CommandSourceStack)context.getSource()).getLevel().getBlockEntity(blockpos) instanceof PlayerStatueBlockEntity statue
                                             )
                                           {
                                             statue.setPlayerUuid(profile.id());
                                             statue.setChanged();
                                             return 1;
                                          }
                                       }

                                       return 0;
                                    }))
                              )
                        ))
                     .then(
                        Commands.literal("give_all")
                           .executes(
                              context -> {
                                 List<ProfileResult> wizards = PatreonData.getInstance()
                                    .getProfileValues()
                                    .stream()
                                    .filter(profilex -> profilex.permissions().compareTo(PatreonPermissions.Wizard) >= 0)
                                    .toList();

                                 for (ProfileResult profile : wizards) {
                                    ItemStack stack = ((PlayerStatueBlockItem)IronsLibRegistries.ItemRegistry.PLAYER_STATUE_BLOCK_ITEM)
                                       .getDefaultInstance();
                                    StatueData.set(stack, new StatueData(profile.uuid(), PlayerStatuePose.defaultPose(), false));
                                    ((CommandSourceStack)context.getSource()).getPlayer().getInventory().add(stack);
                                 }

                                 return wizards.size();
                              }
                           )
                     )
               ))
            .then(
               Commands.literal("soulfire")
                  .then(
                     Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.argument("seconds", IntegerArgumentType.integer(1, 1000000)).executes(context -> {
                           Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
                           int ticks = IntegerArgumentType.getInteger(context, "seconds") * 20;

                           for (Entity target : targets) {
                              SoulFireHelper.igniteSoul(target, ticks);
                           }

                           int count = targets.size();
                           ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal("Applied soul fire to " + count + " entities"), true);
                           return count;
                        }))
                  )
            )
      );
   }

   public static Optional<GameProfile> getProfileByUsername(MinecraftServer server, String username) {
      return server.services().profileResolver().fetchByName(username);
   }
}
