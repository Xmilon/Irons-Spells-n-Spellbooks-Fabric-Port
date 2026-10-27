package io.redspace.ironsspellbooks.api.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.UUID;

public class BossbarManager {
    public record BossbarSprite(Identifier spriteLocation, int width, int height, int buffer, int yBarOffset) {
    }

    private static final Map<UUID, BossbarSprite> CUSTOM_BARS = new HashMap<>();
    private static int frameBossbarHeight;

    public static void startTracking(UUID uuid, BossbarSprite sprite) {
        CUSTOM_BARS.put(uuid, sprite);
    }

    public static void stopTracking(UUID uuid) {
        CUSTOM_BARS.remove(uuid);
    }

    /**
     * Total vertical space taken by boss bars so far this frame; reset by {@link #beginFrame()}.
     */
    public static int getFrameBossbarHeight() {
        return frameBossbarHeight;
    }

    public static void beginFrame() {
        frameBossbarHeight = 0;
    }

    public static void addFrameHeight(int increment) {
        frameBossbarHeight += increment;
    }

    /**
     * Draws a tracked custom boss bar in place of the vanilla one.
     *
     * @return the vertical increment to advance by, or empty if the bar is not custom and vanilla should draw it
     */
    public static OptionalInt renderCustomBossbar(GuiGraphicsExtractor guiGraphics, LerpingBossEvent bossEvent, int y, int defaultIncrement) {
        BossbarSprite customSprite = CUSTOM_BARS.get(bossEvent.getId());
        if (customSprite == null) {
            return OptionalInt.empty();
        }
        int barY = y + customSprite.yBarOffset;
        int x = (guiGraphics.guiWidth() - customSprite.width) / 2;

        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, customSprite.spriteLocation, customSprite.width, customSprite.height * 2, 0, 0, x, barY, customSprite.width, customSprite.height);
        int progress = Mth.lerpDiscrete(bossEvent.getProgress(), 0, customSprite.width - customSprite.buffer * 2) + customSprite.buffer;
        if (progress > 0) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, customSprite.spriteLocation, customSprite.width, customSprite.height * 2, 0, customSprite.height, x, barY, progress, customSprite.height);
        }

        Component component = bossEvent.getName();
        var font = Minecraft.getInstance().font;
        int textX = guiGraphics.guiWidth() / 2 - font.width(component) / 2;
        int textY = barY - 9 - customSprite.yBarOffset;
        guiGraphics.text(font, component, textX, textY, 16777215);
        return OptionalInt.of(defaultIncrement - 5 + customSprite.height + customSprite.yBarOffset); // 5 is default height
    }
}
