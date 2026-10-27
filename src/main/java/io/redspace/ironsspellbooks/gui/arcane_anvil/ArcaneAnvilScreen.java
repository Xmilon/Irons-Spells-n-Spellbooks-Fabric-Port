package io.redspace.ironsspellbooks.gui.arcane_anvil;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class ArcaneAnvilScreen extends ItemCombinerScreen<ArcaneAnvilMenu> {
    private static final Identifier ANVIL_LOCATION = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/gui/arcane_anvil.png");

    public ArcaneAnvilScreen(ArcaneAnvilMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, ANVIL_LOCATION);
        this.titleLabelX = 48;
        this.titleLabelY = 24;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiHelper, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(guiHelper, mouseX, mouseY, partialTick);
        // X over arrow
        if (((this.menu.getSlot(0).hasItem() && this.menu.getSlot(1).hasItem()) && !this.menu.getSlot(2).hasItem())) {
            guiHelper.blit(RenderPipelines.GUI_TEXTURED, ANVIL_LOCATION, this.leftPos + 99, this.topPos + 45, (float) (this.imageWidth), 0f, 28, 21, 256, 256);
        }
    }

    @Override
    protected void extractErrorIcon(GuiGraphicsExtractor guiHelper, int x, int y) {
    }

}
