package io.redspace.ironslib.internal.client;

import io.redspace.ironslib.IronsLib;
import io.redspace.ironslib.internal.client.gui_block_entity_renderer.GuiBlockEntityRenderer;
import io.redspace.ironslib.registry.IronsLibRegistries;
import io.redspace.ironslib.statue.block.statue_block.decorative.DecorativeStatueSpecialRenderer;
import io.redspace.ironslib.statue.block.statue_block.player.PlayerStatueBlockRenderer;
import io.redspace.ironslib.statue.block.statue_block.player.PlayerStatueSpecialRenderer;
import io.redspace.ironslib.transmog.block.transmog_table.TransmogTableScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltInBlockModelsCallback;
import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.block.BuiltInBlockModels;
import net.minecraft.client.renderer.special.SpecialModelRenderers;

public final class ClientSetup implements ClientModInitializer {
   @Override
   public void onInitializeClient() {
      ClientNetworking.initialize();
      ClientEvents.initialize();
      ClientInputEvents.initialize();
      SpecialModelRenderers.ID_MAPPER.put(IronsLib.id("decorative_statue"), DecorativeStatueSpecialRenderer.Unbaked.MAP_CODEC);
      SpecialModelRenderers.ID_MAPPER.put(IronsLib.id("player_statue"), PlayerStatueSpecialRenderer.Unbaked.MAP_CODEC);
      BuiltInBlockModelsCallback.EVENT.register(builder -> builder.put(
         BuiltInBlockModels.special(new PlayerStatueSpecialRenderer.Unbaked()),
         IronsLibRegistries.BlockRegistry.PLAYER_STATUE_BLOCK
      ));
      MenuScreens.register(IronsLibRegistries.MenuRegistry.TRANSMOG_TABLE_MENU, TransmogTableScreen::new);
      PictureInPictureRendererRegistry.register(context -> new GuiBlockEntityRenderer());
      BlockEntityRendererRegistry.register(IronsLibRegistries.BlockRegistry.STATUE_BLOCK_ENTITY, PlayerStatueBlockRenderer::new);
   }
}
