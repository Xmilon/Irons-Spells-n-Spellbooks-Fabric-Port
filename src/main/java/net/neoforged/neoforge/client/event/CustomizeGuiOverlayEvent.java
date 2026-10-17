package net.neoforged.neoforge.client.event;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.BossEvent;
import net.neoforged.bus.api.Event;

public class CustomizeGuiOverlayEvent extends Event {
    public static class BossEventProgress extends CustomizeGuiOverlayEvent {
        private int increment;
        private final int y;
        private final GuiGraphics guiGraphics;
        private final BossEvent bossEvent;

        public BossEventProgress(GuiGraphics guiGraphics, BossEvent bossEvent, int y, int increment) {
            this.guiGraphics = guiGraphics;
            this.bossEvent = bossEvent;
            this.y = y;
            this.increment = increment;
        }

        public int getIncrement(){ return increment; }
        public void setIncrement(int increment){ this.increment = increment; }
        public int getY() { return y; }
        public GuiGraphics getGuiGraphics() { return guiGraphics; }
        public BossEvent getBossEvent() { return bossEvent; }
    }
}
