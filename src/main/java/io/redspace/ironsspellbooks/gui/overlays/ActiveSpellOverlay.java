package io.redspace.ironsspellbooks.gui.overlays;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.item.CastingItem;
import io.redspace.ironsspellbooks.item.Scroll;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;


public class ActiveSpellOverlay implements HudElement {
    public static ActiveSpellOverlay instance = new ActiveSpellOverlay();

    protected static final Identifier WIDGETS_LOCATION = Identifier.withDefaultNamespace("textures/gui/widgets.png");
    public final static Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/gui/icons.png");
    protected static final Identifier FRAME_LOCATION = Identifier.withDefaultNamespace("textures/gui/sprites/hud/hotbar_offhand_left.png");

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiHelper, DeltaTracker deltaTracker) {
        var minecraft = Minecraft.getInstance();
        var player = minecraft.player;
        if (minecraft.gui.hud.isHidden() || player == null || player.isSpectator()) {
            return;
        }
        var screenWidth = guiHelper.guiWidth();
        var screenHeight = guiHelper.guiHeight();
        ItemStack stack = player.getMainHandItem();
        var spellSelectionManager = ClientMagicData.getSpellSelectionManager();
        AbstractSpell spell;
        if (hasRightClickCasting(stack.getItem())) {
            if (ISpellContainer.isSpellContainer(stack)) {
                spell = ISpellContainer.get(stack).getSpellAtIndex(0).getSpell();
            } else {
                if (spellSelectionManager == null) {
                    return;
                }
                spell = spellSelectionManager.getSelectedSpellData().getSpell();
            }
        } else {
            stack = player.getOffhandItem();
            if (hasRightClickCasting(stack.getItem())) {
                if (ISpellContainer.isSpellContainer(stack)) {
                    spell = ISpellContainer.get(stack).getSpellAtIndex(0).getSpell();
                } else {
                    if (spellSelectionManager == null) {
                        return;
                    }
                    spell = spellSelectionManager.getSelectedSpellData().getSpell();
                }
            } else {
                return;
            }
        }
        if (stack.isEmpty() || spell == SpellRegistry.none()) {
            return;
        }

        int centerX, centerY;
        //GUI:522 (offhand slot location)
        centerX = screenWidth / 2 + 91 + 9;// + 29;
        centerY = screenHeight - 23;

        //
        //  Render Spells
        //
        //Slot Border
        guiHelper.blit(RenderPipelines.GUI_TEXTURED, FRAME_LOCATION, centerX, centerY, 0f, 0f, 29, 24, 29, 24);
        //Spell Icon
        guiHelper.blit(RenderPipelines.GUI_TEXTURED, spell.getSpellIconResource(), centerX + 3, centerY + 4, 0f, 0f, 16, 16, 16, 16);
        //Border + Cooldowns
        float f = ClientMagicData.getCooldownPercent(spell);
        if (f > 0 && !stack.getItem().equals(ItemRegistry.SCROLL.get())) {
            int pixels = (int) (16 * f + 1f);
            guiHelper.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, centerX + 3, centerY + 20 - pixels, 47f, 87f, 16, pixels, 256, 256);
        }
    }

    private static boolean hasRightClickCasting(Item item) {
        //TODO: check configs when implemented
        return item instanceof Scroll || item instanceof CastingItem;
    }
}
