package io.redspace.ironsspellbooks.gui.overlays;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.resources.Identifier;


public class CastBarOverlay implements HudElement {
    public static CastBarOverlay instance = new CastBarOverlay();

    public final static Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/gui/icons.png");
    static final int IMAGE_WIDTH = 54;
    static final int COMPLETION_BAR_WIDTH = 44;
    static final int IMAGE_HEIGHT = 21;

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiHelper, DeltaTracker deltaTracker) {
        if (Minecraft.getInstance().gui.hud.isHidden() || Minecraft.getInstance().player.isSpectator()) {
            return;
        }
        var screenWidth = guiHelper.guiWidth();
        var screenHeight = guiHelper.guiHeight();
        if (!ClientMagicData.isCasting() || ClientMagicData.isCasting() && ClientMagicData.getCastType() == CastType.INSTANT) {
            return;
        }


        float castCompletionPercent = ClientMagicData.getCastCompletionPercent();
        String castTimeString = Utils.timeFromTicks((1 - castCompletionPercent) * ClientMagicData.getCastDuration(), 1);
        if (ClientMagicData.getCastType() == CastType.CONTINUOUS) {
            castCompletionPercent = 1 - castCompletionPercent;
        }

        int barX, barY;
        barX = screenWidth / 2 - IMAGE_WIDTH / 2;
        barY = screenHeight / 2 + screenHeight / 8;

        //RenderSystem.setShader(GameRenderer::getPositionTexShader);
        //RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        //RenderSystem.setShaderTexture(0, TEXTURE);

        guiHelper.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, barX, barY, 0f, (float) (IMAGE_HEIGHT * 2), IMAGE_WIDTH, IMAGE_HEIGHT, 256, 256);
        guiHelper.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, barX, barY, 0f, (float) (IMAGE_HEIGHT * 3), (int) (COMPLETION_BAR_WIDTH * castCompletionPercent + (IMAGE_WIDTH - COMPLETION_BAR_WIDTH) / 2), IMAGE_HEIGHT, 256, 256);

        int textX, textY;
        var font = Minecraft.getInstance().font;

        textX = barX + (IMAGE_WIDTH - font.width(castTimeString)) / 2;
        textY = barY + IMAGE_HEIGHT / 2 - font.lineHeight / 2 + 1;

        guiHelper.text(font, castTimeString, textX, textY, 0xFFFFFF);
    }
}
