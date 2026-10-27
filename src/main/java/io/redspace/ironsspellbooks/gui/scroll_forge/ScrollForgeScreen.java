package io.redspace.ironsspellbooks.gui.scroll_forge;

import net.minecraft.network.chat.FontDescription;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.config.SpellConfigManager;
import io.redspace.ironsspellbooks.api.config.SpellConfigParameter;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.item.InkItem;
import io.redspace.ironsspellbooks.network.ScrollForgeSelectSpellPacket;
import io.redspace.ironsspellbooks.util.ModTags;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ScrollForgeScreen extends AbstractContainerScreen<ScrollForgeMenu> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/gui/scroll_forge.png");
    private static final int SPELL_LIST_X = 89;
    private static final int SPELL_LIST_Y = 15;
    private static final int SCROLL_BAR_X = 199;
    private static final int SCROLL_BAR_Y = 15;
    private static final int SCROLL_BAR_WIDTH = 12;
    private static final int SCROLL_BAR_HEIGHT = 56;
    public static final Identifier RUNIC_FONT = Identifier.withDefaultNamespace("illageralt");
    public static final Identifier ENCHANT_FONT = Identifier.withDefaultNamespace("alt");

    private List<SpellCardInfo> availableSpells;
    private ItemStack[] oldMenuSlots = {ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};

    private AbstractSpell selectedSpell = SpellRegistry.none();
    private int scrollOffset;
    private boolean isScrollbarHeld;

    public ScrollForgeScreen(ScrollForgeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 218, 166);
    }

    @Override
    protected void init() {
        availableSpells = new ArrayList<>();
        generateSpellList();
        super.init();
    }

    @Override
    public void onClose() {
        setSelectedSpell(SpellRegistry.none());
        resetList();
        super.onClose();
    }

    private void resetList() {
        if (!(!menu.getInkSlot().getItem().isEmpty() && (menu.getInkSlot().getItem().getItem() instanceof InkItem inkItem && inkItem.getRarity().compareRarity(selectedSpell.getMinRarityValue()) >= 0)))
            setSelectedSpell(SpellRegistry.none());
        //TODO: reorder setting old focus to test if we actually need to reset the spell... or just give ink its own path since we dont even need to regenerate the list anyways
        //TODO: update: what the fuck does that mean
        scrollOffset = 0;

        for (SpellCardInfo s : availableSpells) {
            removeWidget(s.button);
        }
        availableSpells.clear();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiHelper, int mouseX, int mouseY, float delta) {
        super.extractRenderState(guiHelper, mouseX, mouseY, delta);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiHelper, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(guiHelper, mouseX, mouseY, partialTick);

        guiHelper.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0f, 0f, imageWidth, imageHeight, 256, 256);
        float scrollOffset = Mth.clamp((float) this.scrollOffset / (this.totalRowCount() - 3), 0, 1);
        guiHelper.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos + SCROLL_BAR_X, (int) (topPos + SCROLL_BAR_Y + scrollOffset * (SCROLL_BAR_HEIGHT - 15)), (float) (imageWidth + (isScrollbarHeld ? 12 : 0)), 0f, 12, 15, 256, 256);
