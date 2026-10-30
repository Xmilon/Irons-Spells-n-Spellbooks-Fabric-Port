package io.redspace.ironsspellbooks.gui.overlays;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import org.joml.Vector2ic;
import org.joml.Vector4i;

import java.util.List;
import java.util.Optional;


public class ScreenTooltipOverlay implements HudElement {
    public static final ScreenTooltipOverlay instance = new ScreenTooltipOverlay();

    private record RenderInfo(List<Component> tooltip, ClientTooltipPositioner positioner, Optional<Vector4i> colors) {
    }

    RenderInfo toRender = null;

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiHelper, DeltaTracker deltaTracker) {
        if (Minecraft.getInstance().gui.hud.isHidden() || toRender == null) {
            return;
        }

        // manually render tooltip for extra control (mainly color/opacity)
        //guiHelper.setTooltipForNextFrame(Minecraft.getInstance().font, Language.getInstance().getVisualOrder(toRender.tooltip.stream().map(ScreenTooltipOverlay::cast).toList()), toRender.positioner, 0, 0);
        var font = Minecraft.getInstance().font;
        var components = Language.getInstance().getVisualOrder(toRender.tooltip.stream().map(ScreenTooltipOverlay::cast).toList()).stream().map(ClientTextTooltip::new).toList();
        int maxTextWidth = 0;
        int totalHeight = components.size() == 1 ? -2 : 0;

        for (ClientTooltipComponent clienttooltipcomponent : components) {
            int k = clienttooltipcomponent.getWidth(font);
            if (k > maxTextWidth) {
                maxTextWidth = k;
            }

            totalHeight += clienttooltipcomponent.getHeight(font);
        }

        Vector2ic vector2ic = toRender.positioner.positionTooltip(guiHelper.guiWidth(), guiHelper.guiHeight(), 0, 0, maxTextWidth, totalHeight);
        int x = vector2ic.x();
        int y = vector2ic.y();
        //0xf0100010, 0x505000FF, 0x5028007f
        var bgColor1 = toRender.colors.map(Vector4i::x).orElse(0x90100010);
        var bgColor2 = toRender.colors.map(Vector4i::y).orElse(0x90100010);
        var edgeColor1 = toRender.colors.map(Vector4i::z).orElse(0x705000FF);
        var edgeColor2 = toRender.colors.map(Vector4i::w).orElse(0x7028007f);
        extractColoredBackground(guiHelper, x, y, maxTextWidth, totalHeight, bgColor1, bgColor2, edgeColor1, edgeColor2);
        guiHelper.nextStratum();
        int lineY = y;

        for (int l1 = 0; l1 < components.size(); l1++) {
            ClientTooltipComponent clienttooltipcomponent1 = components.get(l1);
            clienttooltipcomponent1.extractText(guiHelper, font, x, lineY);
            lineY += clienttooltipcomponent1.getHeight(font) + (l1 == 0 ? 2 : 0);
        }


        toRender = null;
    }

    // 26.x tooltips are sprite based; the original gradient background (TooltipRenderUtil#renderTooltipBackground) is rebuilt here to keep custom colors.
    private static void extractColoredBackground(GuiGraphicsExtractor g, int x, int y, int w, int h, int bg1, int bg2, int edge1, int edge2) {
        int left = x - 3;
        int top = y - 3;
        int width = w + 6;
        int height = h + 6;
        g.fill(left, top - 1, left + width, top, bg1);
        g.fill(left, top + height, left + width, top + height + 1, bg2);
        g.fillGradient(left, top, left + width, top + height, bg1, bg2);
        g.fillGradient(left - 1, top, left, top + height, bg1, bg2);
        g.fillGradient(left + width, top, left + width + 1, top + height, bg1, bg2);
        int frameY = top + 1;
        g.fillGradient(left, frameY, left + 1, frameY + height - 2, edge1, edge2);
        g.fillGradient(left + width - 1, frameY, left + width, frameY + height - 2, edge1, edge2);
        g.fill(left, frameY - 1, left + width, frameY, edge1);
        g.fill(left, frameY + height - 2, left + width, frameY + height - 1, edge2);
    }

    private static FormattedText cast(Component component) {
        return component;
    }

    public static void renderTooltip(List<Component> tooltip, ClientTooltipPositioner positioner) {
        instance.toRender = new RenderInfo(tooltip, positioner, Optional.empty());
    }

    /**
     * Color in order of bgColor1
     * bgColor2
     * edgeColor1
     * edgeColor2
     */
    public static void renderTooltip(List<Component> tooltip, ClientTooltipPositioner positioner, Vector4i colors) {
        instance.toRender = new RenderInfo(tooltip, positioner, Optional.of(colors));
    }
}
