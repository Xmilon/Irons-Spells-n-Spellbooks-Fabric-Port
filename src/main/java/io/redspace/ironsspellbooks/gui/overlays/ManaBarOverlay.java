package io.redspace.ironsspellbooks.gui.overlays;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.config.ClientConfigs;
import io.redspace.ironsspellbooks.item.CastingItem;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import static io.redspace.ironsspellbooks.api.registry.AttributeRegistry.MAX_MANA;

public class ManaBarOverlay implements HudElement {
    public static final ManaBarOverlay instance = new ManaBarOverlay();

    public final static Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/gui/icons.png");

    //public final static Identifier EMPTY = Identifier.fromNamespaceAndPath(irons_spellbooks.MODID,"textures/gui/health_empty.png");
    //public final static Identifier FULL = Identifier.fromNamespaceAndPath(irons_spellbooks.MODID,"textures/gui/health_full.png");
    public enum Anchor {
        Hunger,
        XP,
        Center,
        TopLeft,
        TopRight,
        BottomLeft,
        BottomRight
    }

    public enum Display {
        Never,
        Always,
        Contextual
    }

    static final int DEFAULT_IMAGE_WIDTH = 98;
    static final int XP_IMAGE_WIDTH = 188;
    static final int IMAGE_HEIGHT = 21;
    static final int HOTBAR_HEIGHT = 25;
    static final int ICON_ROW_HEIGHT = 11;
    static final int CHAR_WIDTH = 6;
    static final int HUNGER_BAR_OFFSET = 50;
    static final int SCREEN_BORDER_MARGIN = 20;
    static final int TEXT_COLOR = 0xFF55FFFF;

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiHelper, DeltaTracker deltaTracker) {
        var minecraft = Minecraft.getInstance();
        var player = minecraft.player;
        if (minecraft.gui.hud.isHidden() || player == null || player.isSpectator()) {
            return;
        }
        var screenWidth = guiHelper.guiWidth();
        var screenHeight = guiHelper.guiHeight();
        if (!shouldShowManaBar(player))
            return;

        int maxMana = getPlayerMaxMana(player);
        int mana = ClientMagicData.getPlayerMana();
        if (maxMana <= 0) {
            return;
        }
        int barX, barY;
        //TODO: cache these?
        int configOffsetY = ClientConfigs.safeGet(ClientConfigs.MANA_BAR_Y_OFFSET);
        int configOffsetX = ClientConfigs.safeGet(ClientConfigs.MANA_BAR_X_OFFSET);
        Anchor anchor = ClientConfigs.safeGet(ClientConfigs.MANA_BAR_ANCHOR);
        if (anchor == Anchor.XP && player.getJumpRidingScale() > 0) //Hide XP Mana bar when actively jumping on a horse
            return;
        barX = getBarX(anchor, screenWidth) + configOffsetX;
        barY = getBarY(anchor, screenHeight, player) - configOffsetY;

        //FIXME: while we do not have to set the texture, we do have to set the shader (mainly for transparency)
        //RenderSystem.setShader(GameRenderer::getPositionTexShader);
        //RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        //RenderSystem.setShaderTexture(0, TEXTURE);

        int imageWidth = anchor == Anchor.XP ? XP_IMAGE_WIDTH : DEFAULT_IMAGE_WIDTH;
        int spriteX = anchor == Anchor.XP ? 68 : 0;
        int spriteY = anchor == Anchor.XP ? 40 : 0;
        guiHelper.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, barX, barY, (float) (spriteX), (float) (spriteY), imageWidth, IMAGE_HEIGHT, 256, 256);
        guiHelper.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, barX, barY, (float) (spriteX), (float) (spriteY + IMAGE_HEIGHT), (int) (imageWidth * Math.min((mana / (double) maxMana), 1)), IMAGE_HEIGHT, 256, 256);

        int textX, textY;
        String manaFraction = (mana) + "/" + maxMana;

        textX = ClientConfigs.safeGet(ClientConfigs.MANA_TEXT_X_OFFSET) + barX + imageWidth / 2 - (int) ((("" + mana).length() + 0.5) * CHAR_WIDTH);
        textY = ClientConfigs.safeGet(ClientConfigs.MANA_TEXT_Y_OFFSET) + barY + (anchor == Anchor.XP ? ICON_ROW_HEIGHT / 3 : ICON_ROW_HEIGHT);

        if (ClientConfigs.safeGet(ClientConfigs.MANA_BAR_TEXT_VISIBLE)) {
            guiHelper.text(Minecraft.getInstance().font, manaFraction, textX, textY, TEXT_COLOR);
            //gui.getFont().draw(poseStack, manaFraction, textX, textY, TEXT_COLOR);
        }
    }

    public static boolean shouldShowManaBar(Player player) {
        if (player == null) {
            return false;
        }
        //We show mana if they are holding an item that can cast spells or if their mana is not full
        var display = ClientConfigs.safeGet(ClientConfigs.MANA_BAR_DISPLAY);
        return !player.isSpectator() && display != Display.Never &&
                (display == Display.Always || player.isHolding(itemStack -> itemStack.getItem() instanceof CastingItem || (ISpellContainer.isSpellContainer(itemStack) && !ISpellContainer.get(itemStack).mustEquip())) || ClientMagicData.getPlayerMana() < getPlayerMaxMana(player));

    }

    // the server sends its own max mana (attributes + equipment); the client-side attribute alone can be stale
    private static int getPlayerMaxMana(Player player) {
        int synced = ClientMagicData.getSyncedMaxMana();
        return synced > 0 ? synced : (int) io.redspace.ironsspellbooks.api.registry.AttributeRegistry.getMaxManaWithFallback(player);
    }

    private static int getBarX(Anchor anchor, int screenWidth) {
        if (anchor == Anchor.XP)
            return screenWidth / 2 - 91 - 3; //Vanilla's Pos - 3
        if (anchor == Anchor.Hunger || anchor == Anchor.Center)
            return screenWidth / 2 - DEFAULT_IMAGE_WIDTH / 2 + (anchor == Anchor.Center ? 0 : HUNGER_BAR_OFFSET);
        else if (anchor == Anchor.TopLeft || anchor == Anchor.BottomLeft)
            return SCREEN_BORDER_MARGIN;
        else return screenWidth - SCREEN_BORDER_MARGIN - DEFAULT_IMAGE_WIDTH;

    }

    private static int getBarY(Anchor anchor, int screenHeight, Player player) {
        if (anchor == Anchor.XP)
            return screenHeight - 32 + 3 - 7; //Vanilla's Pos - 7
        if (anchor == Anchor.Hunger)
            return screenHeight - (getRightHeight(player) - 2) - IMAGE_HEIGHT / 2;
        if (anchor == Anchor.Center)
            return screenHeight - HOTBAR_HEIGHT - (int) (ICON_ROW_HEIGHT * 2.5f) - IMAGE_HEIGHT / 2 - (Math.max(getRightHeight(player), getLeftHeight(player)) - 49);
        if (anchor == Anchor.TopLeft || anchor == Anchor.TopRight)
            return SCREEN_BORDER_MARGIN;
        return screenHeight - SCREEN_BORDER_MARGIN - IMAGE_HEIGHT;

    }

    // Gui#rightHeight / #leftHeight were a NeoForge bookkeeping field; these rebuild what it held when the mana bar drew.
    private static int getRightHeight(Player player) {
        int height = 39 + 10; //food (or mount health) row
        if (player.isEyeInFluid(FluidTags.WATER) || player.getAirSupply() < player.getMaxAirSupply()) {
            height += 10; //air bubbles
        }
        return height;
    }

    private static int getLeftHeight(Player player) {
        int healthRows = Mth.ceil((Mth.ceil(player.getMaxHealth()) + Mth.ceil(player.getAbsorptionAmount())) / 2f / 10f);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);
        int height = 39 + healthRows * rowHeight;
        if (rowHeight != 10) {
            height += 10 - rowHeight;
        }
        if (player.getArmorValue() > 0) {
            height += 10;
        }
        return height;
    }
}
