package io.redspace.ironsspellbooks.gui.overlays;

import com.mojang.renderpearl.api.pipeline.BlendFactor;
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
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

        if (player.hasEffect(MobEffectRegistry.HEARTSTOP)) {
            renderOverlayAdditive(guiHelper, HEARTSTOP_TEXTURE, 0.25f, 0, 0, .25f, screenWidth, screenHeight);
        }
        if (Minecraft.getInstance().options.getCameraType().isFirstPerson() && player.getRootVehicle().getType().equals(EntityRegistry.ICE_TOMB.get())) {
            renderOverlay(guiHelper, ICE_BLOCK_TEXTURE, 1f, 1f, 1f, .5f, screenWidth, screenHeight);
        }
    }

    private static final RenderPipeline ADDITIVE_GUI_PIPELINE = RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "pipeline/gui_additive"))
            .withColorTargetState(new ColorTargetState(new BlendFunction(BlendFactor.ONE, BlendFactor.ONE, BlendFactor.ONE, BlendFactor.ONE)))
            .build();

    private static void renderOverlayAdditive(GuiGraphicsExtractor gui, Identifier texture, float r, float g, float b, float a, int screenWidth, int screenHeight) {
        gui.blit(ADDITIVE_GUI_PIPELINE, texture, 0, 0, 0f, 0f, screenWidth, screenHeight, screenWidth, screenHeight, ARGB.colorFromFloat(a, r, g, b));
    }

    private static void renderOverlay(GuiGraphicsExtractor gui, Identifier texture, float r, float g, float b, float a, int screenWidth, int screenHeight) {
        gui.blit(RenderPipelines.GUI_TEXTURED, texture, 0, 0, 0f, 0f, screenWidth, screenHeight, screenWidth, screenHeight, ARGB.colorFromFloat(a, r, g, b));
    }
}
