package io.redspace.ironsspellbooks.render;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Fabric has no equivalent of NeoForge's separate_transforms model loader. Items that used it have their
 * base model in item/NAME.json plus optional item/NAME_gui.json and item/NAME_head.json, which are
 * swapped in per display context (see ItemRendererMixin).
 */
public final class ItemVariantModels {
    public static final Set<String> GUI = Set.of(
            "amethyst_rapier", "blaze_spell_book", "boreal_blade", "claymore", "copper_spell_book",
            "cursed_doll_spell_book", "decrepit_scythe", "diamond_spell_book", "dragonskin_spell_book",
            "druidic_spell_book", "evoker_spell_book", "firebrand", "gold_spell_book", "hellrazor",
            "ice_spell_book", "iron_spell_book", "keeper_flamberge", "legionnaire_flamberge", "magehunter",
            "misery", "necronomicon_spell_book", "netherite_spell_book", "obsidian_katana", "rotten_spell_book",
            "spellbreaker", "truthseeker", "twilight_gale", "twilight_gale_throwing", "villager_spell_book");
    public static final Set<String> HEAD = Set.of(
            "blaze_spell_book", "chronicle", "copper_spell_book", "cursed_doll_spell_book", "diamond_spell_book",
            "dragonskin_spell_book", "druidic_spell_book", "gold_spell_book", "ice_spell_book", "iron_spell_book",
            "necronomicon_spell_book", "netherite_spell_book", "rotten_spell_book", "villager_spell_book");

    private static final Map<Item, Identifier[]> CACHE = new ConcurrentHashMap<>();
    private static final Identifier[] NONE = new Identifier[2];

    private ItemVariantModels() {
    }

    public static Identifier guiId(String name) {
        return IronsSpellbooks.id("item/" + name + "_gui");
    }

    public static Identifier headId(String name) {
        return IronsSpellbooks.id("item/" + name + "_head");
    }

    /** Returns the model to render for this context, or the given one when the item has no variant. */
    public static BakedModel select(Item item, ItemDisplayContext context, BakedModel model) {
        if (context != ItemDisplayContext.GUI && context != ItemDisplayContext.HEAD) {
            return model;
        }
        var ids = CACHE.computeIfAbsent(item, ItemVariantModels::resolve);
        var id = ids[context == ItemDisplayContext.GUI ? 0 : 1];
        if (id == null) {
            return model;
        }
        var manager = Minecraft.getInstance().getModelManager();
        BakedModel variant = manager.getModel(id);
        return variant == null || variant == manager.getMissingModel() ? model : variant;
    }

    private static Identifier[] resolve(Item item) {
        Identifier key = BuiltInRegistries.ITEM.getKey(item);
        if (!IronsSpellbooks.MODID.equals(key.getNamespace())) {
            return NONE;
        }
        String name = key.getPath();
        return new Identifier[]{GUI.contains(name) ? guiId(name) : null, HEAD.contains(name) ? headId(name) : null};
    }
}
