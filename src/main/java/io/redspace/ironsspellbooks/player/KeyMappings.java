package io.redspace.ironsspellbooks.player;

import com.mojang.blaze3d.platform.InputConstants;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.KeyMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Registration happens in {@link io.redspace.ironsspellbooks.IronsSpellbooksClient} through Fabric's key mapping helper.
 * Category labels come from the lang keys {@code key.category.irons_spellbooks.general} and {@code key.category.irons_spellbooks.quick_cast}.
 */
public final class KeyMappings {
    public static final KeyMapping.Category KEY_BIND_GENERAL_CATEGORY = KeyMapping.Category.register(IronsSpellbooks.id("general"));
    public static final KeyMapping.Category KEY_BIND_QUICK_CAST_CATEGORY = KeyMapping.Category.register(IronsSpellbooks.id("quick_cast"));

    public static final ExtendedKeyMapping SPELL_WHEEL_KEYMAP = new ExtendedKeyMapping(getResourceName("spell_wheel"), InputConstants.Type.KEYBOARD, InputConstants.KEY_R, KEY_BIND_GENERAL_CATEGORY);
    public static final ExtendedKeyMapping SPELL_WHEEL_TOGGLE_KEYMAP = new ExtendedKeyMapping(getResourceName("spell_wheel_toggle"), InputConstants.Type.KEYBOARD, InputConstants.UNKNOWN.getValue(), KEY_BIND_GENERAL_CATEGORY);
    public static final ExtendedKeyMapping SPELLBOOK_CAST_ACTIVE_KEYMAP = new ExtendedKeyMapping(getResourceName("spellbook_cast"), InputConstants.Type.KEYBOARD, InputConstants.KEY_V, KEY_BIND_GENERAL_CATEGORY);
    public static final ExtendedKeyMapping SPELLBAR_SCROLL_MODIFIER_KEYMAP = new ExtendedKeyMapping(getResourceName("spell_bar_modifier"), InputConstants.Type.KEYBOARD, InputConstants.KEY_LALT, KEY_BIND_GENERAL_CATEGORY);
    public static final List<ExtendedKeyMapping> QUICK_CAST_MAPPINGS = createQuickCastKeybinds();

    private static String getResourceName(String name) {
        return String.format("key.irons_spellbooks.%s", name);
    }

    private static List<ExtendedKeyMapping> createQuickCastKeybinds() {
        var qcm = new ArrayList<ExtendedKeyMapping>();
        for (int i = 1; i <= 15; i++) {
            qcm.add(new ExtendedKeyMapping(getResourceName(String.format("spell_quick_cast_%d", i)), InputConstants.Type.KEYBOARD, InputConstants.UNKNOWN.getValue(), KEY_BIND_QUICK_CAST_CATEGORY));
        }
        return qcm;
    }
}
