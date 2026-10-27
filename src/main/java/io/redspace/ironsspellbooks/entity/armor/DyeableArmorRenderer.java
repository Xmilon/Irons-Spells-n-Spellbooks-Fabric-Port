package io.redspace.ironsspellbooks.entity.armor;

import com.geckolib.animatable.GeoItem;
import com.geckolib.model.GeoModel;
import net.minecraft.world.item.Item;

/**
 * The old per-bone dye override only ever returned white (a todo for a default color), so this renders like the generic armor.
 */
public class DyeableArmorRenderer<T extends Item & GeoItem> extends GenericCustomArmorRenderer<T> {
    public DyeableArmorRenderer(GeoModel<T> model) {
        super(model);
    }
}
