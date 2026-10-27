package io.redspace.ironsspellbooks.gui.overlays;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;


public class ScreenEffectsOverlay implements HudElement {
    public static final ScreenEffectsOverlay instance = new ScreenEffectsOverlay();

    public final static Identifier MAGIC_AURA_TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/gui/overlays/enchanted_ward_vignette.png");
    public final static Identifier HEARTSTOP_TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/gui/overlays/heartstop.png");
    public final static Identifier ICE_BLOCK_TEXTURE = Identifier.withDefaultNamespace("textures/block/ice.png");

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiHelper, DeltaTracker deltaTracker) {
        Player player = Minecraft.getInstance().player;
        if (player == null || Minecraft.getInstance().gui.hud.isHidden() || player.isSpectator()) {
            return;
        }
        var screenWidth = guiHelper.guiWidth();
        var screenHeight = guiHelper.guiHeight();

//        if(!FMLLoader.isProduction()){
//            guiHelper.text(Minecraft.getInstance().font, String.format("ice:   %s", Minecraft.getInstance().player.getAttributeValue(AttributeRegistry.ICE_SPELL_POWER)), 10, 10, 0xFFFFFF);
//            guiHelper.text(Minecraft.getInstance().font, String.format("blood: %s", Minecraft.getInstance().player.getAttributeValue(AttributeRegistry.BLOOD_SPELL_POWER)), 10, 20, 0xFFFFFF);
//        }

        if (player.hasEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(MobEffectRegistry.HEARTSTOP.get()))) {
            renderOverlayAdditive(guiHelper, HEARTSTOP_TEXTURE, 0.25f, 0, 0, .25f, screenWidth, screenHeight);
        }
        if (Minecraft.getInstance().options.getCameraType().isFirstPerson() && player.getRootVehicle().getType().equals(EntityRegistry.ICE_TOMB.get())) {
            renderOverlay(guiHelper, ICE_BLOCK_TEXTURE, 1f, 1f, 1f, .5f, screenWidth, screenHeight);
        }
    }

    // shortcut: no custom additive GUI pipeline yet, so the heartstop vignette alpha-blends instead of adding; add one when it can be checked in game.
    private static void renderOverlayAdditive(GuiGraphicsExtractor gui, Identifier texture, float r, float g, float b, float a, int screenWidth, int screenHeight) {
        renderOverlay(gui, texture, r, g, b, a, screenWidth, screenHeight);
    }

    private static void renderOverlay(GuiGraphicsExtractor gui, Identifier texture, float r, float g, float b, float a, int screenWidth, int screenHeight) {
        gui.blit(RenderPipelines.GUI_TEXTURED, texture, 0, 0, 0f, 0f, screenWidth, screenHeight, screenWidth, screenHeight, ARGB.colorFromFloat(a, r, g, b));
    }
}