//        if (lastFocusItem != menu.getFocusSlot().getItem()) {
//            generateSpellList();
//            lastFocusItem = menu.getFocusSlot().getItem();
//        }
        if (menuSlotsChanged())
            generateSpellList();
        renderSpellList(guiHelper, partialTick, mouseX, mouseY);
        //irons_spellbooks.LOGGER.debug("{}", this.menu.getFocusSlot().getItem().getItem().toString());

    }

    private boolean menuSlotsChanged() {
        if (menu.getInkSlot().getItem().getItem() != oldMenuSlots[0].getItem() || /*menu.getBlankScrollSlot().getItem().getItem() != oldMenuSlots[1].getItem() || */menu.getFocusSlot().getItem().getItem() != oldMenuSlots[2].getItem()) {
            oldMenuSlots = new ItemStack[]{
                    menu.getInkSlot().getItem(),
                    menu.getBlankScrollSlot().getItem(),
                    menu.getFocusSlot().getItem()
            };
            return true;
        } else
            return false;
    }

    private void renderSpellList(GuiGraphicsExtractor guiHelper, float partialTick, int mouseX, int mouseY) {
        ItemStack inkStack = menu.getInkSlot().getItem();

        SpellRarity inkRarity = getRarityFromInk(inkStack.getItem());

        availableSpells.sort((a, b) -> SpellConfigManager.getSpellConfigValue(a.spell, SpellConfigParameter.MIN_RARITY).compareRarity(SpellConfigManager.getSpellConfigValue(b.spell, SpellConfigParameter.MIN_RARITY)));

        List<FormattedCharSequence> additionalTooltip = null;
        for (int i = 0; i < availableSpells.size(); i++) {
            SpellCardInfo spellCard = availableSpells.get(i);

            if (i - scrollOffset >= 0 && i - scrollOffset < 3) {
                if (inkRarity == null || spellCard.spell.getMinRarityValue().getValue() > inkRarity.getValue()) {
                    spellCard.activityState = SpellCardInfo.ActivityState.INK_ERROR;
                } else if (minecraft != null && !spellCard.spell.canBeCraftedBy(minecraft.player)) {
                    spellCard.activityState = SpellCardInfo.ActivityState.UNLEARNED_ERROR;
                } else {
                    spellCard.activityState = SpellCardInfo.ActivityState.ENABLED;
                }
                int x = leftPos + SPELL_LIST_X;
                int y = topPos + SPELL_LIST_Y + (i - scrollOffset) * 19;
                spellCard.button.setX(x);
                spellCard.button.setY(y);
                spellCard.draw(this, guiHelper, x, y, mouseX, mouseY);
                if (additionalTooltip == null)
                    additionalTooltip = spellCard.getTooltip(x, y, mouseX, mouseY);
            } else {
                spellCard.activityState = SpellCardInfo.ActivityState.DISABLED;
            }
            spellCard.button.active = spellCard.activityState == SpellCardInfo.ActivityState.ENABLED;
        }

        if (additionalTooltip != null) {
            guiHelper.setTooltipForNextFrame(font, additionalTooltip, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        int length = availableSpells.size();
        int newScroll = scrollOffset - (int) pScrollY;
        if (newScroll <= length - 3 && newScroll >= 0) {
            scrollOffset -= (int) pScrollY;
            return true;
        } else {
            return false;
        }
    }

    public void generateSpellList() {
        this.resetList();

        ItemStack focusStack = menu.getFocusSlot().getItem();
        if (!focusStack.isEmpty() && focusStack.is(ModTags.SCHOOL_FOCUS)) {
            var spells = SchoolRegistry
                    .getSchoolsFromFocus(focusStack).stream()
                    .flatMap(school -> SpellRegistry.getSpellsForSchool(school).stream())
                    .filter(AbstractSpell::allowCrafting)
                    .toList();
            for (int i = 0; i < spells.size(); i++) {
                int tempIndex = i;
                if (spells.get(i).isEnabled() && minecraft != null)
                    availableSpells.add(new SpellCardInfo(spells.get(i), i + 1, i, this.addWidget(
                            new Button.Builder(spells.get(i).getDisplayName(minecraft.player), (b) -> this.setSelectedSpell(spells.get(tempIndex))).pos(0, 0).size(108, 19).build()
                    )));
            }
        }
    }

    private void setSelectedSpell(AbstractSpell spell) {
        selectedSpell = spell;
        PacketDistributor.sendToServer(new ScrollForgeSelectSpellPacket(this.menu.blockEntity.getBlockPos(), spell.getSpellId()));
    }

    private SpellRarity getRarityFromInk(Item ink) {
        if (ink instanceof InkItem inkItem) {
            return inkItem.getRarity();
        } else {
            return null;
        }
    }

    public AbstractSpell getSelectedSpell() {
        return selectedSpell;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        isScrollbarHeld = isHovering(SCROLL_BAR_X, SCROLL_BAR_Y, 12, 56, event.x(), event.y());
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        isScrollbarHeld = false;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double pDragX, double pDragY) {
        int i = this.totalRowCount() - 3;
        if (this.isScrollbarHeld) {
            int j = this.topPos + SCROLL_BAR_Y;
            int k = j + SCROLL_BAR_HEIGHT;
            var scrollOffs = ((float) event.y() - (float) j - 7.5F) / ((float) (k - j) - 15.0F);
            scrollOffs = Mth.clamp(scrollOffs, 0.0F, 1.0F);
            this.scrollOffset = Math.max((int) ((double) (scrollOffs * (float) i) + 0.5D), 0);
            return true;
        } else {
            return super.mouseDragged(event, pDragX, pDragY);
        }
    }

    private int totalRowCount() {
        return this.availableSpells.size();
    }

    private class SpellCardInfo {
        enum ActivityState {
            DISABLED,
            ENABLED,
            INK_ERROR,
            UNLEARNED_ERROR
        }

        ActivityState activityState = ActivityState.DISABLED;
        AbstractSpell spell;
        int spellLevel;
        SpellRarity rarity;
        Button button;
        int index;

        SpellCardInfo(AbstractSpell spell, int spellLevel, int index, Button button) {
            this.spell = spell;
            this.spellLevel = spellLevel;
            this.index = index;
            this.button = button;
            this.rarity = spell.getRarity(spellLevel);
        }

        void draw(ScrollForgeScreen screen, GuiGraphicsExtractor guiHelper, int x, int y, int mouseX, int mouseY) {
            if (this.activityState == ActivityState.ENABLED || this.activityState == ActivityState.UNLEARNED_ERROR) {
                //Draw with highlighted or regular color
                if (spell == screen.getSelectedSpell())
                    guiHelper.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0f, 204f, 108, 19, 256, 256);
                else
                    guiHelper.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0f, 166f, 108, 19, 256, 256);
            } else {
                //"hidden" color
                guiHelper.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0f, 185f, 108, 19, 256, 256);
            }
            var texture = (this.activityState == ActivityState.ENABLED ? spell.getSpellIconResource() : SpellRegistry.none().getSpellIconResource());
            guiHelper.blit(RenderPipelines.GUI_TEXTURED, texture, x + 108 - 18, y + 1, 0f, 0f, 16, 16, 16, 16);

            int maxWidth = 108 - 20;
            var text = trimText(font, getDisplayName().withStyle(this.activityState == ActivityState.ENABLED ? Style.EMPTY : Style.EMPTY.withFont(new FontDescription.Resource(RUNIC_FONT))), maxWidth);
            int textX = x + 2;
            int textY = y + 3;
            guiHelper.textWithWordWrap(font, text, textX, textY, maxWidth, 0xFFFFFF);
        }

        @Nullable
        List<FormattedCharSequence> getTooltip(int x, int y, int mouseX, int mouseY) {
            var text = getDisplayName();
            int textX = x + 2;
            int textY = y + 3;
            if (mouseX >= textX && mouseY >= textY && mouseX < textX + font.width(text) && mouseY < textY + font.lineHeight) {
                return getHoverText();
            } else {
                return null;
            }
        }

        List<FormattedCharSequence> getHoverText() {
            if (this.activityState == ActivityState.INK_ERROR) {
                return List.of(FormattedCharSequence.forward(Component.translatable("ui.irons_spellbooks.ink_rarity_error").getString(), Style.EMPTY));
            } else if (this.activityState == ActivityState.UNLEARNED_ERROR) {
                return List.of(FormattedCharSequence.forward(this.spell.getLockedMessage().getString(), this.spell.getLockedMessage().getStyle()));
            } else {
                return TooltipsUtils.createSpellDescriptionTooltip(this.spell, font);
            }
        }

        private FormattedText trimText(Font font, Component component, int maxWidth) {
            var text = font.getSplitter().splitLines(component, maxWidth, component.getStyle()).get(0);
            if (text.getString().length() < component.getString().length())
                text = FormattedText.composite(text, FormattedText.of("..."));
            return text;
        }

        MutableComponent getDisplayName() {
            return spell.getDisplayName(minecraft.player);
        }
    }
}
