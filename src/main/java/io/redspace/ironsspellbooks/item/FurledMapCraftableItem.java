package io.redspace.ironsspellbooks.item;

import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;

@EventBusSubscriber
public class FurledMapCraftableItem extends FurledMapItem {
    final boolean ancient;
    final FurledMapData mapData;

    public FurledMapCraftableItem(boolean ancient, FurledMapData mapData) {
        // description id is final on Item, so share the base map's translation key through the properties
        super(ItemPropertiesHelper.material().stacksTo(1).overrideDescription(ancient ? "item.irons_spellbooks.furled_map_ancient" : "item.irons_spellbooks.furled_map"));
        this.ancient = ancient;
        this.mapData = mapData;
    }


    @SubscribeEvent
    public static void setMapData(ModifyDefaultComponentsEvent event) {
        // Default component mutation hooks are NeoForge-specific; item instances already
        // carry their map data in this Fabric port path.
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof FurledMapCraftableItem map) {
                // no-op
            }
        }
    }
}
